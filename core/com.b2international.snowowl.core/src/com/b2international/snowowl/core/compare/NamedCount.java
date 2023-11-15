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
package com.b2international.snowowl.core.compare;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * A name an associated counter value.
 * 
 * <p>
 * The actual meaning of the numeric value is dependent on the context.
 * 
 * @since 9.0
 */
public record NamedCount(String name, int count) {

	public Map<String, Object> asMap() {
		return ImmutableMap.of("name", name, "count", count);
	}
	
	@Override
	public String toString() {
		return String.join("=", name, String.valueOf(count));
	}
	
}