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
package com.b2international.snowowl.core.rest.bundle;

import java.util.concurrent.TimeUnit;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.bundle.Bundle;
import com.b2international.snowowl.core.bundle.Bundles;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.CoreApiConfig;
import com.b2international.snowowl.core.rest.domain.ResourceRequest;
import com.b2international.snowowl.core.rest.domain.ResourceSelectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 8.0
 */
@Tag(description = "Bundles", name = CoreApiConfig.BUNDLES)
@RestController
@RequestMapping("/bundles")
public class BundleRestService extends AbstractRestService {
	
	public BundleRestService() {
		super(ResourceDocument.Fields.SORT_FIELDS);
	}
	
	@Operation(
		summary="Retrieve bundles", 
		description="Returns a collection resource containing all/filtered registered bundles."
			+ "<p>Results are by default sorted by ID."
			+ "<p>The following properties can be expanded:"
			+ "<p>"
			+ "&bull; resources() &ndash; this list of resources this bundle contains"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request")
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<Bundles> searchByGet(@ParameterObject final BundleRestSearch params) {
		
		return ResourceRequests.bundles().prepareSearch()
			.filterByIds(params.getId())
			.filterByTitle(params.getTitle())
			.setLimit(params.getLimit())
			.setExpand(params.getExpand())
			.setFields(params.getField())
			.setSearchAfter(params.getSearchAfter())
			.sortBy(extractSortFields(params.getSort()))
			.buildAsync(params.getTimestamp())
			.execute(getBus());
	}
	
	@Operation(
		summary="Retrieve bundles", 
		description="Returns a collection resource containing all/filtered registered bundles."
			+ "<p>Results are by default sorted by ID."
			+ "<p>The following properties can be expanded:"
			+ "<p>"
			+ "&bull; resources() &ndash; this list of resources this bundle contains"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Invalid search config"),
	})
	@PostMapping(value="/search", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<Bundles> searchByPost(@RequestBody(required = false) final BundleRestSearch params) {
		return searchByGet(params);
	}
	
	@Operation(
		summary="Retrieve bundle by its unique identifier",
		description="Returns generic information about a single bundle associated to the given unique identifier."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "Not found")
	})
	@GetMapping(value = "/{bundleId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<Bundle> get(
		@Parameter(description="The bundle identifier")
		@PathVariable(value="bundleId", required = true) 
		final String bundleId,

		@ParameterObject
		final ResourceSelectors selectors) {
		
		return ResourceRequests.bundles().prepareGet(Bundle.uri(bundleId))
			.setExpand(selectors.getExpand())
			.setFields(selectors.getField())
			.buildAsync()
			.execute(getBus());
	}
	
	@Operation(
		summary="Retrieve bundle by its unique identifier",
		description="Returns generic information about a single bundle associated to the given unique identifier."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "Not found")
	})
	@GetMapping(value = "/{bundleId}/{versionId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<Bundle> getVersioned(
		@Parameter(description="The bundle identifier")
		@PathVariable(value="bundleId", required = true) 
		final String bundleId,
		
		@Parameter(description="The bundle version")
		@PathVariable(value="versionId", required = true) 
		final String versionId,

		@ParameterObject
		final ResourceSelectors selectors) {
		
		return ResourceRequests.bundles().prepareGet(Bundle.uri(bundleId, versionId))
			.setExpand(selectors.getExpand())
			.setFields(selectors.getField())
			.buildAsync()
			.execute(getBus());
	}
	
	@Operation(
		summary="Create a bundle",
		description="Create a new bundle with the given parameters"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Created"),
		@ApiResponse(responseCode = "400", description = "Invalid input arguments"),
		@ApiResponse(responseCode = "409", description = "Bundle already exists in the system")
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(
			@RequestBody
			final ResourceRequest<BundleRestCreate> body,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		
		final String commitComment = body.getCommitComment() == null ? String.format("Created new bundle %s", body.getChange().getTitle()) : body.getCommitComment();
		
		final String codeSystemId = body.getChange().toCreateRequest()
				.build(author, commitComment)
				.execute(getBus())
				.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES)
				.getResultAs(String.class);
		
		return ResponseEntity.created(getResourceLocationURI(codeSystemId)).build();
	}
	
	@Operation(
			summary = "Update a bundle",
			description="Update a bundle with the given parameters")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "No content"),
		@ApiResponse(responseCode = "400", description = "Bad Request")
	})
	@PutMapping(value = "/{bundleId}", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(
			@Parameter(description = "The bundle identifier")
			@PathVariable(value="bundleId") 
			final String bundleId,
			
			@RequestBody
			final ResourceRequest<BundleRestUpdate> body,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		
		final String commitComment = body.getCommitComment() == null ? String.format("Update bundle %s", getBundleTitle(bundleId, body)) : body.getCommitComment();
		
		body.getChange().toUpdateRequest(bundleId)
				.build(author, commitComment)
				.execute(getBus())
				.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
	}

	private String getBundleTitle(final String bundleId, final ResourceRequest<BundleRestUpdate> body) {
		return body.getChange().getTitle() != null ? body.getChange().getTitle() : ResourceRequests.bundles().prepareGet(bundleId).buildAsync().execute(getBus()).getSync(1, TimeUnit.MINUTES).getTitle();
	}
	
	@Operation(
			summary = "Delete a bundle",
			description = "Delete a bundle with the given parameters")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Deletion successful"),
		@ApiResponse(responseCode = "409", description = "Bundle cannot be deleted")
	})
	@DeleteMapping(value = "/{bundleId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@Parameter(description = "The bundle identifier")
			@PathVariable(value="bundleId") 
			final String bundleId,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		try {
			final Bundle bundle = ResourceRequests.bundles().prepareGet(bundleId)
					.buildAsync()
					.execute(getBus())
					.getSync(1, TimeUnit.MINUTES);
			
			ResourceRequests.bundles().prepareDelete(bundleId)
				.commit()
				.setAuthor(author)
				.setCommitComment(String.format("Deleted Bundle %s", bundle.getTitle()))
				.buildAsync()
				.execute(getBus())
				.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
		} catch(NotFoundException e) {
			// already deleted, ignore error
		}
	}
}
