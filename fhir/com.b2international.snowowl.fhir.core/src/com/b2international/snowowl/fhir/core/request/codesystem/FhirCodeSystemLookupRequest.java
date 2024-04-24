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

import org.hl7.fhir.r5.model.CodeSystem;
import org.hl7.fhir.r5.model.Parameters;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.Concept;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.codesystem.*;
import com.b2international.snowowl.fhir.core.operations.CodeSystemLookupParameters;
import com.b2international.snowowl.fhir.core.operations.CodeSystemLookupResultParameters;
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
final class FhirCodeSystemLookupRequest extends FhirRequest<CodeSystemLookupResultParameters> {

	private static final long serialVersionUID = 1L;
	
	private static final Set<String> LOOKUP_REQUEST_PROPS = Arrays.stream(SupportedCodeSystemRequestProperties.values()).map(SupportedCodeSystemRequestProperties::getCodeValue).collect(Collectors.toSet());
	
	private final CodeSystemLookupParameters parameters;

	FhirCodeSystemLookupRequest(CodeSystemLookupParameters parameters) {
		super(parameters.getSystem().getValue(), parameters.getVersion().getValue());
		this.parameters = parameters;
	}

	@Override
	protected CodeSystemLookupResultParameters doExecute(ServiceProvider context, CodeSystem codeSystem) {
		validateRequestedProperties(codeSystem);
		
		final String acceptLanguage = extractLocales(parameters.getDisplayLanguage());

		FhirCodeSystemLookupConverter converter = context.service(RepositoryManager.class).get(codeSystem.getUserString(TerminologyResource.Fields.TOOLING_ID))
				.optionalService(FhirCodeSystemLookupConverter.class)
				.orElse(FhirCodeSystemLookupConverter.DEFAULT);
		
		final String conceptExpand = converter.configureConceptExpand(parameters);
		
		Concept concept = CodeSystemRequests.prepareSearchConcepts()
			.one()
			.filterByCodeSystemUri(new ResourceURI(codeSystem.getUserString(TerminologyResource.Fields.RESOURCE_URI)))
			.filterById(parameters.getCode().getCode())
			.setLocales(acceptLanguage)
			.setExpand(conceptExpand)
			.buildAsync()
			.execute(context)
			.first()
			.orElseThrow(() -> new NotFoundException("Concept", parameters.getCode().getCode()));
		
		CodeSystemLookupResultParameters result = new CodeSystemLookupResultParameters();
		
		result.setName(codeSystem.getName());
		result.setDisplay(concept.getTerm());
		result.setVersion(codeSystem.getVersion());
		result.setDesignation(converter.expandDesignations(context, codeSystem, concept, parameters, acceptLanguage));
		result.setProperty(converter.expandProperties(context, codeSystem, concept, parameters));
		
		return result; 
	}
	
	private void validateRequestedProperties(CodeSystem codeSystem) {
		final Set<String> requestedProperties = parameters.getPropertyCodes();
		// first check if any of the properties are lookup request properties
		final Set<String> nonLookupProperties = Sets.difference(requestedProperties, LOOKUP_REQUEST_PROPS);
		
		// second check if the remaining unsupported properties supported by the CodeSystem either via full URL
		final Set<String> supportedProperties = codeSystem.getProperty() == null 
				? Collections.emptySet() 
				: codeSystem.getProperty().stream().map(SupportedConceptProperty::getUri).map(Uri::getUriValue).collect(Collectors.toSet());
		final Set<String> unsupportedProperties = Sets.difference(nonLookupProperties, supportedProperties);
		
		// or via their code only
		final Set<String> supportedCodes = codeSystem.getProperty() == null 
				? Collections.emptySet() 
				: codeSystem.getProperty().stream().map(SupportedConceptProperty::getCodeValue).collect(Collectors.toSet());
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