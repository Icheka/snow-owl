/*
 * Copyright 2017-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request.version;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.SystemRequestBuilder;

/**
 * @since 5.7
 */
public final class VersionCreateRequestBuilder 
		extends BaseRequestBuilder<VersionCreateRequestBuilder, ServiceProvider, Boolean>
		implements SystemRequestBuilder<Boolean> {

	private String version;
	private String description;
	private LocalDate effectiveTime;
	private ResourceURI resource;
	private boolean force = false;

	public VersionCreateRequestBuilder setResource(ResourceURI resource) {
		this.resource = resource;
		return getSelf();
	}
	
	public VersionCreateRequestBuilder setDescription(String description) {
		this.description = description;
		return getSelf();
	}
	
	/**
	 * The version effective time that the recently changed components will get during versioning. Format: yyyyMMdd.
	 * @param effectiveTime
	 * @return
	 */
	public VersionCreateRequestBuilder setEffectiveTime(String effectiveTime) {
		return setEffectiveTime(LocalDate.parse(effectiveTime, DateTimeFormatter.BASIC_ISO_DATE));
	}
	
	public VersionCreateRequestBuilder setEffectiveTime(LocalDate effectiveTime) {
		this.effectiveTime = effectiveTime;
		return getSelf();
	}
	
	public VersionCreateRequestBuilder setVersionId(String versionId) {
		this.version = versionId;
		return getSelf();
	}
	
	public VersionCreateRequestBuilder setForce(boolean force) {
		this.force = force;
		return getSelf();
	}
	
	@Override
	protected Request<ServiceProvider, Boolean> doBuild() {
		final VersionCreateRequest req = new VersionCreateRequest();
		req.version = version;
		req.description= description;
		req.effectiveTime = effectiveTime;
		req.resource = resource;
		req.force = force;
		return req;
	}

}