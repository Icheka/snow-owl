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
package com.b2international.snowowl.fhir.core.model;

import java.util.Collection;

import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Entry to encapsulate a resource-based response in a {@link Bundle}
 * @since 8.0.0
 */
@JsonDeserialize(using = JsonDeserializer.None.class, builder = ResourceResponseEntry.Builder.class)
public class ResourceResponseEntry extends Entry {

	/*
	 * entry.all(response.exists() = (%resource.type = 'batch-response' or %resource.type = 'transaction-response' or %resource.type = 'history'))
	 */
	private BatchResponse response;
	
	private FhirResource responseResource;
	
	protected ResourceResponseEntry(final Collection<String> links, final Uri fullUrl, 
			final BatchResponse response, final FhirResource responseResource) {
		super(links, fullUrl);
		this.response = response;
		this.responseResource = responseResource;
	}
	
	public BatchResponse getResponse() {
		return response;
	}
	
	@JsonProperty("resource")
	public FhirResource getResponseResource() {
		return responseResource;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends Entry.Builder<Builder, ResourceResponseEntry> {
		
		private BatchResponse response;
		
		private FhirResource responseResource;
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		public Builder response(BatchResponse response) {
			this.response = response;
			return getSelf();
		}
		
		public Builder resource(FhirResource requestResource) {
			this.responseResource = requestResource;
			return getSelf();
		}
		
		@Override
		protected ResourceResponseEntry doBuild() {
			return new ResourceResponseEntry(links, fullUrl, response, responseResource);
		}
		
	}

}
