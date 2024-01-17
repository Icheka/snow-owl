/*
 * Copyright 2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.test.commons.collections;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.assertCreated;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;

import java.util.Map;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

/**
 * @since 9.0.0
 */
public class TerminologyResourceCollectionRestRequests {

	public static final String COLLECTIONS_API = "/collections";
	
	public static ValidatableResponse assertTerminologyResourceCollectionCreate() {
		return assertTerminologyResourceCollectionCreate(prepareTerminologyResourceCollectionCreateBody(IDs.base62UUID()));
	}

	public static ValidatableResponse assertTerminologyResourceCollectionCreate(Json body) {
		return givenAuthenticatedRequest(COLLECTIONS_API)
			.contentType(ContentType.JSON)
			.body(body)
			.post()
			.then();
	}

	public static Json prepareTerminologyResourceCollectionCreateBody(String collectionId) {
		return Json.object(
			"id", collectionId,
			"title", "Title of " + collectionId,
			"url", collectionId,
			"description", "<div>Markdown supported</div>",
			"toolingId", SnomedTerminologyComponentConstants.TOOLING_ID,
			"oid", "oid_" + collectionId,
			"language", "ENG",
			"owner", "owner",
			"contact", "https://b2ihealthcare.com"
		);
	}
	
	public static ValidatableResponse assertTerminologyResourceCollectionGet(String collectionId, String...expand) {
		return givenAuthenticatedRequest(COLLECTIONS_API)
				.accept(ContentType.JSON)
				.queryParam("expand", expand == null ? null : String.join(",", expand))
				.get("/{id}", collectionId)
				.then().assertThat();
	}
	
	public static ValidatableResponse assertTerminologyResourceCollectionSearch(Map<String, Object> filters) {
		return givenAuthenticatedRequest(COLLECTIONS_API)
				.accept(ContentType.JSON)
				.queryParams(filters)
				.get()
				.then().assertThat();
	}
	
	public static String createTerminologyResourceCollection() {
		return assertCreated(assertTerminologyResourceCollectionCreate());
	}
	
	public static String createTerminologyResourceCollection(Json body) {
		return assertCreated(assertTerminologyResourceCollectionCreate(body));
	}
	
}
