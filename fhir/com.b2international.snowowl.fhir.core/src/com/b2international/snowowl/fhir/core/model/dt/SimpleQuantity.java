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
package com.b2international.snowowl.fhir.core.model.dt;

import java.util.List;

import jakarta.validation.constraints.Null;

import com.b2international.snowowl.fhir.core.model.Extension;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * FHIR Simple Quantity complex datatype
 * 
 * sqty-1: The comparator is not used on a SimpleQuantity (expression : comparator.empty())
 * 
 * @see <a href="https://www.hl7.org/fhir/datatypes.html#quantity">FHIR:Data Types:Quantity</a>
 * @since 6.6
 */
@JsonDeserialize(builder = SimpleQuantity.Builder.class)
public class SimpleQuantity extends BaseQuantity {
	
	SimpleQuantity(String id, List<Extension<?>> extensions,
			final Double value, final Code comparator, final String unit, final Uri system, final Code code) {
		super(id, extensions, value, comparator, unit, system, code);
	}
	
	/*
	 * Comparator getter is overridden to enable additional @Null restriction
	 */
	@Null
	@JsonProperty
	@Override
	public Code getComparator() {
		return comparator;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends BaseQuantity.Builder<Builder, SimpleQuantity> {
		
		@Override
		protected Builder getSelf() {
			return this;
		}
	
		@Override
		protected SimpleQuantity doBuild() {
			return new SimpleQuantity(id, extensions, value, comparator, unit, system, code);
		}
	}

}
