/*
 * Copyright 2021-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.resource;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.Resources;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.domain.ResourceSelectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 8.0
 */
@Tag(description = "Resources", name = "resources")
@RestController
@RequestMapping(value = "/resources", produces = { AbstractRestService.JSON_MEDIA_TYPE })
public class ResourceRestService extends AbstractRestService {
	
	public ResourceRestService() {
		super(Resource.Fields.ALL);
	}

	@Operation(
		summary = "Retrieve Resources", 
		description = "Returns a collection resource containing all/filtered registered Resources."
			+ "<p>Results are by default sorted by ID.")
	@ApiResponses({ 
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request") 
	})
	@GetMapping
	public Promise<Resources> searchByGet(@ParameterObject final ResourceRestSearch params) {
		
		return ResourceRequests.prepareSearch()
			.filterByIds(params.getId())
			.filterByResourceTypes(params.getResourceType())
			.filterByTitleExact(params.getTitleExact())
			.filterByTitle(params.getTitle())
			.filterByToolingIds(params.getToolingId())
			.filterByBundleIds(params.getBundleId())
			.filterByBundleAncestorIds(params.getBundleAncestorId())
			.filterByStatus(params.getStatus())
			.filterByUrls(params.getUrl())
			.filterByOwner(params.getOwner())
			.filterBySettings(params.getSettings())
			.setLimit(params.getLimit())
			.setExpand(params.getExpand())
			.setFields(params.getField())
			.setSearchAfter(params.getSearchAfter())
			.sortBy(extractSortFields(params.getSort()))
			.buildAsync(params.getTimestamp())
			.execute(getBus());
	}
	
	@Operation(
		summary = "Retrieve Resources", 
		description = "Returns a collection resource containing all/filtered registered Resources."
			+ "<p>Results are by default sorted by ID.")
	@ApiResponses({ 
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request") 
	})
	@PostMapping(value = "/search", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<Resources> searchByPost(@RequestBody(required = false) final ResourceRestSearch params) {
		return searchByGet(params);
	}

	@Operation(
		summary = "Retrieve resource by its unique identifier", 
		description = "Returns generic information about a single resource associated to the given unique identifier."
	)
	@ApiResponses({ 
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request"), 
		@ApiResponse(responseCode = "404", description = "Not Found") 
	})
	@GetMapping("/{resourceId}")
	public Promise<Resource> get(
		@Parameter(description = "The resource identifier") 
		@PathVariable(value = "resourceId") 
		final String resourceId,
		
		@Parameter(description = "The timestamp to use for historical ('as of') queries")
		final Long timestamp,
		
		@ParameterObject
		final ResourceSelectors selectors) {
		
		return ResourceRequests
			.prepareGet(resourceId)
			.setExpand(selectors.getExpand())
			.setFields(selectors.getField())
			.buildAsync(timestamp)
			.execute(getBus());
	}
}
