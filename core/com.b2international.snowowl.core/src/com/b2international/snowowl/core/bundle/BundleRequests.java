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

/**
 * @since 8.0
 */
public final class BundleRequests {
	
	public BundleCreateRequestBuilder prepareCreate() {
		return new BundleCreateRequestBuilder();
	}

	public BundleGetRequestBuilder prepareGet(final String resourceUri) {
		return prepareGet(Bundle.uri(resourceUri));
	}
	
	public BundleGetRequestBuilder prepareGet(final ResourceURI resourceUri) {
		return new BundleGetRequestBuilder(resourceUri);
	}
	
	public BundleSearchRequestBuilder prepareSearch() {
		return new BundleSearchRequestBuilder();
	}

	public BundleUpdateRequestBuilder prepareUpdate(final String uniqueId) {
		return new BundleUpdateRequestBuilder(uniqueId);
	}

	public BundleDeleteRequestBuilder prepareDelete(final String uniqueId) {
		return new BundleDeleteRequestBuilder(uniqueId);
	}
}
