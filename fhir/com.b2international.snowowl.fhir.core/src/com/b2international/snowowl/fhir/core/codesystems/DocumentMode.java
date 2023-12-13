/*
 * Copyright 2021 B2i Healthcare, https://b2ihealthcare.com
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

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.ResourceNarrative;
import com.b2international.snowowl.fhir.core.model.capabilitystatement.Document;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * FHIR Document mode code system
 * 
 * @since 8.0.0
 * @see Document
 */
@ResourceNarrative("Whether the application produces or consumes documents.")
public enum DocumentMode implements FhirCodeSystem {
	
	PRODUCER,
	
	CONSUMER;
	
	public final static String CODE_SYSTEM_URI = "http://hl7.org/fhir/document-mode";
	
	public String getDisplayName() {
		return StringUtils.capitalizeFirstLetter(name().toLowerCase());
	}
	
	@Override
	public String getCodeValue() {
		return name().toLowerCase();
	}
	
	@Override
	public String getCodeSystemUri() {
		return CODE_SYSTEM_URI;
	}
	
	@JsonCreator
    public static DocumentMode forValue(String value) {
		return DocumentMode.valueOf(value.toUpperCase());
    }

}
