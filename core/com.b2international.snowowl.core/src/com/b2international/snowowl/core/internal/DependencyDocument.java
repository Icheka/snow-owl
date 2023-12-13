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
package com.b2international.snowowl.core.internal;

import java.util.Objects;

import com.b2international.index.Doc;
import com.b2international.snowowl.core.ResourceURIWithQuery;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 8.12.0
 */
@Doc
public final class DependencyDocument implements Comparable<DependencyDocument>, DependencyEntry {

	private final ResourceURIWithQuery uri;
	private final String scope;
	
	@JsonCreator
	public DependencyDocument(@JsonProperty("uri") ResourceURIWithQuery uri, @JsonProperty("scope") String scope) {
		this.uri = Objects.requireNonNull(uri);
		this.scope = scope;
	}
	
	@Override
	public ResourceURIWithQuery getUri() {
		return uri;
	}
	
	@Override
	public String getScope() {
		return scope;
	}
	
	@Override
	public int compareTo(DependencyDocument other) {
		return COMPARATOR.compare(this, other);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(uri, scope);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		DependencyDocument other = (DependencyDocument) obj;
		return Objects.equals(uri, other.uri) && Objects.equals(scope, other.scope);
	}
	
	public DependencyDocument withScope(final String newScope) {
		return Objects.equals(scope, newScope) ? this : new DependencyDocument(uri, newScope);
	}
	
}
