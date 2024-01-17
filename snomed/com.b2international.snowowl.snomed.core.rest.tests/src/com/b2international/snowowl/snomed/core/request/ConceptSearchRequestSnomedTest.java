/*
 * Copyright 2020-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.core.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.Concept;
import com.b2international.snowowl.core.domain.Concepts;
import com.b2international.snowowl.snomed.common.SnomedConstants;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.SnomedContentRule;

/**
 * @since 7.5
 */
public class ConceptSearchRequestSnomedTest {

	private static final ResourceURI CODESYSTEM = SnomedContentRule.SNOMEDCT.withPath("2018-01-31");
	
	private static final String ID = "105590001";
	private static final String PT = "Substance";
	private static final String FSN = "Substance (substance)";
	
	@Test
	public void hitCount() throws Exception {
		Concepts matches = CodeSystemRequests.prepareSearchConcepts()
			.setLimit(0)
			.filterByCodeSystemUri(CODESYSTEM)
			.buildAsync()
			.execute(Services.bus())
			.getSync();
		assertThat(matches.getTotal()).isEqualTo(1888);
	}
	
	@Test
	public void filterById() throws Exception {
		Concepts matches = CodeSystemRequests.prepareSearchConcepts()
			.one()
			.filterByCodeSystemUri(CODESYSTEM)
			.filterById(SnomedConstants.Concepts.ROOT_CONCEPT)
			.buildAsync()
			.execute(Services.bus())
			.getSync();
		assertThat(matches).hasSize(1);
	}
	
	@Test
	public void filterByQuery() throws Exception {
		Concepts matches = CodeSystemRequests.prepareSearchConcepts()
			.setLimit(0)
			.filterByCodeSystemUri(CODESYSTEM)
			.filterByQuery("*")
			.filterByExclusion(SnomedConstants.Concepts.ROOT_CONCEPT)
			.buildAsync()
			.execute(Services.bus())
			.getSync();
		assertThat(matches.getTotal()).isEqualTo(1887);
	}
	
	@Test
	public void setPreferreDisplayToFsn() throws Exception {
		Concepts matches = CodeSystemRequests.prepareSearchConcepts()
				.filterByCodeSystemUri(CODESYSTEM)
				.filterById(ID)
				.setPreferredDisplay("FSN")
				.setLocales("en")
				.buildAsync()
				.execute(Services.bus())
				.getSync();
		assertThat(matches.getTotal()).isEqualTo(1);
		final Concept concept = matches.first().get();
		assertThat(concept.getTerm()).isEqualTo(FSN);
	}
	
	@Test
	public void useDefaultDisplay() throws Exception {
		Concepts matches = CodeSystemRequests.prepareSearchConcepts()
				.filterByCodeSystemUri(CODESYSTEM)
				.filterById(ID)
				.setLocales("en")
				.buildAsync()
				.execute(Services.bus())
				.getSync();
		assertThat(matches.getTotal()).isEqualTo(1);
		final Concept concept = matches.first().get();
		assertThat(concept.getTerm()).isEqualTo(PT);
	}
	
}
