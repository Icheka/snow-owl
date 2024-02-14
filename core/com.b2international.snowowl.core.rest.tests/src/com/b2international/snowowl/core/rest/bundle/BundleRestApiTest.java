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

import static com.b2international.snowowl.test.commons.rest.BundleApiAssert.*;
import static com.b2international.snowowl.test.commons.rest.CodeSystemApiAssert.createCodeSystem;
import static com.b2international.snowowl.test.commons.rest.CodeSystemApiAssert.prepareCodeSystemCreateRequestBody;
import static com.b2international.snowowl.test.commons.rest.ResourceApiAssert.assertResourceGet;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.bundle.Bundle;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.CommitResult;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.rest.RestExtensions;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @since 8.0
 */
public class BundleRestApiTest {
	
	@Test
	public void createBundleNoId() {
		assertCreate(
			Json.object(
				ResourceDocument.Fields.ID, ""
			)
		).statusCode(400).body("violations", hasItem("'id' must not be empty (was '')"));
	}

	@Test
	public void createBundleNoTitle() {
		assertCreate(
			Json.object(ResourceDocument.Fields.ID, "b1")
		).statusCode(400).body("violations", hasItem("'title' must not be empty (was 'null')"));
	}

	@Test
	public void createBundleNoUrl() {
		assertCreate(
			Json.object(
				ResourceDocument.Fields.ID, "b1",
				ResourceDocument.Fields.TITLE, "Bundle title b2"
			)
		)
		.statusCode(400).body("violations", hasItem("'url' must not be empty (was 'null')"));
	}
	
	@Test
	public void createBundle_OK() throws JsonProcessingException {
		var bundleId = createBundle("b1");
		assertBundleGet(bundleId)
			.statusCode(200);
	}
	
	@Test
	public void getBundleWithTimestamp() throws Exception {
		final String id = createBundle("b16");
		
		final Bundle createdBundle = assertResourceGet(id)
			.statusCode(200)
			.extract()
			.as(Bundle.class);
		
		final String newDescription = "Updated description";
		assertUpdateBundleField(id, "description", newDescription);
		
		final Bundle updatedBundle = assertResourceGet(id)
			.statusCode(200)
			.extract()
			.as(Bundle.class);
		
		assertResourceGet(id, createdBundle.getCreatedAt() - 1L)
			.statusCode(404);
		
		// retrieve the state at creation time
		final Bundle bundle1 = assertResourceGet(id, createdBundle.getCreatedAt())
			.statusCode(200)
			.extract()
			.as(Bundle.class);
		
		assertEquals(createdBundle.getDescription(), bundle1.getDescription());
		
		// retrieve the state at the point in time when the update happened
		final Bundle bundle2 = assertResourceGet(id, updatedBundle.getUpdatedAt())
			.statusCode(200)
			.extract()
			.as(Bundle.class);
		
		assertEquals(newDescription, bundle2.getDescription());
		
		// look into the future a small amount as well
		final Bundle bundle3 = assertResourceGet(id, updatedBundle.getUpdatedAt() + 1L)
			.statusCode(200)
			.extract()
			.as(Bundle.class);
		
		assertEquals(newDescription, bundle3.getDescription());		
	}

	@Test
	public void searchBundleByNonExistentId() {
		assertBundleSearch(Map.of("id", "not-existing"))
			.body("total", equalTo(0))
			.body("items", empty());
	}
	
	@Test
	public void searchBundleById() {
		createBundle(prepareBundleCreateRequestBody("b2"));
		assertBundleSearch(Map.of("id", Set.of("b2")))
			.body("items", hasItem(hasEntry("id", "b2")));
	}

	@Test
	public void searchBundleByTitle() {
		createBundle(prepareBundleCreateRequestBody("b3").with("title", "Unique bundle title"));
		
		//Exact case insensitive match
		assertBundleSearch(Map.of("title", "unique bundle title")).and()
			.body("items", hasItem(hasEntry("id", "b3")))
			.body("items", hasItem(hasEntry("title", "Unique bundle title")))
			.assertThat();

		//Prefix match
		assertBundleSearch(Map.of("title", "uni bun tit")).and()
			.body("items", hasItem(hasEntry("id", "b3")))
			.body("items", hasItem(hasEntry("title", "Unique bundle title")))
			.assertThat();

		//All term match 
		assertBundleSearch(Map.of("title", "bundle unique title")).and()
			.body("items", hasItem(hasEntry("id", "b3")))
			.body("items", hasItem(hasEntry("title", "Unique bundle title")))
			.assertThat();

		//Boolean prefix match
		assertBundleSearch(Map.of("title", "Unique bundle ti")).and()
			.body("items", hasItem(hasEntry("id", "b3")))
			.body("items", hasItem(hasEntry("title", "Unique bundle title")))
			.assertThat();
	}
	
	@Test
	public void searchBundleExpandResources() {
		final String rootBundleId = "rootBundleId";
		final String subBundleId = "subBundleId";
		
		createBundle(prepareBundleCreateRequestBody(rootBundleId));
		createBundle(prepareBundleCreateRequestBody(subBundleId, rootBundleId));
		createCodeSystem(prepareCodeSystemCreateRequestBody("cs1").with("bundleId", rootBundleId));
		createCodeSystem(prepareCodeSystemCreateRequestBody("cs2").with("bundleId", rootBundleId));
		createCodeSystem(prepareCodeSystemCreateRequestBody("cs3").with("bundleId", subBundleId));
		
		assertBundleSearch(Map.of("id", Set.of(rootBundleId), "expand", "resources()")).and()
			.body("total", equalTo(1))
			.body("items[0].resources.total", equalTo(3))
			.body("items[0].resources.items", hasItem(hasEntry("id", "cs1")))
			.body("items[0].resources.items", hasItem(hasEntry("id", "cs2")))
			.body("items[0].resources.items", hasItem(hasEntry("id", subBundleId)))
			.assertThat();
	}
	
	@Test
	public void searchBundleWithTimestamp() throws Exception {
		final long timestamp1 = createBundleAndGetTimestamp("b17");
		final long timestamp2 = createBundleAndGetTimestamp("b18");
		final long timestamp3 = createBundleAndGetTimestamp("b19");
		
		assertBundleSearch(idsWithTimestamp(timestamp1 - 1L, "b17", "b18", "b19")).statusCode(200).body("items", empty());
		assertBundleSearch(idsWithTimestamp(timestamp1 + 0L, "b17", "b18", "b19")).statusCode(200).body("items.id", containsInAnyOrder("b17"));
		assertBundleSearch(idsWithTimestamp(timestamp2 - 1L, "b17", "b18", "b19")).statusCode(200).body("items.id", containsInAnyOrder("b17"));
		assertBundleSearch(idsWithTimestamp(timestamp2 + 0L, "b17", "b18", "b19")).statusCode(200).body("items.id", containsInAnyOrder("b17", "b18"));
		assertBundleSearch(idsWithTimestamp(timestamp3 - 1L, "b17", "b18", "b19")).statusCode(200).body("items.id", containsInAnyOrder("b17", "b18"));
		assertBundleSearch(idsWithTimestamp(timestamp3 + 0L, "b17", "b18", "b19")).statusCode(200).body("items.id", containsInAnyOrder("b17", "b18", "b19"));
		assertBundleSearch(idsWithTimestamp(timestamp3 + 1L, "b17", "b18", "b19")).statusCode(200).body("items.id", containsInAnyOrder("b17", "b18", "b19"));
	}

	private static long createBundleAndGetTimestamp(final String id) {
		final CommitResult commitResult = ResourceRequests.bundles()
			.prepareCreate()
			.setId(id)
			.setTitle("Bundle " + id)
			.setDescription("description")
			.setLanguage("en")
			.setUrl("https://b2ihealthcare.com/" + id)
			.setBundleId(IComponent.ROOT_ID)
			.build(RestExtensions.USER, "Created bundle " + id)
			.execute(Services.bus())
			.getSync(1L, TimeUnit.MINUTES);
			
		return commitResult.getCommitTimestamp();
	}

	private static Map<String, Object> idsWithTimestamp(final long timestamp, final String... ids) {
		return Map.of(
			"timestamp", timestamp,
			"id", List.of(ids));
	}

	@Test
	public void updateBundleTitle() {
		final String id = "b4";
		createBundle(prepareBundleCreateRequestBody(id));
		assertUpdateBundleField(id, "title", "new bundle title");
	}

	@Test
	public void updateBundleUrl() {
		final String id = "b5";
		createBundle(prepareBundleCreateRequestBody(id));
		assertUpdateBundleField(id, "url", "new bundle url");
	}

	@Test
	public void updateBundleLanguage() {
		final String id = "b6";
		createBundle(prepareBundleCreateRequestBody(id));
		assertUpdateBundleField(id, "language", "hu");
	}
	
	@Test
	public void updateBundleDescription() {
		final String id = "b7";
		createBundle(prepareBundleCreateRequestBody(id));
		assertUpdateBundleField(id, "description", "Bundle `Hungarian` resources");
	}
	
	@Test
	public void updateBundleStaus() {
		final String id = "b8";
		createBundle(prepareBundleCreateRequestBody(id));
		assertUpdateBundleField(id, "status", "draft");
	}
	
	@Test
	public void updateBundleCopyright() {
		final String id = "b9";
		createBundle(prepareBundleCreateRequestBody(id));
		assertUpdateBundleField(id, "copyright", "Licensed under the Fictive License 2.0");
	}
	
	@Test
	public void updateBundleOwner() {
		final String id = "b10";
		createBundle(prepareBundleCreateRequestBody(id));
		assertUpdateBundleField(id, "owner", "B2i");
	}
	
	@Test
	public void updateBundleContact() {
		final String id = "b11";
		createBundle(prepareBundleCreateRequestBody(id));
		assertUpdateBundleField(id, "contact", "info@b2ihealthcare.com");
	}
	
	@Test
	public void updateBundleUsage() {
		final String id = "b12";
		createBundle(prepareBundleCreateRequestBody(id));
		assertUpdateBundleField(id, "usage", "For testing");
	}
	
	@Test
	public void updateBundlePurpose() {
		final String id = "b13";
		createBundle(prepareBundleCreateRequestBody(id));
		assertUpdateBundleField(id, "purpose", "Test bundle REST API update endpoint");
	}
	
	@Test
	public void updateBundleBundleId() {
		final String id = "b14";
		final String newBundleId = IDs.base62UUID();
		
		createBundle(prepareBundleCreateRequestBody(id));
		createBundle(prepareBundleCreateRequestBody(newBundleId));
		
		assertUpdateBundleField(id, "bundleId", newBundleId);
	}
	
	@Test
	public void updateBundleBundleIdNotExist() {
		final String id = "b15";
		createBundle(prepareBundleCreateRequestBody(id));
		assertUpdateBundle(id, Json.object("bundleId", "not-existing-id"))
			.statusCode(400);
	}	
}
