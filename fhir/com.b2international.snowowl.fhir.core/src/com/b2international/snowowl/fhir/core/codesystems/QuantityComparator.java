/*
 * Copyright 2018 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.fhir.core.codesystems;

import com.b2international.snowowl.fhir.core.ResourceNarrative;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * FHIR property type code system
 * 
 * @since 6.4
 */
@ResourceNarrative("How the Quantity should be understood and represented.")
public enum QuantityComparator implements FhirCodeSystem {
	
	LESS_THAN("<", "Less than"),
	LESS_OR_EQUAL_TO("<=", "Less or Equal to"), 
	GREATER_OR_EQUAL_TO(">=", "Greater or Equal to"), 
	GREATER_THAN(">", "Greater than");
	
	public final static String CODE_SYSTEM_URI = "http://hl7.org/fhir/quantity-comparator";
	
	private String code;
	private String displayName;


	private QuantityComparator(String code, String displayName) {
		this.code = code;
		this.displayName = displayName;
	}
	
	@Override
	public String getCodeValue() {
		return code;
	}
	
	@Override
	public String getDisplayName() {
		return displayName;
	}
	
	@Override
	public String getCodeSystemUri() {
		return CODE_SYSTEM_URI;
	}
	
	@JsonCreator
    public static QuantityComparator forValue(String value) {
		
		switch (value) {
		case "<": return LESS_THAN;
		case "<=": return LESS_OR_EQUAL_TO;
		case ">=": return GREATER_OR_EQUAL_TO;
		case ">": return GREATER_THAN;
		
		default:
			throw new IllegalArgumentException("Uknown comparator code : '" + value + "'");
		}
    }

}
