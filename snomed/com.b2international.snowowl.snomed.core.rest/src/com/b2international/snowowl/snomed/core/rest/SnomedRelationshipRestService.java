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
package com.b2international.snowowl.snomed.core.rest;

import java.util.concurrent.TimeUnit;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.b2international.commons.http.AcceptLanguageHeader;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.SnomedApiConfig;
import com.b2international.snowowl.core.rest.domain.ResourceSelectors;
import com.b2international.snowowl.snomed.core.domain.RelationshipValue;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedRelationshipRestInput;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedRelationshipRestSearch;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedRelationshipRestUpdate;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedResourceRequest;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 1.0
 */
@Tag(description="Relationships", name = SnomedApiConfig.RELATIONSHIPS)
@RestController
@RequestMapping(value = "/{path:**}/relationships")		
public class SnomedRelationshipRestService extends AbstractRestService {

	public SnomedRelationshipRestService() {
		super(SnomedRelationship.Fields.ALL);
	}
	
	@Operation(
		summary="Retrieve Relationships from a path", 
		description="Returns a list with all/filtered Relationships from a path."
				+ "<p>The following properties can be expanded:"
				+ "<p>"
				+ "&bull; type() &ndash; the relationship's type concept<br>"
				+ "&bull; source() &ndash; the relationship's source concept<br>"
				+ "&bull; destination() &ndash; the relationship's destination concept<br>"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Invalid search config"),
		@ApiResponse(responseCode = "404", description = "Branch not found")
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<SnomedRelationships> searchByGet(
			@Parameter(description = "The resource path", required = true)
			@PathVariable(value="path")
			final String path,

			@ParameterObject
			final SnomedRelationshipRestSearch params,
			
			@Parameter(description = "Accepted language tags, in order of preference", example = AcceptLanguageHeader.DEFAULT_ACCEPT_LANGUAGE_HEADER)
			@RequestHeader(value="Accept-Language", defaultValue = AcceptLanguageHeader.DEFAULT_ACCEPT_LANGUAGE_HEADER, required=false) 
			final String acceptLanguage) {
		return SnomedRequests
					.prepareSearchRelationship()
					.filterByIds(params.getId())
					.filterByActive(params.getActive())
					.filterByModules(params.getModule())
					.filterByNamespaces(params.getNamespace())
					.filterByNamespaceConcepts(params.getNamespaceConceptId())
					.filterByEffectiveTime(params.getEffectiveTime())
					.filterByCharacteristicType(params.getCharacteristicType())
					.filterBySources(params.getSource())
					.filterByTypes(params.getType())
					.filterByDestinations(params.getDestination())
					.filterByGroup(params.getGroup())
					.filterByUnionGroup(params.getUnionGroup())
					.filterByValueType(params.getValueType())
					.filterByValue(params.getOperator(), RelationshipValue.fromLiteral(params.getValue()))
					.isActiveMemberOf(params.getIsActiveMemberOf())
					.setLimit(params.getLimit())
					.setSearchAfter(params.getSearchAfter())
					.setExpand(params.getExpand())
					.setFields(params.getField())
					.setLocales(acceptLanguage)
					.sortBy(extractSortFields(params.getSort()))
					.build(path)
					.execute(getBus());
	}
	
	@Operation(
		summary="Retrieve Relationships from a path", 
		description="Returns a list with all/filtered Relationships from a path."
				+ "<p>The following properties can be expanded:"
				+ "<p>"
				+ "&bull; type() &ndash; the relationship's type concept<br>"
				+ "&bull; source() &ndash; the relationship's source concept<br>"
				+ "&bull; destination() &ndash; the relationship's destination concept<br>"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Invalid search config"),
		@ApiResponse(responseCode = "404", description = "Branch not found")
	})
	@PostMapping(value="/search", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<SnomedRelationships> searchByPost(
			@Parameter(description = "The resource path", required = true)
			@PathVariable(value="path")
			final String path,
	
			@RequestBody(required = false)
			final SnomedRelationshipRestSearch params,
		
			@Parameter(description = "Accepted language tags, in order of preference", example = AcceptLanguageHeader.DEFAULT_ACCEPT_LANGUAGE_HEADER)
			@RequestHeader(value="Accept-Language", defaultValue = AcceptLanguageHeader.DEFAULT_ACCEPT_LANGUAGE_HEADER, required=false) 
			final String acceptLanguage) {
		return searchByGet(path, params, acceptLanguage);
	}
	
	@Operation(
		summary="Create Relationship", 
		description="Creates a new Relationship directly on a version path."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Created"),
		@ApiResponse(responseCode = "404", description = "Branch not found")
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(
			@Parameter(description = "The resource path", required = true)
			@PathVariable("path") 
			final String path,
			
			@Parameter(description = "Relationship parameters")
			@RequestBody 
			final SnomedResourceRequest<SnomedRelationshipRestInput> body,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {

		final SnomedRelationshipRestInput change = body.getChange();
		final String commitComment = body.getCommitComment();
		final String defaultModuleId = body.getDefaultModuleId();
		
		final String createdRelationshipId = change.toRequestBuilder()
				.commit()
				.setDefaultModuleId(defaultModuleId)
				.setAuthor(author)
				.setCommitComment(commitComment)
				.build(path)
				.execute(getBus())
				.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES)
				.getResultAs(String.class);
				
		return ResponseEntity.created(getResourceLocationURI(path, createdRelationshipId)).build();
	}

	@Operation(
		summary="Retrieve Relationship properties", 
		description="Returns all properties of the specified Relationship, including the associated refinability value."
				+ "<p>The following properties can be expanded:"
				+ "<p>"
				+ "&bull; type() &ndash; the relationship's type concept<br>"
				+ "&bull; source() &ndash; the relationship's source concept<br>"
				+ "&bull; destination() &ndash; the relationship's destination concept<br>"
				+ "&bull; characteristicType() &ndash; the relationship's characteristic type concept<br>"
				+ "&bull; modifier() &ndash; the relationship's modifier concept<br>"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "Branch or Relationship not found")
	})
	@GetMapping(value = "/{relationshipId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<SnomedRelationship> read(
			@Parameter(description = "The resource path", required = true)
			@PathVariable("path") 
			final String path,
			
			@Parameter(description = "The Relationship identifier")
			@PathVariable("relationshipId") 
			final String relationshipId,
			
			@ParameterObject
			final ResourceSelectors selectors,
			
			@Parameter(description = "Accepted language tags, in order of preference", example = AcceptLanguageHeader.DEFAULT_ACCEPT_LANGUAGE_HEADER)
			@RequestHeader(value="Accept-Language", defaultValue = AcceptLanguageHeader.DEFAULT_ACCEPT_LANGUAGE_HEADER, required=false) 
			final String acceptLanguage) {

		return SnomedRequests.prepareGetRelationship(relationshipId)
					.setExpand(selectors.getExpand())
					.setFields(selectors.getField())
					.setLocales(acceptLanguage)
					.build(path)
					.execute(getBus());
	}

	@Operation(
		summary="Update Relationship",
		description="Updates properties of the specified Relationship."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "No content"),
		@ApiResponse(responseCode = "404", description = "Branch or Relationship not found")
	})
	@PutMapping(value = "/{relationshipId}", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(
			@Parameter(description = "The resource path", required = true)
			@PathVariable("path") 
			final String path,
			
			@Parameter(description = "The Relationship identifier")
			@PathVariable("relationshipId") 
			final String relationshipId,
			
			@Parameter(description = "Update Relationship parameters")
			@RequestBody 
			final SnomedResourceRequest<SnomedRelationshipRestUpdate> body,
			
			@Parameter(description = "Force update flag")
			@RequestParam(value = "force", defaultValue="false", required=false)
			final Boolean force,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {

		final String commitComment = body.getCommitComment();
		final SnomedRelationshipRestUpdate update = body.getChange();
		final String defaultModuleId = body.getDefaultModuleId();

		update.toRequestBuilder(relationshipId)
			.force(force)
			.commit()
			.setDefaultModuleId(defaultModuleId)
			.setAuthor(author)
			.setCommitComment(commitComment)
			.build(path)
			.execute(getBus())
			.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
	}

	@Operation(
		summary="Delete Relationship",
		description="Permanently removes the specified unreleased Relationship and related components.<p>If the Relationship "
				+ "has already been released, it can not be removed and a <code>409</code> "
				+ "status will be returned."
				+ "<p>The force flag enables the deletion of a released Relationship. "
				+ "Deleting published components is against the RF2 history policy so"
				+ " this should only be used to remove a new component from a release before the release is published.</p>"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Delete successful"),
		@ApiResponse(responseCode = "404", description = "Branch or Relationship not found"),
		@ApiResponse(responseCode = "409", description = "Relationship cannot be deleted")
	})
	@DeleteMapping(value = "/{relationshipId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@Parameter(description = "The resource path", required = true)
			@PathVariable("path") 
			final String path,
			
			@Parameter(description = "The Relationship identifier")
			@PathVariable("relationshipId") 
			final String relationshipId,

			@Parameter(description = "Force deletion flag")
			@RequestParam(value = "force", defaultValue="false", required=false)
			final Boolean force,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {

		SnomedRequests.prepareDeleteRelationship(relationshipId)
			.force(force)
			.commit()
			.setAuthor(author)
			.setCommitComment(String.format("Deleted Relationship '%s' from store.", relationshipId))
			.build(path)
			.execute(getBus())
			.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
	}

}
