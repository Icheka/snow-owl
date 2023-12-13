/*
 * Copyright 2011-2019 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.cis.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.5
 */
public class DeprecationData extends RequestData {

	@JsonProperty("sctid")
	private String sctId;

	public DeprecationData(final String namespace, final String software, final String sctId) {
		super(namespace, software);
		this.sctId = sctId;
	}
	
	@JsonCreator
	public DeprecationData(
			@JsonProperty("namespace") final int namespace, 
			@JsonProperty("software") final String software, 
			@JsonProperty("sctid") final String sctId) {
		super(namespace, software);
		this.sctId = sctId;
	}

	public String getSctId() {
		return sctId;
	}

	public void setSctId(String sctId) {
		this.sctId = sctId;
	}

}
