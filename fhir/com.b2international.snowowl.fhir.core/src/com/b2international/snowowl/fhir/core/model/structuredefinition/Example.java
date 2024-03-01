/*
 * Copyright 2019-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.fhir.core.model.structuredefinition;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.model.Element;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.model.typedproperty.TypedProperty;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * A sample value for this element demonstrating the type of information that would typically be found in the element.
 * 
 * @since 7.1
 */
@JsonDeserialize(builder = Example.Builder.class)
public class Example extends Element {
	
	@NotNull
	@Mandatory
	@JsonProperty
	private final String label;
	
	@NotNull
	@Valid
	@Mandatory
	@JsonProperty
	@JsonUnwrapped
	private TypedProperty<?> value;
	
	Example(final String id, 
			final List<Extension<?>> extensions, 
			final String label,
			final TypedProperty<?> value) {
		
		super(id, extensions);
		this.label = label;
		this.value = value;
	}
	
	public String getLabel() {
		return label;
	}
	
	public TypedProperty<?> getValue() {
		return value;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends Element.Builder<Builder, Example> {
		
		private String label;
		private TypedProperty<?> value;
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		public Builder label(String label) {
			this.label = label;
			return getSelf();
		}
		
		@JsonAlias({
			"valueString", 
			"valueDate", 
			"valueDateTime", 
			"valueInstant"})
		public Builder value(final TypedProperty<?> value) {
			this.value = value;
			return getSelf();
		}
		
		@Override
		protected Example doBuild() {
			return new Example(id, extensions, label, value);
		}
	}

}
