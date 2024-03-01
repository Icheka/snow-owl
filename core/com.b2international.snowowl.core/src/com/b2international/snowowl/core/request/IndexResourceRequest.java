/*
 * Copyright 2020-2024 B2i Healthcare, https://b2ihealthcare.com
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

import java.util.Collections;
import java.util.List;

import jakarta.validation.constraints.NotNull;

import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.ServiceProvider;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Base class for requests that may run subqueries (or sub-requests) to attach
 * additional objects related to the primary item in the response. It also
 * allows returning partial objects via field selection.
 *
 * @param <C> - the request context type
 * @param <R> - the response type
 * 
 * @since 7.5
 */
public abstract class IndexResourceRequest<C extends ServiceProvider, R> extends ResourceRequest<C, R> {

	private static final long serialVersionUID = 1L;

	@NotNull
	private Options expand;

	private List<String> fields = Collections.emptyList();
	
	@JsonProperty
	protected final Options expand() {
		return expand;
	}
	
	@JsonProperty
	protected final List<String> fields() {
		return fields;
	}
	
	final void setExpand(Options expand) {
		this.expand = expand;
	}
	
	final void setFields(List<String> fields) {
		this.fields = fields;
	}
	
}
