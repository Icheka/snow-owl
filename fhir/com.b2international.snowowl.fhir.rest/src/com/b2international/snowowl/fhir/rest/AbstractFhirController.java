/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.b2international.commons.exceptions.ConflictException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.commons.exceptions.NotImplementedException;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;
import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.exceptions.FhirException;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;

/**
 * Custom FHIR exception handling and configuration for all FHIR resources, operations.
 * 
 * @since 8.0
 */
public abstract class AbstractFhirController extends AbstractRestService {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractFhirResourceController.class);
	
	public static final String APPLICATION_FHIR_JSON = "application/fhir+json;charset=utf-8";
	
	private static final String GENERIC_USER_MESSAGE = "Something went wrong during the processing of your request.";
	
	@Autowired
	protected ObjectMapper mapper;
	
	protected final <T> T toRequest(Parameters.Fhir in, Class<T> request) {
		return mapper.convertValue(in.toJson(), request);
	}
	
	protected final Parameters.Fhir toResponse(Object response) {
		return new Parameters.Fhir(Parameters.from(response));
	}
	
	/**
	 * Generic <b>Internal Server Error</b> exception handler, serving as a fallback for RESTful client calls.
	 * 
	 * @param ex
	 * @return {@link OperationOutcome} instance with detailed messages
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody OperationOutcome handle(final Exception ex) {
		if (Throwables.getRootCause(ex).getMessage().toLowerCase().contains("broken pipe")) {
	        return null; // socket is closed, cannot return any response    
	    } else {
	    	LOG.error("Exception during processing of a request", ex);
	    	FhirException fhirException = FhirException.createFhirError(GENERIC_USER_MESSAGE + " Exception: " + ex.getMessage(), OperationOutcomeCode.MSG_BAD_SYNTAX);
	    	return fhirException.toOperationOutcome();
	    }
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody OperationOutcome handle(final BadRequestException ex) {
		return ex.toOperationOutcome();
	}

	
	/**
	 * Exception handler converting any {@link JsonMappingException} to an <em>HTTP 400</em>.
	 * 
	 * @param ex
	 * @return {@link OperationOutcome} instance with detailed messages
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody OperationOutcome handle(HttpMessageNotReadableException ex) {
		LOG.trace("Exception during processing of a JSON document", ex);
		FhirException fhirException = FhirException.createFhirError(GENERIC_USER_MESSAGE + " Exception: " + ex.getMessage(), OperationOutcomeCode.MSG_CANT_PARSE_CONTENT);
		return fhirException.toOperationOutcome();
	}

	/**
	 * <b>Not Found</b> exception handler. All {@link NotFoundException not found exception}s are mapped to {@link HttpStatus#NOT_FOUND
	 * <em>404 Not Found</em>} in case of the absence of an instance resource.
	 * 
	 * @param ex
	 * @return {@link RestApiError} instance with detailed messages
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public @ResponseBody OperationOutcome handle(final NotFoundException ex) {
		return OperationOutcome.builder()
				.addIssue(
					Issue.builder()
						.severity(IssueSeverity.ERROR)
						.code(IssueType.NOT_FOUND)
						.detailsWithDisplayArgs(OperationOutcomeCode.MSG_NO_EXIST, ex.getKey())
						.diagnostics(ex.getMessage())
						.addLocation(ex.getKey())
					.build()
				)
				.build();
	}

	/**
	 * Exception handler to return <b>Not Implemented</b> when an {@link UnsupportedOperationException} is thrown from the underlying system.
	 * 
	 * @param ex
	 * @return {@link RestApiError} instance with detailed messages
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
	public @ResponseBody OperationOutcome handle(NotImplementedException ex) {
		FhirException fhirException = FhirException.createFhirError(ex.getMessage(), OperationOutcomeCode.MSG_UNKNOWN_OPERATION);
		return fhirException.toOperationOutcome();
	}

	/**
	 * Exception handler to return <b>Bad Request</b> when an {@link BadRequestException} is thrown from the underlying system.
	 * 
	 * @param ex
	 * @return {@link RestApiError} instance with detailed messages
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.CONFLICT)
	public @ResponseBody OperationOutcome handle(final ConflictException ex) {
		FhirException fhirException = FhirException.createFhirError(ex.getMessage(), OperationOutcomeCode.MSG_LOCAL_FAIL);
		return fhirException.toOperationOutcome();
	}
	
}
