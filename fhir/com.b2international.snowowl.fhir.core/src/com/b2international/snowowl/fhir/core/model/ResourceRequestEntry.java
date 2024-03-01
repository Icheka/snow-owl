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
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Entry to encapsulate a resource-based request in a {@link Bundle}
 * @since 8.0.0
 */
@JsonDeserialize(using = JsonDeserializer.None.class, builder = ResourceRequestEntry.Builder.class)
public class ResourceRequestEntry extends AbstractRequestEntry {

	private FhirResource requestResource;
	
	protected ResourceRequestEntry(final Collection<String> links, final Uri fullUrl, 
			final BatchRequest request, final FhirResource requestResource) {
		
		super(links, fullUrl, request);
		this.requestResource = requestResource;
	}
	
	@JsonProperty("resource")
	public FhirResource getRequestResource() {
		return requestResource;
	}
	
	@JsonIgnore
	@AssertTrue(message = "Only POST requests can be resource-based")
	public boolean isPost() {
		
		if (getRequest() == null) return false;
		return HttpVerb.POST.getCode().equals(getRequest().getMethod());
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends AbstractRequestEntry.Builder<Builder, ResourceRequestEntry> {
		
		private FhirResource requestResource;
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		public Builder request(BatchRequest request) {
			this.request = request;
			return getSelf();
		}
		
		public Builder resource(FhirResource requestResource) {
			this.requestResource = requestResource;
			return getSelf();
		}
		
		@Override
		protected ResourceRequestEntry doBuild() {
			return new ResourceRequestEntry(links, fullUrl, request, requestResource);
		}
	}

}
