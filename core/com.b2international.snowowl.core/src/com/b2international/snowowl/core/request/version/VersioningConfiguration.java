/*
 * Copyright 2011-2023 B2i Healthcare, https://b2ihealthcare.com
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

import static com.google.common.base.Strings.nullToEmpty;

import java.time.LocalDate;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.google.common.base.MoreObjects;

/**
 * Configuration for the publication process.
 */
public final class VersioningConfiguration {

	private final String user;
	
	private final ResourceURI resource;
	private final String version;
	private final String description;
	private final LocalDate effectiveTime;
	private final boolean force;
	private final boolean childResource;
	
	public VersioningConfiguration(
			String user,
			ResourceURI resource,
			String version, 
			String description,
			LocalDate effectiveTime,
			boolean force,
			boolean childResource) {
		this.user = user;
		this.resource = resource;
		this.version = version;
		this.description = description;
		this.effectiveTime = effectiveTime;
		this.force = force;
		this.childResource = childResource;
	}
	
	public String getUser() {
		return user;
	}

	public String getVersion() {
		return version;
	}

	public LocalDate getEffectiveTime() {
		return effectiveTime;
	}

	public String getDescription() {
		return nullToEmpty(description);
	}
	
	public ResourceURI getResource() {
		return resource;
	}
	
	public boolean isForce() {
		return force;
	}
	
	public boolean isChildResource() {
		return childResource;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("resource", resource)
				.add("version", version)
				.add("effectiveTime", EffectiveTimes.format(effectiveTime))
				.add("description", nullToEmpty(description))
				.add("force", force)
				.add("childResource", childResource)
				.toString();
	}

}