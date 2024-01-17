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
package com.b2international.snowowl.fhir.core.request.conceptmap;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMap;
import com.b2international.snowowl.fhir.core.model.conceptmap.TranslateRequest;
import com.b2international.snowowl.fhir.core.model.conceptmap.TranslateResult;

/**
 * @since 8.0
 */
@FunctionalInterface
public interface FhirConceptMapTranslator {

	FhirConceptMapTranslator NOOP = (context, conceptMap, request) -> TranslateResult.builder().message("N/A").build(); 
	
	/**
	 * @param context
	 * @param conceptMap
	 * @param request
	 * @return
	 */
	TranslateResult translate(ServiceProvider context, ConceptMap conceptMap, TranslateRequest request);
	
}
