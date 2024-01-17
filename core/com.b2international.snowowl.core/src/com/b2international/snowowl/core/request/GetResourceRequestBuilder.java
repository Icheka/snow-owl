/*
 * Copyright 2016-2021 B2i Healthcare, https://b2ihealthcare.com
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

import com.b2international.snowowl.core.ServiceProvider;

/**
 * @since 5.2
 */
public abstract class GetResourceRequestBuilder<
	B extends GetResourceRequestBuilder<B, SB, C, SR, R>,
	SB extends SearchResourceRequestBuilder<SB, C, SR>,
	C extends ServiceProvider, 
	SR,
	R> 
	extends IndexResourceRequestBuilder<B, C, R> {
	
	private final GetResourceRequest<SB, C, SR, R> request;

	protected GetResourceRequestBuilder(final GetResourceRequest<SB, C, SR, R> request) {
		super();
		this.request = request;
	}
	
	@Override
	protected final IndexResourceRequest<C, R> create() {
		return request;
	}

}
