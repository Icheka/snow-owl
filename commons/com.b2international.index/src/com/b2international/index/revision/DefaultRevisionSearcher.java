/*
 * Copyright 2011-2023 B2i Healthcare, https://b2ihealthcare.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.index.revision;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.b2international.commons.metric.Metrics;
import com.b2international.index.Hits;
import com.b2international.index.Searcher;
import com.b2international.index.aggregations.Aggregation;
import com.b2international.index.aggregations.AggregationBuilder;
import com.b2international.index.es.EsDocumentSearcher;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Knn;
import com.b2international.index.query.Query;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @since 4.7
 */
public final class DefaultRevisionSearcher implements RevisionSearcher {

	private final RevisionBranchRef branch;
	private final Searcher searcher;
	private final int maxTermsCount;

	public DefaultRevisionSearcher(RevisionBranchRef branch, Searcher searcher) {
		this.branch = branch;
		this.searcher = searcher;
		this.maxTermsCount = ((EsDocumentSearcher) searcher).maxTermsCount();
	}
	
	@Override
	public Searcher searcher() {
		return searcher;
	}
	
	@Override
	public void setMetrics(Metrics metrics) {
		searcher.setMetrics(metrics);
	}
	
	@Override
	public <T> T get(Class<T> type, String key) throws IOException {
		if (Revision.class.isAssignableFrom(type)) {
			final Query<T> query = Query.select(type).where(Expressions.exactMatch(Revision.Fields.ID, key)).limit(2).build();
			return Iterables.getOnlyElement(search(query), null);
		} else {
			return searcher.get(type, key);
		}
	}
	
	@Override
	public <T> Iterable<T> get(Class<T> type, Iterable<String> keys) throws IOException {
		if (Iterables.isEmpty(keys)) {
			return Collections.emptySet();
		} else if (Revision.class.isAssignableFrom(type)) {
			List<String> allKeys = ImmutableList.copyOf(keys);
			if (allKeys.size() > maxTermsCount) {
				List<T> results = Lists.newArrayListWithExpectedSize(allKeys.size());
				for (List<String> currentKeys : Lists.partition(allKeys, maxTermsCount)) {
					results.addAll(search(Query.select(type).where(Expressions.matchAny(Revision.Fields.ID, currentKeys)).limit(currentKeys.size()).build()).getHits());
				}
				return results;
			} else {
				return search(Query.select(type).where(Expressions.matchAny(Revision.Fields.ID, allKeys)).limit(allKeys.size()).build());
			}
		} else {
			return searcher.get(type, keys);
		}
	}

	@Override
	public <T> Hits<T> search(Query<T> query) throws IOException {
		if (query.isRevisionQuery()) {
			if (query.getSelection().getParentScope() == null) {
				// rewrite query if we are looking for revision, otherwise if we are looking for unversioned nested use it as is
				query = query.withFilter(getRevisionFilter()).build();
			} else {
				checkArgument(Revision.class.isAssignableFrom(query.getSelection().getParentScope()), "Searching non-revision documents require a revision parent type: %s", query);
				// run a query on the parent documents with nested match on the children
				query = query.withFilter(Expressions.hasParent(query.getSelection().getParentScope(), getRevisionFilter())).build();
			}
		}
		return searcher.search(query);
	}

	@Override
	public <T> Aggregation<T> aggregate(AggregationBuilder<T> aggregation) throws IOException {
		aggregation.query(Expressions.bool()
				.filter(aggregation.getQuery())
				.filter(getRevisionFilter())
			.build());
		return searcher.aggregate(aggregation);
	}
	
	@Override
	public <T> Hits<T> knn(Knn<T> knn) throws IOException {
		return searcher.knn(knn.withFilter(Expressions.bool()
				.filter(knn.getFilter())
				.filter(getRevisionFilter())
			.build()));
	}
	
	@Override
	public String branch() {
		return branch.path();
	}
	
	@Override
	public RevisionBranchRef ref() {
		return branch;
	}
	
	public final Expression getRevisionFilter() {
		return branch.toRevisionFilter();
	}
		
}
