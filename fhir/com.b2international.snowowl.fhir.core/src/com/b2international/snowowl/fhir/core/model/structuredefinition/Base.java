/*
 * Copyright 2018-2024 B2i Healthcare, https://b2ihealthcare.com
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

import jakarta.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.model.Element;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * FHIR {@link ElementDefinition} base definition information for tools.
 * @since 7.1
 */
@JsonDeserialize(builder = Base.Builder.class)
public class Base extends Element {

	@NotNull
	@Mandatory
	@JsonProperty
	private final String path;
	
	@Mandatory
	@JsonProperty
	private final int min;
	
	@NotNull
	@Mandatory
	@JsonProperty
	private final String max;
	
	Base(final String id, 
			final List<Extension<?>> extensions,
			final String path, 
			final int min,
			final String max) {
		
		super(id, extensions);
		
		this.path = path;
		this.min = min;
		this.max = max;
	}
	
	public String getPath() {
		return path;
	}
	
	public int getMin() {
		return min;
	}
	
	public String getMax() {
		return max;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends Element.Builder<Builder, Base> {
		
		private String path;
		private int min;
		private String max;
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		/**
		 * @param path to identify the base element
		 * @return builder
		 */
		public Builder path(String path) {
			this.path = path;
			return getSelf();
		}

		/**
		 * Min cardinality of the base element
		 * @param min
		 * @return
		 */
		public Builder min(int min) {
			this.min = min;
			return getSelf();
		}
		
		/**
		 * Max cardinality of the base element
		 * @param max
		 * @return
		 */
		public Builder max(String max) {
			this.max = max;
			return getSelf();
		}
		
		
		@Override
		protected Base doBuild() {
			return new Base(id, extensions, path, min, max);
		}
	}

}
