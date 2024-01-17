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

import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.Resources;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.request.resource.BaseMetadataResourceConverter;

/**
 * @since 8.0
 */
final class BundleConverter extends BaseMetadataResourceConverter<Bundle, Bundles> {

	public BundleConverter(final RepositoryContext context, final Options expand, final List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}
	
	@Override
	protected Bundles createCollectionResource(List<Bundle> results, String searchAfter, int limit, int total) {
		return new Bundles(results, searchAfter, limit, total);
	}

	@Override
	public void expand(final List<Bundle> results) {
		super.expand(results);
		expandContents(results);
	}

	private void expandContents(final List<Bundle> results) {
		if (!expand().containsKey(Bundle.Expand.RESOURCES)) {
			return;
		}
		
		results.forEach(result -> {
			final Resources resources = ResourceRequests.prepareSearch()
				.setLimit(getLimit(expand().getOptions(Bundle.Expand.RESOURCES)))
				.filterByBundleId(result.getId())
				.buildAsync()
				.getRequest()
				.execute(context());
			
			result.setResources(resources);
		});
	}
}
