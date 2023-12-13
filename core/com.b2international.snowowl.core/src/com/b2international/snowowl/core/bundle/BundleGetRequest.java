/*
 * Copyright 2021-2022 B2i Healthcare, https://b2ihealthcare.com
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

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.request.resource.BaseGetResourceRequest;

/**
 * @since 8.0
 */
final class BundleGetRequest
		extends BaseGetResourceRequest<BundleSearchRequestBuilder, Bundles, Bundle> {

	private static final long serialVersionUID = 1L;

	BundleGetRequest(ResourceURI resourceUri) {
		super(resourceUri);
	}

	@Override
	protected BundleSearchRequestBuilder createSearchRequestBuilder() {
		return new BundleSearchRequestBuilder();
	}

}
