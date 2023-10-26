/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.collection;

import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.Resources;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.request.resource.BaseMetadataResourceConverter;

/**
 * @since 9.0.0
 */
final class TerminologyResourceCollectionConverter extends BaseMetadataResourceConverter<TerminologyResourceCollection, TerminologyResourceCollections> {

	public TerminologyResourceCollectionConverter(RepositoryContext context, Options expand, List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}

	@Override
	protected TerminologyResourceCollections createCollectionResource(List<TerminologyResourceCollection> results, String searchAfter, int limit, int total) {
		return new TerminologyResourceCollections(results, searchAfter, limit, total);
	}
	
	@Override
	public void expand(List<TerminologyResourceCollection> results) {
		super.expand(results);
		expandRefsets(null);
	}
	private void expandRefsets(final List<TerminologyResourceCollection> results) {
		if (!expand().containsKey(TerminologyResourceCollection.Expand.REFSETS)) {
			return;
		}
		
		results.forEach(result -> {
			final Resources resources = ResourceRequests.prepareSearch()
				.setLimit(getLimit(expand().getOptions(TerminologyResourceCollection.Expand.REFSETS)))
				.filterByBundleId(result.getId())
				.buildAsync()
				.getRequest()
				.execute(context());
			
			result.setRefsets(resources);
		});
	}

}
