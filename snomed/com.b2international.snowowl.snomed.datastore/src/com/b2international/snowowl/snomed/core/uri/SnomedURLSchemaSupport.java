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
package com.b2international.snowowl.snomed.core.uri;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.uri.ResourceURLSchemaSupport;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;

/**
 * TODO move SnomedUri from FHIR implementation and use that to provide full SNOMED CT URI support
 * 
 * @since 8.0
 */
public class SnomedURLSchemaSupport implements ResourceURLSchemaSupport {

	@Override
	public void validate(String uri) throws BadRequestException {
		// very basic validation to ensure that all tests use proper URLs
		if (!uri.startsWith(SnomedTerminologyComponentConstants.SNOMED_URI_SCT) && !uri.startsWith(SnomedTerminologyComponentConstants.SNOMED_URI_DEV)) {
			throw new BadRequestException("SNOMED CT URIs must start with one of ['%s', '%s']. Got '%s'", SnomedTerminologyComponentConstants.SNOMED_URI_SCT, SnomedTerminologyComponentConstants.SNOMED_URI_DEV, uri);
		}
	}
	
	@Override
	public String withVersion(String uri, String version, LocalDate effectiveTime) {
		return String.join("/version/", uri, Optional.ofNullable(effectiveTime).map(et -> et.format(DateTimeFormatter.BASIC_ISO_DATE)).orElse(version));
	}
	
}
