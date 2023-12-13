/*
 * Copyright 2018-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.validation.issue;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.CompareUtils;
import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;
import com.b2international.snowowl.core.plugin.ClassPathScanner;
import com.b2international.snowowl.core.repository.JsonSupport;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.b2international.snowowl.core.uri.ResourceURIPathResolver;
import com.b2international.snowowl.core.validation.ValidationRequests;
import com.b2international.snowowl.core.validation.rule.ValidationRule;
import com.b2international.snowowl.core.validation.rule.ValidationRule.Severity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @since 6.4
 */
public class ValidationIssueApiTest {

	private ServiceProvider context;
	
	@Before
	public void setup() {
		final ObjectMapper mapper = JsonSupport.getDefaultObjectMapper();
		final Index index = Indexes.createIndex(UUID.randomUUID().toString(), mapper, new Mappings(ValidationIssue.class, ValidationRule.class));
		index.admin().create();
		final ValidationRepository repository = new ValidationRepository(index);
		final ClassPathScanner scanner = new ClassPathScanner("com.b2international");
		context = ServiceProvider.EMPTY.inject()
				.bind(ClassPathScanner.class, scanner)
				.bind(ValidationRepository.class, repository)
				.bind(ValidationIssueDetailExtensionProvider.class, new ValidationIssueDetailExtensionProvider(scanner))
				.bind(ResourceURIPathResolver.class, ResourceURIPathResolver.fromMap(Map.of("SNOMEDCT", Branch.MAIN_PATH)))
				.build();
	}
	
	@After
	public void after() {
		if (context != null) {
			context.service(ValidationRepository.class).admin().delete();
			if (context instanceof IDisposableService) {
				((IDisposableService) context).dispose();
			}
		}
	}
	
	@Test
	public void filterIssueByDetails() {
		final Map<String, Object> details = ImmutableMap.of("testkey", "testvalue");
		
		final String ruleId = ValidationRequests.rules().prepareCreate()
				.setId(UUID.randomUUID().toString())
				.setToolingId("any")
				.setMessageTemplate("Error message")
				.setSeverity(Severity.ERROR)
				.setType("snomed-query")
				.setImplementation("*")
				.buildAsync()
				.getRequest()
				.execute(context);
		
		final String issueWithDetail = createIssue(ruleId, details);
		final String issueWithoutDetail = createIssue(ruleId, Collections.emptyMap()); 
		
		final ValidationIssues issues = ValidationRequests.issues().prepareSearch()
			.all()
			.filterByDetails(details)
			.buildAsync()
			.getRequest()
			.execute(context);
		
		assertThat(issues).hasSize(1);
		assertThat(issues.stream().map(ValidationIssue::getId).collect(Collectors.toList())).containsOnly(issueWithDetail);
	}
	
	@Test
	public void filterIssueByResultId() {
		final String ruleId = ValidationRequests.rules().prepareCreate()
			.setId(UUID.randomUUID().toString())
			.setToolingId("any")
			.setMessageTemplate("Error message")
			.setSeverity(Severity.ERROR)
			.setType("snomed-query")
			.setImplementation("*")
			.buildAsync()
			.getRequest()
			.execute(context);
		
		final ValidationIssue issue = new ValidationIssue(
			IDs.base62UUID(),
			"custom-validation-results",
			ruleId,
			ComponentURI.of(CodeSystem.uri("SNOMEDCT", "testBranch"), ComponentIdentifier.of("concept", "ID1")),
			false);
		
		// Add an issue with a custom result ID
		final String issueWithResultId = issue.getId();
		context.service(ValidationRepository.class).save(issue);

		// Also index an issue with the shared result ID
		createIssue(ruleId, Collections.emptyMap()); 
		
		final ValidationIssues issues = ValidationRequests.issues()
			.prepareSearch()
			.all()
			.filterByResultId("custom-validation-results")
			.buildAsync()
			.getRequest()
			.execute(context);
		
		assertThat(issues).hasSize(1);
		assertThat(issues).map(ValidationIssue::getId).containsOnly(issueWithResultId);
	}
	
	@Test
	public void filterIssueByAffectedComponentLabel_Exact() throws Exception {
		final String issueA = createIssue(Collections.emptyMap(), "A");
		final String issueB = createIssue(Collections.emptyMap(), "B");
		
		final ValidationIssues issues = ValidationRequests.issues().prepareSearch()
				.all()
				.filterByAffectedComponentLabel("a")
				.buildAsync()
				.getRequest()
				.execute(context);
		
		assertThat(issues).hasSize(1);
		assertThat(issues.stream().map(ValidationIssue::getId).collect(Collectors.toSet())).containsOnly(issueA);
	}
	
	@Test
	public void filterIssueByAffectedComponentLabel_Partial() throws Exception {
		final String issueA = createIssue(Collections.emptyMap(), "Systolic Blood Pressure");
		final String issueB = createIssue(Collections.emptyMap(), "Pressure");
		
		final ValidationIssues issues = ValidationRequests.issues().prepareSearch()
				.all()
				.filterByAffectedComponentLabel("blood")
				.buildAsync()
				.getRequest()
				.execute(context);
		
		assertThat(issues).hasSize(1);
		assertThat(issues.stream().map(ValidationIssue::getId).collect(Collectors.toSet())).containsOnly(issueA);
	}
	
	@Test
	public void filterIssueByAffectedComponentLabel_Prefix() throws Exception {
		final String issueA = createIssue(Collections.emptyMap(), "Systolic Blood Pressure");
		final String issueB = createIssue(Collections.emptyMap(), "Blood Pressure");
		
		final ValidationIssues issues = ValidationRequests.issues().prepareSearch()
				.all()
				.filterByAffectedComponentLabel("sys blo pre")
				.buildAsync()
				.getRequest()
				.execute(context);
		
		assertThat(issues).hasSize(1);
		assertThat(issues.stream().map(ValidationIssue::getId).collect(Collectors.toSet())).containsOnly(issueA);
	}
	
	@Test
	public void filterIssueByAffectedComponentLabel_MatchID() throws Exception {
		final String issueA = createIssue(Collections.emptyMap(), "A");
		final String issueB = createIssue(Collections.emptyMap(), "B");
		
		final String issueAComponentId = ValidationRequests.issues().prepareGet(issueA).buildAsync().getRequest().execute(context).getAffectedComponent().getComponentId();
		
		final ValidationIssues issues = ValidationRequests.issues().prepareSearch()
				.all()
				.filterByAffectedComponentLabel(issueAComponentId)
				.buildAsync()
				.getRequest()
				.execute(context);
		
		assertThat(issues).hasSize(1);
		assertThat(issues.stream().map(ValidationIssue::getId).collect(Collectors.toSet())).containsOnly(issueA);
	}

	private String createIssue(Map<String, Object> details, String...labels) {
		return createIssue("testRuleId", details, labels);
	}
	
	private String createIssue(String ruleId, Map<String, Object> details, String...labels) {
		final String branchPath = "testBranch";
		final String issueId = UUID.randomUUID().toString();
		final String componentId = UUID.randomUUID().toString();
		
		final ValidationIssue issue = new ValidationIssue(
			issueId,
			ruleId,
			ComponentURI.of(CodeSystem.uri("SNOMEDCT", branchPath), ComponentIdentifier.of("concept", componentId)),
			false
		);
		
		if (!CompareUtils.isEmpty(details)) {
			issue.setDetails(details);
		}
		
		if (!CompareUtils.isEmpty(labels)) {
			issue.setAffectedComponentLabels(ImmutableList.copyOf(labels));
		}
		
		context.service(ValidationRepository.class).save(issue);
		
		return issueId;
	}
	
}