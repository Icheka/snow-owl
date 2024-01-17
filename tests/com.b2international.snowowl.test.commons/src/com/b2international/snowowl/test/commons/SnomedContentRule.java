/*
 * Copyright 2011-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.test.commons;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.rules.ExternalResource;

import com.b2international.snowowl.core.Dependency;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.attachments.Attachment;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.codesystem.CodeSystems;
import com.b2international.snowowl.core.jobs.JobRequests;
import com.b2international.snowowl.core.jobs.RemoteJobEntry;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.request.SearchResourceRequest.Sort;
import com.b2international.snowowl.core.util.PlatformUtil;
import com.b2international.snowowl.core.version.Version;
import com.b2international.snowowl.core.version.VersionDocument;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.request.rf2.SnomedRf2Requests;
import com.b2international.snowowl.test.commons.rest.RestExtensions;
import com.google.common.collect.Lists;

/**
 * JUnit test rule to import SNOMED CT content during automated tests.
 * 
 * @since 3.6
 */
public class SnomedContentRule extends ExternalResource {

	public static final String SNOMEDCT_ID = "SNOMEDCT";
	public static final String SNOMEDCT_COMPLEX_MAP_BLOCK_EXT_ID = "SNOMEDCT-COMPLEX-MAP-BLOCK";
	public static final String SNOMEDCT_OID = "2.16.840.1.113883.6.96";
	
	public static final ResourceURI SNOMEDCT = CodeSystem.uri(SNOMEDCT_ID);
	public static final ResourceURI SNOMEDCT_COMPLEX_MAP_BLOCK_EXT = CodeSystem.uri(SNOMEDCT_COMPLEX_MAP_BLOCK_EXT_ID);
	
	private final Path importArchive;
	private final Rf2ReleaseType contentType;
	private final ResourceURI codeSystemId;
	
	private String importUntil;
	private ResourceURI extensionOf;
	private boolean createVersions = true;

	public SnomedContentRule(final ResourceURI codeSystemId, final String importArchivePath, final Rf2ReleaseType contentType) {
		this(codeSystemId, SnomedContentRule.class, importArchivePath, contentType);
	}
	
	public SnomedContentRule(final ResourceURI codeSystemId, final Class<?> relativeClass, final String importArchivePath, final Rf2ReleaseType contentType) {
		this(codeSystemId, relativeClass, false, importArchivePath, contentType);
	}
	
	public SnomedContentRule(final ResourceURI codeSystemId, final Class<?> relativeClass, final boolean isFragment, final String importArchivePath, final Rf2ReleaseType contentType) {
		this.codeSystemId = checkNotNull(codeSystemId, "codeSystem");
		this.contentType = checkNotNull(contentType, "contentType");
		this.importArchive = isFragment ? PlatformUtil.toAbsolutePath(relativeClass, importArchivePath) : PlatformUtil.toAbsolutePathBundleEntry(relativeClass, importArchivePath);
	}
	
	public SnomedContentRule importUntil(String importUntil) {
		this.importUntil = importUntil;
		return this;
	}
	
	public SnomedContentRule extensionOf(ResourceURI extensionOf) {
		this.extensionOf = extensionOf;
		return this;
	}
	
	public SnomedContentRule createVersions(boolean createVersions) {
		this.createVersions = createVersions;
		return this;
	}

	@Override
	protected void before() throws Throwable {
		createCodeSystemIfNotExist();
		Attachment attachment = Attachment.upload(Services.context(), importArchive);
		String jobId = SnomedRequests.rf2().prepareImport()
			.setRf2Archive(attachment)
			.setReleaseType(contentType)
			.setCreateVersions(createVersions)
			.setImportUntil(importUntil)
			.build(codeSystemId)
			.runAsJobWithRestart(SnomedRf2Requests.importJobKey(codeSystemId), "Importing RF2 content into " + codeSystemId)
			.execute(Services.bus())
			.getSync(1, TimeUnit.MINUTES);
		RemoteJobEntry job = JobRequests.waitForJob(Services.bus(), jobId, 2000 /* 2 seconds */);
		assertTrue("Failed to import RF2 archive", job.isSuccessful());
	}
	
	private void createCodeSystemIfNotExist() {
		final IEventBus eventBus = Services.bus();
		final CodeSystems codeSystems = CodeSystemRequests.prepareSearchCodeSystem()
				.setLimit(0)
				.filterById(codeSystemId.getResourceId())
				.buildAsync()
				.execute(eventBus)
				.getSync();

		if (codeSystems.getTotal() > 0) {
			return;
		}
		
		final ResourceURI extensionOf = Optional.ofNullable(this.extensionOf).or(() -> {
			return ResourceRequests.prepareSearchVersion()
					.filterByResource(SNOMEDCT)
					.sortBy(Sort.fieldDesc(VersionDocument.Fields.EFFECTIVE_TIME))
					.setLimit(1)
					.buildAsync()
					.execute(eventBus)
					.getSync()
					.first()
					.map(Version::getVersionResourceURI);
			})
			.orElse(null);
			
		CodeSystemRequests.prepareNewCodeSystem()
			.setId(codeSystemId.getResourceId())
			.setBranchPath(SNOMEDCT.equals(codeSystemId) ? Branch.MAIN_PATH : null)
			.setUrl(SNOMEDCT.equals(codeSystemId) ? SnomedTerminologyComponentConstants.SNOMED_URI_SCT + "/" + Concepts.MODULE_SCT_CORE : SnomedTerminologyComponentConstants.SNOMED_URI_SCT + "/" + codeSystemId.getResourceId())
			.setDescription("description")
			.setDependencies(extensionOf == null ? null : List.of(Dependency.of(extensionOf, TerminologyResource.DependencyScope.EXTENSION_OF)))
			.setLanguage("ENG")
			.setTitle(codeSystemId.getResourceId())
			.setOid("oid:" + codeSystemId)
			.setOwner("owner")
			.setContact("https://b2ihealthcare.com")
			.setToolingId(SnomedTerminologyComponentConstants.TOOLING_ID)
			.setSettings(Map.of(
				"publisher", "SNOMED International",
				SnomedTerminologyComponentConstants.CODESYSTEM_LANGUAGE_CONFIG_KEY, List.of(
					Map.of(
						"languageTag", "en",
						"languageRefSetIds", Lists.newArrayList(Concepts.REFSET_LANGUAGE_TYPE_UK, Concepts.REFSET_LANGUAGE_TYPE_US)
					)
				)
			))
			.build(RestExtensions.USER, String.format("Create code system %s", codeSystemId))
			.execute(eventBus)
			.getSync(1, TimeUnit.MINUTES);
	}
}
