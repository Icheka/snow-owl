/*
 * Copyright 2022 B2i Healthcare, https://b2ihealthcare.com
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

import java.util.List;

import com.b2international.snowowl.fhir.core.codesystems.ExtensionType;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * 
 * FHIR Code Extension
 * 
 * @see <a href="https://www.hl7.org/fhir/extensibility.html#Extension">FHIR:Foundation:Extensibility</a>
 * @since 8.4.0
 */
@JsonDeserialize(using = JsonDeserializer.None.class, builder = CodeExtension.Builder.class)
public class CodeExtension extends Extension<Code> {
	
	public CodeExtension(final String id, final List<Extension<?>> extensions, final Uri url, final Code value) {
		super(id, extensions, url, value);
	}

	@Override
	public ExtensionType getExtensionType() {
		return ExtensionType.CODE;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends Extension.Builder<Builder, CodeExtension, Code> {
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		/*
		 * For deserialization support.
		 */
		protected Builder valueCode(final Code value) {
			this.value = value;
			return this;
		}
		
		@Override
		protected CodeExtension doBuild() {
			return new CodeExtension(id, extensions, url, value);
		}
	}
}
