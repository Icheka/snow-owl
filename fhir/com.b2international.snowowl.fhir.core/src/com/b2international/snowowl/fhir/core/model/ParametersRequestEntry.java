/*
 * Copyright 2021-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.fhir.core.model;

import java.util.Collection;

import jakarta.validation.constraints.AssertTrue;

import com.b2international.snowowl.fhir.core.codesystems.HttpVerb;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Entry to encapsulate a parameter-based request in a {@link Bundle}
 * @since 8.0.0
 */
@JsonDeserialize(using = JsonDeserializer.None.class, builder = ParametersRequestEntry.Builder.class)
public class ParametersRequestEntry extends AbstractRequestEntry {

	private Fhir requestResource;
	
	protected ParametersRequestEntry(final Collection<String> links, final Uri fullUrl, 
			final BatchRequest request, final Fhir requestResource) {
		super(links, fullUrl, request);
		this.requestResource = requestResource;
	}
	
	@JsonProperty("resource")
	public Fhir getRequestResource() {
		return requestResource;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonIgnore
	@AssertTrue(message = "Only POST requests can be parameter-based")
	public boolean isPost() {
		return HttpVerb.POST.getCode().equals(getRequest().getMethod());
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends AbstractRequestEntry.Builder<Builder, ParametersRequestEntry> {
		
		private BatchRequest request;
		
		private Fhir requestResource;
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		public Builder request(BatchRequest request) {
			this.request = request;
			return getSelf();
		}
		
		public Builder resource(Fhir requestResource) {
			this.requestResource = requestResource;
			return getSelf();
		}
		
		@Override
		protected ParametersRequestEntry doBuild() {
			return new ParametersRequestEntry(links, fullUrl, request, requestResource);
		}
	}
}
