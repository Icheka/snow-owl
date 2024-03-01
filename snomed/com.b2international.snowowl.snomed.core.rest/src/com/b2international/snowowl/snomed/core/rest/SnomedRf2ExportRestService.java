/*
 * Copyright 2020-2024 B2i Healthcare, https://b2ihealthcare.com
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

import java.io.File;

import org.elasticsearch.common.Strings;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.b2international.commons.http.AcceptLanguageHeader;
import com.b2international.snowowl.core.attachments.Attachment;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.attachments.InternalAttachmentRegistry;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.SnomedApiConfig;
import com.b2international.snowowl.snomed.core.domain.Rf2MaintainerType;
import com.b2international.snowowl.snomed.core.domain.Rf2RefSetExportLayout;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedRf2ExportConfiguration;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 7.5
 */
@Tag(name = SnomedApiConfig.EXPORT)
@Controller
@RequestMapping(value="/{path:**}/export")
public class SnomedRf2ExportRestService extends AbstractRestService {

	@Autowired
	private AttachmentRegistry attachments;
	
	@Operation(
		summary="Export SNOMED CT content to RF2", 
		description="Exports SNOMED CT content from the given branch to RF2."
	)
	@ApiResponses({
		@ApiResponse(responseCode="200", description="OK")
	})
	@GetMapping
	public @ResponseBody ResponseEntity<?> export(
			@Parameter(description = "The branch path", required = true)
			@PathVariable(value="path")
			final String branch,

			@ParameterObject
			final SnomedRf2ExportConfiguration params,
			
			@Parameter(description = "Accepted language tags, in order of preference", example = AcceptLanguageHeader.DEFAULT_ACCEPT_LANGUAGE_HEADER)
			@RequestHeader(value=HttpHeaders.ACCEPT_LANGUAGE, defaultValue = AcceptLanguageHeader.DEFAULT_ACCEPT_LANGUAGE_HEADER, required=false) 
			final String acceptLanguage) {
		
		final Attachment exportedFile = SnomedRequests.rf2().prepareExport()
			.setReleaseType(params.getType() == null ? null : Rf2ReleaseType.getByNameIgnoreCase(params.getType()))
			.setExtensionOnly(params.isExtensionOnly())
			.setLocales(acceptLanguage)
			.setIncludePreReleaseContent(params.isIncludeUnpublished())
			.setModules(params.getModuleIds())
			.setRefSets(params.getRefSetIds())
			.setCountryNamespaceElement(params.getNamespaceId())
			.setMaintainerType(Strings.isNullOrEmpty(params.getMaintainerType()) ? null : Rf2MaintainerType.getByNameIgnoreCase(params.getMaintainerType()))
			.setNrcCountryCode(params.getNrcCountryCode())
			// .setNamespaceFilter(namespaceFilter) is not supported on REST, yet
			.setTransientEffectiveTime(params.getTransientEffectiveTime())
			.setStartEffectiveTime(params.getStartEffectiveTime())
			.setEndEffectiveTime(params.getEndEffectiveTime())
			.setRefSetExportLayout(params.getRefSetLayout() == null ? null : Rf2RefSetExportLayout.getByNameIgnoreCase(params.getRefSetLayout()))
			.setComponentTypes(params.getComponentTypes())
			.build(branch)
			.execute(getBus())
			.getSync();
		
		final File file = ((InternalAttachmentRegistry) attachments).getAttachment(exportedFile.getAttachmentId());
		final Resource exportZipResource = new FileSystemResource(file);
		
		final HttpHeaders httpHeaders = new HttpHeaders();
		
		httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		httpHeaders.setContentDispositionFormData("attachment", exportedFile.getFileName());

		// TODO figure out a smart way to cache export results, probably it could be tied to commitTimestamps/versions/etc. 
		file.deleteOnExit();
		return new ResponseEntity<>(exportZipResource, httpHeaders, HttpStatus.OK);
	}
	
}
