/*
 * Copyright 2021-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.bundle;

import java.util.Collections;

import com.b2international.index.Hits;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.resource.BaseResourceSearchRequest;

/**
 * @since 8.0
 */
final class BundleSearchRequest extends BaseResourceSearchRequest<Bundles> {

	private static final long serialVersionUID = 1L;

	@Override
	protected void prepareAdditionalFilters(RepositoryContext context, ExpressionBuilder queryBuilder) {
		super.prepareAdditionalFilters(context, queryBuilder);
		queryBuilder.filter(ResourceDocument.Expressions.resourceType(Bundle.RESOURCE_TYPE));
	}

	@Override
	protected Bundles toCollectionResource(RepositoryContext context, Hits<ResourceDocument> hits) {
		final BundleConverter converter = new BundleConverter(context, expand(), null);
		return converter.convert(hits.getHits(), hits.getSearchAfter(), hits.getLimit(), hits.getTotal());
	}

	@Override
	protected Bundles createEmptyResult(int limit) {
		return new Bundles(Collections.emptyList(), null, limit, 0);
	}
	
}
