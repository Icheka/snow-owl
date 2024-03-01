/*
 * Copyright 2011-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.fhir.core.model.conceptmap;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.NotEmpty;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * FHIR Concept map dependsOn backbone element
 * <br> Other elements required for the mapping (from context)
 * @since 6.10
 */
@JsonDeserialize(builder = DependsOn.Builder.class)
public class DependsOn {

	@Valid
	@JsonProperty
	@NotNull
	private final Uri property;
	
	@Valid
	@JsonProperty
	private final Uri system;
	
	@Valid
	@NotEmpty
	@JsonProperty
	private final String value;
	
	@Summary
	@JsonProperty
	private final String display;

	DependsOn(Uri property, Uri system, String value, String display) {
		
		this.property = property;
		this.system = system;
		this.value = value;
		this.display = display;
	}
	
	public Uri getProperty() {
		return property;
	}
	
	public Uri getSystem() {
		return system;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getDisplay() {
		return display;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends ValidatingBuilder<DependsOn> {

		private Uri property;
		private Uri system;
		private String value;
		private String display;

		public Builder property(final Uri property) {
			this.property = property;
			return this;
		}
		
		public Builder property(final String propertyString) {
			this.property = new Uri(propertyString);
			return this;
		}
		
		public Builder system(final Uri system) {
			this.system = system;
			return this;
		}
		
		public Builder system(final String systemString) {
			this.system = new Uri(systemString);
			return this;
		}
		
		public Builder value(final String value) {
			this.value = value;
			return this;
		}
		
		public Builder display(final String display) {
			this.display = display;
			return this;
		}
		
		@Override
		protected DependsOn doBuild() {
			
			return new DependsOn(property, system, value, display);
		}
	}
	
}
