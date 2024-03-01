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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.codesystems.RestfulInteraction;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * FHIR Capability statement Interaction backbone definition.
 * @since 8.0.0
 */
@JsonDeserialize(builder = Interaction.Builder.class)
public class Interaction {
	
	@Mandatory
	@NotNull
	@Valid
	@JsonProperty
	private final Code code;
	
	@Valid
	@JsonProperty
	private final String documentation;
	
	Interaction(final Code code, final String documentation) {
		this.code = code;
		this.documentation = documentation;
	}
	
	public Code getCode() {
		return code;
	}
	
	public String getDocumentation() {
		return documentation;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends ValidatingBuilder<Interaction> {
		
		private Code code;
		private String documentation;
		
		@JsonProperty
		public Builder code(final Code code) {
			this.code = code;
			return this;
		}

		@JsonIgnore
		public Builder code(final String code) {
			this.code = new Code(code);
			return this;
		}

		@JsonIgnore
		public Builder code(final RestfulInteraction interaction) {
			this.code = interaction.getCode();
			return this;
		}

		public Builder documentation(final String documentation) {
			this.documentation = documentation;
			return this;
		}
		
		@Override
		protected Interaction doBuild() {
			return new Interaction(code, documentation);
		}
	}

}
