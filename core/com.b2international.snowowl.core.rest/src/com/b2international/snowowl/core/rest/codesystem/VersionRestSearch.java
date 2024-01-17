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
package com.b2international.snowowl.core.rest.codesystem;

import java.util.List;

import com.b2international.snowowl.core.rest.domain.ObjectRestSearch;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * @since 8.0
 */
public class VersionRestSearch extends ObjectRestSearch {

	@Parameter(description="The corresponding resource identifier(s) to match")
	private List<String> resource;
	
	@Parameter(description = "The types of resources to get the versions for")
	private List<String> resourceType;
	
	@Parameter(description = "Exact match filter for the created at field")
	private Long createdAt;
	
	@Parameter(description = "Greater than equal to filter for the created at field")
	private Long createdAtFrom;
	
	@Parameter(description = "Less than equal filter to for the created at field")
	private Long createdAtTo;

	@Parameter(description = "The corresponding version identifier(s) to match")
	private List<String> version;
	
	public List<String> getResource() {
		return resource;
	}
	
	public void setResource(List<String> resource) {
		this.resource = resource;
	}

	public List<String> getResourceType() {
		return resourceType;
	}

	public void setResourceType(List<String> resourceType) {
		this.resourceType = resourceType;
	}

	public Long getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}
	
	public Long getCreatedAtFrom() {
		return createdAtFrom;
	}

	public void setCreatedAtFrom(Long createdAtFrom) {
		this.createdAtFrom = createdAtFrom;
	}

	public Long getCreatedAtTo() {
		return createdAtTo;
	}

	public void setCreatedAtTo(Long createdAtTo) {
		this.createdAtTo = createdAtTo;
	}

	public List<String> getVersion() {
		return version;
	}
	
	public void setVersion(List<String> version) {
		this.version = version;
	}
}
