/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.suggestion;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @since 8.0
 */
public class SuggestionRestParameters {
	
	@Parameter(description = "Code System path to find the concept in.")
	private String codeSystemPath;
	
	@Parameter(description = "The term to match")
	private String term;
	
	@Parameter(description = "The maximum number of items to return", example = "50", schema = @Schema(defaultValue = "50"))
	private int limit = 50;
	
	@Parameter(description = "The preferred display (FSN, PT or ID_ONLY)")
	private String preferredDisplay = "PT";

	public String getCodeSystemPath() {
		return codeSystemPath;
	}
	
	public void setCodeSystemPath(String codeSystemPath) {
		this.codeSystemPath = codeSystemPath;
	}
	
	public String getTerm() {
		return term;
	}
	
	public void setTerm(String term) {
		this.term = term;
	}
	
	public int getLimit() {
		return limit;
	}
	
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	public String getPreferredDisplay() {
		return preferredDisplay;
	}
	
	public void setPreferredDisplay(String preferredDisplay) {
		this.preferredDisplay = preferredDisplay;
	}
}
