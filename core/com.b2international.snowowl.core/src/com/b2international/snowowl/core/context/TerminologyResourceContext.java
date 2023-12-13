/*
 * Copyright 2021 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.context;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.domain.DelegatingContext;
import com.b2international.snowowl.core.domain.DelegatingContext.Builder;

/**
 * @since 8.0
 */
public interface TerminologyResourceContext extends ServiceProvider {

	/**
	 * @return the {@link ResourceURI} of the current {@link TerminologyResource} that is being accessed/modified/etc.
	 */
	default ResourceURI resourceURI() {
		return service(ResourceURI.class);
	}
	
	/**
	 * @return the current {@link TerminologyResource} that is being accessed/modified/etc.
	 */
	default TerminologyResource resource() {
		return service(TerminologyResource.class);
	}
	
	@Override
	default Builder<? extends TerminologyResourceContext> inject() {
		return new DelegatingContext.Builder<>(TerminologyResourceContext.class, this);
	}
}
