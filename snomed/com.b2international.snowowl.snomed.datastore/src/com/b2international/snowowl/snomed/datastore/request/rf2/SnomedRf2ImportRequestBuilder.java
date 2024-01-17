/*
 * Copyright 2017-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.datastore.request.rf2;

import java.time.LocalDate;

import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.core.attachments.Attachment;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.io.ImportResponse;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.datastore.request.SnomedContentRequestBuilder;
import com.google.common.annotations.VisibleForTesting;

/**
 * @since 6.0.0
 */
public final class SnomedRf2ImportRequestBuilder 
		extends BaseRequestBuilder<SnomedRf2ImportRequestBuilder, BranchContext, ImportResponse> 
		implements SnomedContentRequestBuilder<ImportResponse> {

	private static final int DEFAULT_BATCH_SIZE = 60_000;
	
	private Attachment rf2Archive;
	private Rf2ReleaseType releaseType = Rf2ReleaseType.DELTA;
	private boolean createVersions = true;
	private Iterable<String> ignoreMissingReferencesIn;
	private boolean dryRun = false;
	private LocalDate importUntil;
	private int batchSize = DEFAULT_BATCH_SIZE;
	private String author;
	
	SnomedRf2ImportRequestBuilder() {
	}
	
	public SnomedRf2ImportRequestBuilder setRf2Archive(Attachment rf2Archive) {
		this.rf2Archive = rf2Archive;
		return getSelf();
	}

	public SnomedRf2ImportRequestBuilder setReleaseType(Rf2ReleaseType releaseType) {
		this.releaseType = releaseType;
		return getSelf();
	}
	
	public SnomedRf2ImportRequestBuilder setCreateVersions(boolean createVersions) {
		this.createVersions = createVersions;
		return getSelf();
	}
	
	public SnomedRf2ImportRequestBuilder setIgnoreMissingReferencesIn(Iterable<String> ignoreMissingReferencesIn) {
		this.ignoreMissingReferencesIn = ignoreMissingReferencesIn;
		return getSelf();
	}
	
	public SnomedRf2ImportRequestBuilder setDryRun(boolean dryRun) {
		this.dryRun = dryRun;
		return getSelf();
	}
	
	public SnomedRf2ImportRequestBuilder setImportUntil(String importUntil) {
		return setImportUntil(importUntil == null ? null : EffectiveTimes.parse(importUntil, DateFormats.SHORT));
	}
	
	public SnomedRf2ImportRequestBuilder setImportUntil(LocalDate importUntil) {
		this.importUntil = importUntil;
		return getSelf();
	}
	
	public SnomedRf2ImportRequestBuilder setBatchSize(Integer batchSize) {
		this.batchSize = batchSize == null ? DEFAULT_BATCH_SIZE : batchSize;
		return getSelf();
	}
	
	public SnomedRf2ImportRequestBuilder setAuthor(String author) {
		this.author = author;
		return getSelf();
	}

	@Override
	protected Request<BranchContext, ImportResponse> doBuild() {
		final SnomedRf2ImportRequest req = new SnomedRf2ImportRequest(rf2Archive);
		req.setReleaseType(releaseType);
		req.setCreateVersions(createVersions);
		req.setIgnoreMissingReferencesIn(Collections3.toImmutableSet(ignoreMissingReferencesIn));
		req.setDryRun(dryRun);
		req.setImportUntil(importUntil);
		req.setBatchSize(batchSize);
		req.setAuthor(author);
		return req;
	}

	@Override
	public boolean snapshot() {
		return false;
	}
	
	@VisibleForTesting
	public static void enableVersionsOnChildBranches() {
		SnomedRf2ImportRequest.disableVersionsOnChildBranches.set(false);
	}
	
	@VisibleForTesting
	public static void disableVersionsOnChildBranches() {
		SnomedRf2ImportRequest.disableVersionsOnChildBranches.set(true);
	}

}
