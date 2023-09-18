/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.r5;

/**
 * Stores keys for temporary user data that is stored on model objects. 
 * 
 * @since 9.0
 */
public abstract class ResourceConstants {

	public static final String CURRENT_PAGE_ID = "currentPageId";
	public static final String NEXT_PAGE_ID = "nextPageId";
	
	private ResourceConstants() {
		// This class is not supposed to be instantiated
	}
}
