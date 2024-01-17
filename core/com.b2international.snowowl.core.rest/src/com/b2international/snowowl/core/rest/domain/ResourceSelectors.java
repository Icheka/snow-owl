/*
 * Copyright 2021 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.rest.domain;

import java.util.List;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * @since 8.0
 */
public class ResourceSelectors {

	@Parameter(description = "Expansion parameters")
	private List<String> expand;
	
	@Parameter(description = "Field selection")
	private List<String> field;
	
	public final List<String> getExpand() {
		return expand;
	}

	public final void setExpand(List<String> expand) {
		this.expand = expand;
	}
	
	public List<String> getField() {
		return field;
	}
	
	public void setField(List<String> field) {
		this.field = field;
	}

}
