/*
 * Copyright 2011-2024 B2i Healthcare, https://b2ihealthcare.com
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

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.b2international.commons.exceptions.ApiError;
import com.b2international.commons.exceptions.ApiErrorException;
import com.b2international.commons.validation.ApiValidation;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.jobs.JobRequests;
import com.b2international.snowowl.core.jobs.RemoteJobEntry;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.domain.ResourceRequest;
import com.b2international.snowowl.core.version.Version;
import com.b2international.snowowl.core.version.VersionDocument;
import com.b2international.snowowl.core.version.Versions;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 1.0
 */
@Tag(description = "Resources", name = "resources")
@RestController
@RequestMapping(value = "/versions")
public class VersionRestService extends AbstractRestService {
	
	public VersionRestService() {
		super(VersionDocument.Fields.SORT_FIELDS);
	}
	
	@Operation(
		summary="Retrieve all resource versions",
		description="Returns a list containing all published resource versions."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Invalid search config")
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<Versions> searchVersionsByGet(
			@ParameterObject
			VersionRestSearch params) {
		return ResourceRequests.prepareSearchVersion()
				.filterByIds(params.getId())
				.filterByResourceTypes(params.getResourceType())
				.filterByResources(params.getResource())
				.filterByCreatedAt(params.getCreatedAt())
				.filterByCreatedAt(params.getCreatedAtFrom(), params.getCreatedAtTo())
				.filterByVersionIds(params.getVersion())
				.setLimit(params.getLimit())
				.setExpand(params.getExpand())
				.setFields(params.getField())
				.setSearchAfter(params.getSearchAfter())
				.sortBy(extractSortFields(params.getSort()))
				.buildAsync()
				.execute(getBus());
		
	}
	
	@Operation(
		summary="Retrieve all resource versions",
		description="Returns a list containing all published resource versions."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Invalid search config")
	})
	@PostMapping(value = "/search", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<Versions> searchVersionsByPost(@RequestBody(required = false) VersionRestSearch params) {
		return searchVersionsByGet(params);
	}

	@Operation(
		summary="Retrieve a resource version by identifier (<resourceType/resourceId/version>)",
		description="Returns a published resource version for the specified resource with the given version identifier."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "Version Not Found")
	})
	@GetMapping(value = "/{versionUri:**}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<Version> getVersion(
			@Parameter(description="The resource version uri")
			@PathVariable(value="versionUri") 
			final String versionUri) {
		return ResourceRequests.prepareGetVersion(versionUri).buildAsync().execute(getBus());
	}
	
	@Operation(
		summary="Create a new resource version",
		description="Creates a new resource version. "
				+ "The version tag (represented by an empty branch) is created on the resource's current working branch. "
				+ "Where applicable, effective times are set on the unpublished content as part of this operation."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Created"),
		@ApiResponse(responseCode = "404", description = "Not found"),
		@ApiResponse(responseCode = "409", description = "Code system version conflicts with existing branch")
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(value = HttpStatus.CREATED)
	public ResponseEntity<Void> createVersion(
			@Parameter(description="Version parameters")
			@RequestBody final ResourceRequest<VersionRestInput> input,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		final VersionRestInput change = input.getChange();
		ApiValidation.checkInput(change);
		
		String newVersionUri = String.join(Branch.SEPARATOR, change.getResource().toString(), change.getVersion());
		
		String jobId = ResourceRequests.prepareNewVersion()
				.setResource(change.getResource())
				.setVersion(change.getVersion())
				.setDescription(change.getDescription())
				.setEffectiveTime(change.getEffectiveTime())
				.setForce(change.isForce())
				.setCommitComment(input.getCommitComment())
				.setAuthor(author)
				.buildAsync()
				.runAsJobWithRestart(ResourceRequests.versionJobKey(change.getResource()), "Creating version " + newVersionUri)
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES);
		
		RemoteJobEntry job = JobRequests.waitForJob(getBus(), jobId, 500);
		
		if (job.isSuccessful()) {
			final URI location = MvcUriComponentsBuilder.fromMethodName(VersionRestService.class, "getVersion", newVersionUri).build().toUri();
			return ResponseEntity.created(location).build();
		} else if (!Strings.isNullOrEmpty(job.getResult())) {
			ApiError error = job.getResultAs(ApplicationContext.getServiceForClass(ObjectMapper.class), ApiError.class);
			throw new ApiErrorException(error.withMessage(error.getMessage().replace("Branch name", "Version")));
		} else {
			throw new SnowowlRuntimeException("Version creation failed.");
		}
	}

}
