/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.usagecontext;

import java.util.List;

import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Quantity;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * 
 * FHIR Value Quantity Usage Context
 * 
 * https://www.hl7.org/fhir/metadatatypes.html#UsageContext
 * @since 6.6
 */
@JsonDeserialize(using = JsonDeserializer.None.class, builder = QuantityUsageContext.Builder.class)
public class QuantityUsageContext extends UsageContext<Quantity> {

	public static final String CONTEXT_TYPE = "Quantity";

	QuantityUsageContext(final String id, @SuppressWarnings("rawtypes") final List<Extension> extensions, final Coding code, final Quantity value) {
		super(id, extensions, code, value);
	}
	
	@Override
	public String getType() {
		return CONTEXT_TYPE;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends UsageContext.Builder<Builder, QuantityUsageContext, Quantity> {
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		/*
		 * For deserialization support.
		 */
		protected Builder valueQuantity(final Quantity value) {
			this.value = value;
			return this;
		}

		@Override
		protected QuantityUsageContext doBuild() {
			return new QuantityUsageContext(id, extensions, code, value);
		}
	}

}
