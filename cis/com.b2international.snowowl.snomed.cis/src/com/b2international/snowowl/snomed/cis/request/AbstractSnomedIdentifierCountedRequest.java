/*
 * Copyright 2017-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.cis.request;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.cis.domain.SctIds;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * @since 5.5
 */
abstract class AbstractSnomedIdentifierCountedRequest implements Request<ServiceProvider, SctIds> {

	@JsonProperty
	@NotNull
	private final ComponentCategory category;
	
	@JsonProperty
	private final String namespace;

	@JsonProperty
	@PositiveOrZero
	private final int quantity;

	AbstractSnomedIdentifierCountedRequest(ComponentCategory category, String namespace, int quantity) {
		this.category = category;
		this.namespace = namespace;
		this.quantity = quantity;
	}

	protected final ComponentCategory category() {
		return category;
	}
	
	protected final String namespace() {
		return namespace;
	}
	
	protected final int quantity() {
		return quantity;
	}

}
