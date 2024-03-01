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
package com.b2international.snowowl.fhir.core.request.codesystem;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.Concept;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.codesystem.*;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.collect.Sets;

/**
 * Performs the lookup operation based on the parameter-based lookup request.
 * 
 * <p>
 * From the spec:
 * If no properties are specified, the server chooses what to return. The following properties are defined for all code systems: url, name, version (code system info) 
 * and code information: display, definition, designation, parent and child, and for designations, lang.X where X is a designation language code. 
 * Some of the properties are returned explicit in named parameters (when the names match), and the rest (except for lang.X) in the property parameter group
 * </p>
 * @see LookupRequest
 * @see LookupResult
 * @since 8.0
 */
final class FhirLookupRequest extends FhirRequest<LookupResult> {

	private static final long serialVersionUID = 1L;
	
	private static final Set<String> LOOKUP_REQUEST_PROPS = Arrays.stream(SupportedCodeSystemRequestProperties.values()).map(SupportedCodeSystemRequestProperties::getCodeValue).collect(Collectors.toSet());
	
	@NotNull
	@Valid
	@JsonProperty
	@JsonUnwrapped
	private LookupRequest request;

	FhirLookupRequest(LookupRequest request) {
		super(request.getSystem(), request.getVersion());
		this.request = request;
	}

	@Override
	protected LookupResult doExecute(ServiceProvider context, CodeSystem codeSystem) {
		validateRequestedProperties(codeSystem);
		
		final String acceptLanguage = extractLocales(request.getDisplayLanguage());

		FhirCodeSystemLookupConverter converter = context.service(RepositoryManager.class).get(codeSystem.getToolingId())
				.optionalService(FhirCodeSystemLookupConverter.class)
				.orElse(FhirCodeSystemLookupConverter.DEFAULT);
		
		final String conceptExpand = converter.configureConceptExpand(request);
		
		Concept concept = CodeSystemRequests.prepareSearchConcepts()
			.one()
			.filterByCodeSystemUri(codeSystem.getResourceURI())
			.filterById(request.getCode())
			.setLocales(acceptLanguage)
			.setExpand(conceptExpand)
			.buildAsync()
			.execute(context)
			.first()
			.orElseThrow(() -> new NotFoundException("Concept", request.getCode()));
		
		
		return LookupResult.builder()
				.name(codeSystem.getName())
				.display(concept.getTerm())
				.version(codeSystem.getVersion())
				.designation(converter.expandDesignations(context, codeSystem, concept, request, acceptLanguage))
				.property(converter.expandProperties(context, codeSystem, concept, request))
				.build();
	}
	
	private void validateRequestedProperties(CodeSystem codeSystem) {
		final Set<String> requestedProperties = request.getPropertyCodes();
		// first check if any of the properties are lookup request properties
		final Set<String> nonLookupProperties = Sets.difference(requestedProperties, LOOKUP_REQUEST_PROPS);
		
		// second check if the remaining unsupported properties supported by the CodeSystem either via full URL
		final Set<String> supportedProperties = codeSystem.getProperties() == null 
				? Collections.emptySet() 
				: codeSystem.getProperties().stream().map(SupportedConceptProperty::getUri).map(Uri::getUriValue).collect(Collectors.toSet());
		final Set<String> unsupportedProperties = Sets.difference(nonLookupProperties, supportedProperties);
		
		// or via their code only
		final Set<String> supportedCodes = codeSystem.getProperties() == null 
				? Collections.emptySet() 
				: codeSystem.getProperties().stream().map(SupportedConceptProperty::getCodeValue).collect(Collectors.toSet());
		final Set<String> unsupportedCodes = Sets.difference(unsupportedProperties, supportedCodes);
		
		if (!unsupportedCodes.isEmpty()) {
			if (unsupportedCodes.size() == 1) {
				throw new BadRequestException(String.format("Unrecognized property %s. Supported properties are: %s.", unsupportedCodes, supportedProperties), "LookupRequest.property");
			} else {
				throw new BadRequestException(String.format("Unrecognized properties %s. Supported properties are: %s.", unsupportedCodes, supportedProperties), "LookupRequest.property");
			}
		}
	}

}
