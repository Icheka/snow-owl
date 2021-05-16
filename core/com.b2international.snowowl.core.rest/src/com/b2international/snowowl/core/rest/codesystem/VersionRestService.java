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
package com.b2international.snowowl.core.rest.codesystem;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.b2international.commons.exceptions.ApiError;
import com.b2international.commons.exceptions.ApiErrorException;
import com.b2international.commons.validation.ApiValidation;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.jobs.JobRequests;
import com.b2international.snowowl.core.jobs.RemoteJobEntry;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;
import com.b2international.snowowl.core.version.Version;
import com.b2international.snowowl.core.version.VersionDocument;
import com.b2international.snowowl.core.version.Versions;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import io.swagger.annotations.*;

/**
 * @since 1.0
 */
@Api(value = "Resources", description="Resources", tags = { "resources" })
@RestController
@RequestMapping(value = "/versions")
public class VersionRestService extends AbstractRestService {
	
	public VersionRestService() {
		super(VersionDocument.Fields.SORT_FIELDS);
	}
	
	@ApiOperation(
			value="Retrieve all resource versions",
			notes="Returns a list containing all published resource versions.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 400, message = "Invalid search config", response = RestApiError.class)
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<Versions> searchVersions(
			VersionRestSearch config) {
		return ResourceRequests.prepareSearchVersion()
				.filterByResources(config.getResource())
				.setLimit(config.getLimit())
				.setExpand(config.getExpand())
				.setSearchAfter(config.getSearchAfter())
				.sortBy(extractSortFields(config.getSort()))
				.buildAsync()
				.execute(getBus());
		
	}

	@ApiOperation(
			value="Retrieve a resource version by identifier (<resourceType/resourceId/version>)",
			notes="Returns a published resource version for the specified resource with the given version identifier.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Version Not Found", response = RestApiError.class)
	})
	@GetMapping(value = "/{versionUri:**}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<Version> getVersion(
			@ApiParam(value="The resource version uri")
			@PathVariable(value="versionUri") 
			final ResourceURI versionUri) {
		return ResourceRequests.prepareGetVersion(versionUri).buildAsync().execute(getBus());

	}
	
	@ApiOperation(
			value="Create a new resource version",
			notes="Creates a new resource version. "
					+ "The version tag (represented by an empty branch) is created on the resource's current working branch. "
					+ "Where applicable, effective times are set on the unpublished content as part of this operation.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created"),
		@ApiResponse(code = 404, message = "Not found", response = RestApiError.class),
		@ApiResponse(code = 409, message = "Code system version conflicts with existing branch", response = RestApiError.class)
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(value = HttpStatus.CREATED)
	public ResponseEntity<Void> createVersion(
			@ApiParam(value="Version parameters")
			@RequestBody final VersionRestInput input) {
		ApiValidation.checkInput(input);
		String jobId = ResourceRequests.prepareNewVersion()
				.setResource(input.getResource())
				.setVersion(input.getVersion())
				.setDescription(input.getDescription())
				.setEffectiveTime(input.getEffectiveTime())
				.setForce(input.isForce())
				.buildAsync()
				.runAsJobWithRestart(ResourceRequests.versionJobKey(input.getResource()), String.format("Creating version '%s/%s'", input.getResource(), input.getVersion()))
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES);
		
		RemoteJobEntry job = JobRequests.waitForJob(getBus(), jobId, 500);
		
		if (job.isSuccessful()) {
			final URI location = MvcUriComponentsBuilder.fromMethodName(VersionRestService.class, "getVersion", input.getResource().withPath(input.getVersion())).build().toUri();
			return ResponseEntity.created(location).build();
		} else if (!Strings.isNullOrEmpty(job.getResult())) {
			ApiError error = job.getResultAs(ApplicationContext.getServiceForClass(ObjectMapper.class), ApiError.class);
			throw new ApiErrorException(error);
		} else {
			throw new SnowowlRuntimeException("Version creation failed.");
		}
	}

}
