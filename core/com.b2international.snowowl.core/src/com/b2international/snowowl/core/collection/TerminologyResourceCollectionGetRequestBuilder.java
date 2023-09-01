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

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.request.resource.BaseGetResourceRequestBuilder;

/**
 * @since 9.0
 */
public final class TerminologyResourceCollectionGetRequestBuilder 
		extends BaseGetResourceRequestBuilder<TerminologyResourceCollectionGetRequestBuilder, TerminologyResourceCollectionSearchRequestBuilder, TerminologyResourceCollections, TerminologyResourceCollection> {

	public TerminologyResourceCollectionGetRequestBuilder(ResourceURI resourceUri) {
		super(new TerminologyResourceCollectionGetRequest(resourceUri));
	}

}