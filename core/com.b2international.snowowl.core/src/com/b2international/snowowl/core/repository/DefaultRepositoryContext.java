/*
 * Copyright 2011-2021 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.repository;

import com.b2international.snowowl.core.RepositoryInfo;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.DelegatingContext;
import com.b2international.snowowl.core.domain.RepositoryContext;

/**
 * @since 4.5
 */
public final class DefaultRepositoryContext extends DelegatingContext implements RepositoryContext {

	public DefaultRepositoryContext(ServiceProvider delegate, RepositoryInfo info) {
		super(delegate);
		bind(RepositoryInfo.class, info);
	}

}
