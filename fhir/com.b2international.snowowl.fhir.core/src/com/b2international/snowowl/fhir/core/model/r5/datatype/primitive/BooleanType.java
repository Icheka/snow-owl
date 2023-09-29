/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.r5.datatype.primitive;

import java.util.regex.Pattern;

import com.b2international.snowowl.fhir.core.model.r5.base.PrimitiveType;

/**
 * A boolean value. 
 *
 * @see <a href="https://hl7.org/fhir/R5/datatypes.html#boolean">2.1.28.0.1 Primitive Types - boolean</a>
 * @since 9.0
 */
public class BooleanType extends PrimitiveType<Boolean> {
	
	public static final Pattern VALID_PATTERN = Pattern.compile("true|false");
	
	private final String value;

	public BooleanType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	@Override
	public Boolean getRawValue() {
		return Boolean.valueOf(value);
	}
}
