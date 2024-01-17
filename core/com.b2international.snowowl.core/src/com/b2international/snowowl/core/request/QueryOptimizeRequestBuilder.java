/*
 * Copyright 2020-2023 B2i Healthcare, https://b2ihealthcare.com
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

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;

import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.context.TerminologyResourceContentRequestBuilder;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.QueryExpression;
import com.b2international.snowowl.core.domain.QueryExpressionDiffs;

/**
 * @since 7.7
 */
public final class QueryOptimizeRequestBuilder 
	extends ResourceRequestBuilder<QueryOptimizeRequestBuilder, BranchContext, QueryExpressionDiffs> 
	implements TerminologyResourceContentRequestBuilder<QueryExpressionDiffs> {

	private static final int DEFAULT_LIMIT = 25;
	
	private final List<QueryExpression> inclusions = newArrayList();
	private final List<QueryExpression> exclusions = newArrayList();

	private Integer limit = DEFAULT_LIMIT;
	private Options additionalOptions = Options.empty();
	
	public QueryOptimizeRequestBuilder filterByInclusions(Collection<QueryExpression> inclusions) {
		if (inclusions != null) {
			this.inclusions.addAll(inclusions);
		}
		return getSelf();
	}

	public QueryOptimizeRequestBuilder filterByExclusions(Collection<QueryExpression> exclusions) {
		if (exclusions != null) {
			this.exclusions.addAll(exclusions);
		}
		return getSelf();
	}
	
	public QueryOptimizeRequestBuilder setLimit(Integer limit) {
		this.limit = limit;
		return getSelf();
	}
	
	public QueryOptimizeRequestBuilder setAdditionalOptions(Options additionalOptions) {
		this.additionalOptions = additionalOptions;
		return getSelf();
	}

	@Override
	protected QueryOptimizeRequest create() {
		return new QueryOptimizeRequest();
	}
	
	@Override
	protected void init(ResourceRequest<BranchContext, QueryExpressionDiffs> request) {
		super.init(request);
		
		final QueryOptimizeRequest req = (QueryOptimizeRequest) request;
		req.setInclusions(inclusions);
		req.setExclusions(exclusions);
		req.setLimit(limit == null ? DEFAULT_LIMIT : limit);
		req.setAdditionalOptions(additionalOptions == null ? Options.empty() : additionalOptions);
	}
}
