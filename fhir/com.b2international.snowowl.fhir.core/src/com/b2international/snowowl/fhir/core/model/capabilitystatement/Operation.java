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
package com.b2international.snowowl.fhir.core.model.capabilitystatement;

import jakarta.validation.constraints.NotEmpty;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * FHIR Capability statement Operation backbone definition.
 * @since 8.0.0
 */
@JsonDeserialize(builder = Operation.Builder.class)
public class Operation {
	
	@NotEmpty
	@Mandatory
	@JsonProperty
	private final String name;
	
	@JsonProperty
	private final Uri definition;
	
	@JsonProperty
	private final String documentation;
	
	Operation(final String name, final Uri definition, final String documentation) {
		this.name = name;
		this.definition = definition;
		this.documentation = documentation;
	}
	
	public String getName() {
		return name;
	}
	
	public Uri getDefinition() {
		return definition;
	}
	
	public String getDocumentation() {
		return documentation;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends ValidatingBuilder<Operation> {
		
		private String name;
		private Uri definition;
		private String documentation;
		
		public Builder name(final String name) {
			this.name = name;
			return this;
		}

		public Builder definition(final String definition) {
			this.definition = new Uri(definition);
			return this;
		}
		
		public Builder definition(final Uri definition) {
			this.definition = definition;
			return this;
		}
		
		public Builder documentation(final String documentation) {
			this.documentation = documentation;
			return this;
		}
		
		@Override
		protected Operation doBuild() {
			return new Operation(name, definition, documentation);
		}
	}
}
