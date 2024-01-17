/*
 * Copyright 2017-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.request;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.b2international.index.Hits;
import com.b2international.index.Searcher;
import com.b2international.index.query.*;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.uri.ResourceURIPathResolver.PathWithVersion;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * @since 5.11
 * @param <C> - the required execution context of this request
 * @param <B> - the return type
 * @param <D> - the document type to search for 
 */
public abstract class SearchIndexResourceRequest<C extends ServiceProvider, B, D> extends SearchResourceRequest<C, B> {

	private static final long serialVersionUID = 1L;

	/**
	 * Special field name for sorting based on the document score (relevance).
	 */
	public static final SortField SCORE = Sort.fieldDesc(SortBy.FIELD_SCORE);
	
	@Override
	protected final B doExecute(C context) throws IOException {
		final Searcher searcher = searcher(context);
		final Expression where = prepareQuery(context);
		
		// configure additional fields to load when subsetting the response
		List<String> fields = fields();
		if (!fields().isEmpty()) {
			fields = configureFieldsToLoad(fields);
		}
		
		final KnnFilter knnFilter = getKnnFilter();
		final String knnField = getKnnField();
		final Hits<D> hits; 
		
		if (knnFilter != null && knnField != null) {
			hits = searcher.knn(Knn.select(getSelect())
					.field(getKnnField())
					.k(limit())
					.filter(where)
					.numCandidates(knnFilter.getNumCandidates())
					.queryVector(knnFilter.getQueryVector())
					.build());
		} else {
			hits = searcher.search(Query.select(getSelect())
					.from(getFrom())
					.fields(fields)
					.where(where)
					.searchAfter(searchAfter())
					.limit(limit())
					.sortBy(querySortBy(context))
					.withScores(trackScores())
					.cached(cacheHits(context))
					.build());
		}
		
		return toCollectionResource(context, hits);
	}

	protected String getKnnField() {
		return null;
	}

	/**
	 * Subclasses may choose to support knn filtering by providing a knn filter here. Please note that this API is experimental and subject to change. Especially as the underlying API on the Elasticsearch side is also marked as experimental.
	 * 
	 * @return a {@link KnnFilter} to perform knn filtering
	 */
	protected KnnFilter getKnnFilter() {
		return null;
	}

	private final List<String> configureFieldsToLoad(List<String> fields) {
		final Set<String> fieldsToLoad = new LinkedHashSet<>(fields);
		
		collectAdditionalFieldsToFetch(fieldsToLoad);
		
		// perform field replacements between known model and index fields, if specified by the subclass
		final Multimap<String, String> fieldReplacements = collectFieldsToLoadReplacements();
		if (!fieldReplacements.isEmpty()) {
			for (String fieldToLoad : List.copyOf(fieldsToLoad)) {
				Collection<String> replacements = fieldReplacements.get(fieldToLoad);
				if (!replacements.isEmpty()) {
					fieldsToLoad.remove(fieldToLoad);
					fieldsToLoad.addAll(replacements);
				}
			}
		}
		
		// in case of Revisions always include the ID field (if not requested) to avoid low-level error
		// after configuring the additional field inclusions
		if (Revision.class.isAssignableFrom(getFrom())) {
			fieldsToLoad.add(Revision.Fields.ID);
		}
		
		return List.copyOf(fieldsToLoad);
	}
	
	/**
	 * Based on the current set of fields to load, possible field name replacements can be provided here if the index mapping is different than the current model representation. 
	 * @return
	 */
	protected Multimap<String, String> collectFieldsToLoadReplacements() {
		return ImmutableMultimap.of();
	}

	/**
	 * Subclasses may override this method to provide additional fields to fetch from the underlying index, if those fields are necessary to complete
	 * the request. This method is only being called if there is at least one client requested field. If there is none it will load the entire object and
	 * no need to configure additional fields.
	 * 
	 * @param fieldsToLoad
	 */
	protected void collectAdditionalFieldsToFetch(Set<String> fieldsToLoad) {
	}

	/**
	 * Returns the default {@link Searcher} attached to the given context that can search {@link #getDocumentType() document}s. Subclasses may override this if they would like to use a different
	 * searcher service.
	 * 
	 * @param context
	 * @return
	 */
	protected Searcher searcher(C context) {
		if (Revision.class.isAssignableFrom(getFrom())) {
			return context.service(RevisionSearcher.class);
		} else {
			return context.service(Searcher.class);
		}
	}

	/**
	 * Prepares the search query with the clauses, filters specified in this request. 
	 * @param context - the context that can be used to prepare the query 
	 */
	protected Expression prepareQuery(C context) {
		ExpressionBuilder queryBuilder = Expressions.bool();
		prepareQuery(context, queryBuilder);
		return queryBuilder.build();
	}

	/**
	 * Subclasses may override this method to provide additional query clauses based on the received search parameters.
	 * 
	 * @param context
	 * @param queryBuilder
	 */
	protected void prepareQuery(C context, ExpressionBuilder queryBuilder) {
	}

	/**
	 * Subclasses may override to configure scoring. By default disabled score tracking in search requests.
	 * @return whether the search should compute scores for the prepared query or not
	 */
	protected boolean trackScores() {
		return false;
	}
	
	/**
	 * Subclasses may override to configure caching. By default search requests that are executed against a version URI will be cached.  
	 * @param context - the context that can be used to determine whether caching should be enabled for this search request or not
	 * @return whether the search should cache the resulting hits in the underlying index system or not
	 */
	protected boolean cacheHits(C context) {
		return Revision.class.isAssignableFrom(getFrom()) && context.optionalService(PathWithVersion.class).isPresent();
	}
	
	/**
	 * @return the view class to return as matches
	 */
	protected Class<D> getSelect() {
		return getDocumentType();
	}
	
	/**
	 * @return the document type from which the hits will be returned 
	 */
	protected Class<?> getFrom() {
		return getSelect();
	}
	
	/**
	 * @return the type of documents to search for.
	 * @deprecated - will be replaced by {@link #getSelect()} and {@link #getFrom()} in 9.0
	 */
	protected Class<D> getDocumentType() {
		throw new UnsupportedOperationException("No longer supported, use getSelect() and getFrom() methods instead.");
	}
	
	/**
	 * Converts the document hits to a API response of {@link CollectionResource} subtype.
	 * @param context - the context that can be used to convert the hits
	 * @param hits - the hits to convert to API response
	 * @return
	 */
	protected abstract B toCollectionResource(C context, Hits<D> hits);

	/**
	 * @param context
	 * @return the raw expression configured with all specified filters.
	 */
	public final Expression toRawQuery(C context) {
		try {
			return prepareQuery(context);
		} catch (NoResultException e) {
			return Expressions.matchNone();
		}
	}
	
}
