/*
 * Copyright 2021-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.bundle;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.TestMethodNameRule;
import com.b2international.snowowl.test.commons.rest.RestExtensions;

/**
 * @since 8.0
 */
abstract class BaseBundleApiTest {
	
	static final String USER = RestExtensions.USER;
	static final String ROOT = IComponent.ROOT_ID;
	static final String URL_PREFIX = SnomedTerminologyComponentConstants.SNOMED_URI_DEV + "/";
	static final String TITLE = "Bundle title";
	static final String LANGUAGE = "en";
	static final String DESCRIPTION = "Bundle to group resources";
	static final String STATUS = "draft";
	static final String COPYRIGHT = "copyright";
	static final String OWNER = "b2i";
	static final String CONTACT = "info@b2ihealthcare.com";
	static final String USAGE = "Bundle testing sources";
	static final String PURPOSE = "Testing purpose";
	static final Map<String, Object> SETTINGS = Map.of("ownerProfileName", "owner");

	@Rule 
	public final TestMethodNameRule testName = new TestMethodNameRule();

	String id;
	
	@Before
	public void setup() {
		this.id = testName.get();
	}
	
	@After
	public void cleanUp() {
		ResourceRequests.bundles().prepareSearch()
			.buildAsync()
			.execute(Services.bus())
			.getSync(1, TimeUnit.MINUTES)
			.forEach(bundle -> {
				ResourceRequests.prepareDelete(bundle.getResourceURI())
					.force(true)
					.build(USER, String.format("Delete bundle: %s", bundle.getId()))
					.execute(Services.bus())
					.getSync(1, TimeUnit.MINUTES);
			});
	}

	Bundle getBundle() {
		return getBundle(id);
	}
	
	Bundle getBundle(final String id) {
		return ResourceRequests.bundles().prepareGet(id)
				.buildAsync()
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES);
	}
	
	String createBundle(final String id) {
		return createBundle(id, ROOT, String.join("_", id, TITLE));
	}

	String createBundle() {
		return createBundle(id, ROOT, String.join("_", id, TITLE));
	}
	
	String createBundle(final String id, final String bundleId, final String title) {
		return ResourceRequests.bundles().prepareCreate()
				.setId(id)
				.setUrl(URL_PREFIX + id)
				.setTitle(title)
				.setLanguage(LANGUAGE)
				.setDescription(DESCRIPTION)
				.setStatus(STATUS)
				.setCopyright(COPYRIGHT)
				.setOwner(OWNER)
				.setContact(CONTACT)
				.setUsage(USAGE)
				.setPurpose(PURPOSE)
				.setSettings(SETTINGS)
				.setBundleId(bundleId)
				.build(USER, String.format("Create bundle: %s", id))
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES)
				.getResultAs(String.class);
	}

	String createCodeSystem(final String bundleId, final String title) {
		return CodeSystemRequests.prepareNewCodeSystem()
				.setTitle(title)
				.setBundleId(bundleId)
				.setUrl(URL_PREFIX + title)
				.setToolingId(SnomedTerminologyComponentConstants.TOOLING_ID)
				.build(USER, String.format("Create code system: %s", id))
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES)
				.getResultAs(String.class);

	}
	
	Bundles execute(final BundleSearchRequestBuilder builder) {
		return builder.buildAsync()
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES);
	}
	
	Stream<String> executeThenExtractIds(final BundleSearchRequestBuilder builder) {
		return builder.buildAsync()
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES)
				.stream()
				.map(Bundle::getId);
	}
}
