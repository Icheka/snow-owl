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
package com.b2international.snowowl.test.commons.codesystem;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.version.Version;
import com.b2international.snowowl.core.version.Versions;
import com.b2international.snowowl.test.commons.ApiTestConstants;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

/**
 * @since 4.7
 */
public abstract class CodeSystemVersionRestRequests {

	public static Optional<Version> getLatestVersion(String codeSystemId) {
		return givenAuthenticatedRequest(ApiTestConstants.VERSIONS_API)
				.when()
				.queryParams(Map.of(
					"resource", CodeSystem.uri(codeSystemId).toString(),
					"sort", "effectiveTime:desc",
					"limit", 1
				))
				.get()
				.then()
				.extract()
				.as(Versions.class)
				.first();
	}
	
	public static Version getVersion(String codeSystemId, String version) {
		return assertGetVersion(codeSystemId, version)
				.statusCode(200)
				.extract()
				.as(Version.class);
	}
	
	public static Versions searchVersion(String codeSystemId, String version, String expand) {
		return givenAuthenticatedRequest(ApiTestConstants.VERSIONS_API)
				.when()
				.queryParams(Map.of(
					"id", Branch.get(codeSystemId, version),
					"expand", expand,
					"limit", Integer.MAX_VALUE
				))
				.get()
				.then()
				.extract()
				.as(Versions.class);
	}
	
	public static Versions searchVersionByPost(String codeSystemId, String version, String expand) {
		return givenAuthenticatedRequest(ApiTestConstants.VERSIONS_API)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.body(Json.object(
					"id", Json.array(Branch.get(codeSystemId, version)),
					"expand", Json.array(expand),
					"limit", Integer.MAX_VALUE
				))
				.post("/search")
				.then()
				.extract()
				.as(Versions.class);
	}
	
	public static ValidatableResponse assertGetVersion(String codeSystemId, String version) {
		return givenAuthenticatedRequest(ApiTestConstants.VERSIONS_API)
				.get("/{id}/{version}", codeSystemId, version)
				.then();
	}

	public static ValidatableResponse createVersion(String codeSystemId, LocalDate effectiveTime) {
		return createVersion(codeSystemId, effectiveTime.toString(), effectiveTime);
	}
	
	public static ValidatableResponse createVersion(String codeSystemId, String version, LocalDate effectiveTime) {
		return createVersion(codeSystemId, version, effectiveTime, false);
	}
	
	public static ValidatableResponse createVersion(String codeSystemId, String version, LocalDate effectiveTime, boolean force) {
		return createVersion(CodeSystem.uri(codeSystemId), version, effectiveTime, force);
	}
	
	public static ValidatableResponse createVersion(ResourceURI resourceToVersion, String version, LocalDate effectiveTime, boolean force) {
		return givenAuthenticatedRequest(ApiTestConstants.VERSIONS_API)
				.contentType(ContentType.JSON)
				.body(Json.object(
					"resource", resourceToVersion.toString(),
					"version", version,
					"description", version,
					"effectiveTime", effectiveTime.toString(),
					"force", force
				))
				.post()
				.then();
	}
	
	public static ValidatableResponse createVersion(String codeSystemId, String version, LocalDate effectiveTime, Map<String,?> headers) {
		return givenAuthenticatedRequest(ApiTestConstants.VERSIONS_API)
				.contentType(ContentType.JSON)
				.headers(headers)
				.body(Json.object(
						"resource", CodeSystem.uri(codeSystemId).toString(),
						"version", version,
						"description", version,
						"effectiveTime", effectiveTime.toString()
						))
				.post()
				.then();
	}

	public static ValidatableResponse createVersion(String codeSystemId, String version, String description, LocalDate effectiveTime) {
		return givenAuthenticatedRequest(ApiTestConstants.VERSIONS_API)
				.contentType(ContentType.JSON)
				.body(Json.object(
					"resource", CodeSystem.uri(codeSystemId).toString(),
					"version", version,
					"description", description,
					"effectiveTime", effectiveTime.toString()
				))
				.post()
				.then();
	}
	
	public static Versions getVersions(String codeSystemId) {
		return givenAuthenticatedRequest(ApiTestConstants.VERSIONS_API)
				.when()
				.queryParams(Map.of(
					"resource", CodeSystem.uri(codeSystemId).toString(),
					"limit", Integer.MAX_VALUE
				))
				.get()
				.then()
				.extract()
				.as(Versions.class);
	}

	public static LocalDate getNextAvailableEffectiveDate(String codeSystemId) {
		// XXX make sure we always use today or later dates for versions
		// This ensures that all versions created by tests will be in chronological order, even if some of the pre-imported content we are relying on is in the past
		// and adding one day to that historical version would mean a historical effective time version, which is unfortunate and can lead to inconsistencies in tests
		LocalDate today = LocalDate.now();
		return getLatestVersion(codeSystemId)
				.map(Version::getEffectiveTime)
				.map(latestVersion -> latestVersion.isBefore(today) ? today : latestVersion.plus(1, ChronoUnit.DAYS))
				.orElse(today);
	}

	private CodeSystemVersionRestRequests() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
