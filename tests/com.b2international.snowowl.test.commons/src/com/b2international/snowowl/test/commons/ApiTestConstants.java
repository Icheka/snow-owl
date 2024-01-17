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
package com.b2international.snowowl.test.commons;

import java.util.concurrent.TimeUnit;

/**
 * @since 7.15
 */
public interface ApiTestConstants {

	/**
	 * The context-relative base URL for the administrative controller. 
	 */
//	String ADMIN_API = "/admin";
	
	/**
	 * The context-relative base URL for the codesystems controller.
	 */
	String CODESYSTEMS_API = "/codesystems";
	
	/**
	 * The context-relative base URL for the version controller.
	 */
	String VERSIONS_API = "/versions";
	
	/**
	 * The context-relative base URL for the resource controller.
	 */
	String RESOURCES_API = "/resources";

	/**
	 * The context-relative base URL for the bundle controller.
	 */
	String BUNDLE_API = "/bundles";
	
	/**
	 * The context-relative base URL for the suggest controller.
	 */
	String SUGGEST_API = "/suggest";
	
	/**
	 * The context-relative base URL for SNOMED CT-related controllers.
	 */
	String SCT_API = "/snomedct";
	
	long POLL_INTERVAL = TimeUnit.MILLISECONDS.toMillis(200L);

	long POLL_TIMEOUT = TimeUnit.SECONDS.toMillis(120L);
	
}
