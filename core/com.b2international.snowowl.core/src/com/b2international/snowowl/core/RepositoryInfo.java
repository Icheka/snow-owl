/*
 * Copyright 2017-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import com.b2international.index.es.client.EsIndexStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @since 5.8
 */
@JsonDeserialize(as = RepositoryInfo.Default.class)
public interface RepositoryInfo {

	/**
	 * @return the ID of the repository
	 */
	@JsonProperty
	String id();
	
	/**
	 * @returns the {@link Health health} state for this repository. The repository is considered healthy if it is in {@link Health#GREEN} state.
	 */
	@JsonProperty
	Health health();
	
	/**
	 * @return the diagnosis message if {@link #health()} is not {@link Health#GREEN}. 
	 */
	@JsonProperty
	default String diagnosis() {
		return null;
	}
	
	@JsonProperty
	List<EsIndexStatus> indices();
	
	/**
	 * @since 5.8 
	 */
	enum Health {
		RED, YELLOW, GREEN;
	}
	
	/**
	 * @since 5.8
	 */
	class Default implements RepositoryInfo, Serializable {

		private static final long serialVersionUID = 1L;
		
		private final String id;
		private final Health health;
		private final String diagnosis;
		private final List<EsIndexStatus> indices;

		@JsonCreator
		private Default(
			@JsonProperty("id") String id,
			@JsonProperty("health") Health health,
			@JsonProperty("diagnosis") String diagnosis,
			@JsonProperty("indices") List<EsIndexStatus> indices
		) {
			this.id = id;
			this.health = health;
			this.diagnosis = diagnosis;
			this.indices = indices;
		}
		
		@Override
		public String id() {
			return id;
		}

		@Override
		public Health health() {
			return health;
		}
		
		@Override
		public String diagnosis() {
			return diagnosis;
		}
		
		@Override
		public List<EsIndexStatus> indices() {
			return indices;
		}

		@Override
		public int hashCode() {
			return Objects.hash(diagnosis, health, id, indices);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Default other = (Default) obj;
			return Objects.equals(diagnosis, other.diagnosis) && health == other.health && Objects.equals(id, other.id)
					&& Objects.equals(indices, other.indices);
		}
		
	}
	
	static RepositoryInfo of(String id, Health health, String diagnosis, List<EsIndexStatus> indices) {
		return new Default(id, health, diagnosis, indices);
	}
	
	static RepositoryInfo of(RepositoryInfo info) {
		return of(info.id(), info.health(), info.diagnosis(), info.indices());
	}
	
}
