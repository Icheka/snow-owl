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
package com.b2international.snowowl.fhir.tests.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemContentMode;
import com.b2international.snowowl.fhir.core.codesystems.HttpVerb;
import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.model.BatchRequest;
import com.b2international.snowowl.fhir.core.model.BatchResponse;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.FhirResource;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.Link;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.b2international.snowowl.fhir.core.model.OperationOutcomeEntry;
import com.b2international.snowowl.fhir.core.model.ParametersRequestEntry;
import com.b2international.snowowl.fhir.core.model.ParametersResponseEntry;
import com.b2international.snowowl.fhir.core.model.ResourceRequestEntry;
import com.b2international.snowowl.fhir.core.model.ResourceResponseEntry;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupResult;
import com.b2international.snowowl.fhir.core.model.codesystem.Property;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Json;
import com.b2international.snowowl.fhir.core.model.dt.SubProperty;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.tests.FhirTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;

import io.restassured.path.json.JsonPath;

/**
 * Test for validating the {@link Bundle} model object.
 * @since 8.0.0
 */
public class BundleTest extends FhirTest {
	
	@Test
	public void serializeResourceBundle() throws Exception {
		
		CodeSystem codeSystem = CodeSystem.builder("repo/shortName")
			.status(PublicationStatus.ACTIVE)
			.name("Local code system")
			.content(CodeSystemContentMode.COMPLETE)
			.url(new Uri("code system uri"))
			.build();
		
		ResourceResponseEntry entry = ResourceResponseEntry.builder()
				.fullUrl("full_Url")
				.response(BatchResponse.createOkResponse())
				.resource(codeSystem)
				.build();
		
		Bundle bundle = Bundle.builder("bundle_Id?")
			.language("en")
			.total(1)
			.type(BundleType.SEARCHSET)
			.addLink("self", "http://localhost:8080/snowowl/CodeSystem")
			.addEntry(entry)
			.build();
		
		applyFilter(bundle);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(bundle));
		
		assertThat(jsonPath.getString("resourceType"), equalTo("Bundle"));
		assertThat(jsonPath.getString("id"), equalTo("bundle_Id?"));
		assertThat(jsonPath.getString("language"), equalTo("en"));
		assertThat(jsonPath.getString("type"), equalTo("searchset"));
		assertThat(jsonPath.getInt("total"), equalTo(1));
		
		jsonPath.setRoot("link[0]");
		
		assertThat(jsonPath.getString("relation"), equalTo("self"));
		assertThat(jsonPath.getString("url"), equalTo("http://localhost:8080/snowowl/CodeSystem"));
		
		jsonPath.setRoot("entry[0]");
		
		assertThat(jsonPath.getString("fullUrl"), equalTo("full_Url"));
		assertThat(jsonPath.getString("response.status"), equalTo("200"));
		jsonPath.setRoot("entry[0].resource");
		
		assertThat(jsonPath.getString("resourceType"), equalTo("CodeSystem"));
		assertThat(jsonPath.getString("id"), equalTo("repo/shortName"));
		assertThat(jsonPath.getString("url"), equalTo("code system uri"));
		assertThat(jsonPath.getString("name"), equalTo("Local code system"));
		assertThat(jsonPath.getString("status"), equalTo("active"));
		assertThat(jsonPath.getString("content"), equalTo("complete"));
	}
	
	@Test
	public void buildRequestBundle() throws Exception {
		
		LookupRequest lookupRequest = LookupRequest.builder()
				.code("23245-4")
				.system("http://loinc.org")
				.build();
		
		Json json1 = new Parameters.Json(lookupRequest);
		
		ParametersRequestEntry entry = ParametersRequestEntry.builder()
				.request(BatchRequest.createPostRequest("CodeSystem/$lookup"))
				.resource(new Parameters.Fhir(json1.parameters()))
				.build();
			
		Bundle bundle = Bundle.builder()
			.language("en")
			.total(1)
			.type(BundleType.BATCH)
			.addLink("self", "http://localhost:8080/snowowl/CodeSystem")
			.addEntry(entry)
			.build();
		
		assertEquals("en", bundle.getLanguage().getCodeValue());
		assertEquals(1, bundle.getTotal());
		assertEquals(BundleType.BATCH.getCode(), bundle.getType());
		Link link = bundle.getLink().iterator().next();
		assertEquals("self", link.getRelation());
		assertEquals("http://localhost:8080/snowowl/CodeSystem", link.getUrl().getUriValue());
		
		Entry bundleEntry = bundle.getItems().iterator().next();
		assertTrue(bundleEntry instanceof ParametersRequestEntry);
		ParametersRequestEntry requestEntry = (ParametersRequestEntry) bundleEntry;
		BatchRequest batchRequest = requestEntry.getRequest();
		
		assertEquals(HttpVerb.POST.getCode(), batchRequest.getMethod());
		assertEquals("CodeSystem/$lookup", batchRequest.getUrl().getUriValue());
		
		Fhir requestResource = requestEntry.getRequestResource();
		
		//Back to Domain JSON...
		Json json = new Parameters.Json(requestResource);
		LookupRequest returnedLookupRequest = objectMapper.convertValue(json, LookupRequest.class);
		assertEquals("23245-4", returnedLookupRequest.getCode());
		assertEquals("http://loinc.org", returnedLookupRequest.getSystem());
			
	}
	
	@Test
	public void buildResourceRequestBundle() throws Exception {
		
		CodeSystem codeSystem = CodeSystem.builder()
				.status(PublicationStatus.ACTIVE)
				.content(CodeSystemContentMode.COMPLETE)
				.build();
		
		ResourceRequestEntry entry = ResourceRequestEntry.builder()
				.request(BatchRequest.createPostRequest("/CodeSystem"))
				.resource(codeSystem)
				.build();
		
		Bundle bundle = Bundle.builder()
				.language("en")
				.total(1)
				.type(BundleType.BATCH)
				.addEntry(entry)
				.addLink("self", "http://localhost:8080/snowowl/CodeSystem")
				.build();
		
		assertEquals("en", bundle.getLanguage().getCodeValue());
		assertEquals(1, bundle.getTotal());
		assertEquals(BundleType.BATCH.getCode(), bundle.getType());
		Link link = bundle.getLink().iterator().next();
		assertEquals("self", link.getRelation());
		assertEquals("http://localhost:8080/snowowl/CodeSystem", link.getUrl().getUriValue());
		
		Entry bundleEntry = bundle.getItems().iterator().next();
		assertTrue(bundleEntry instanceof ResourceRequestEntry);
		ResourceRequestEntry requestEntry = (ResourceRequestEntry) bundleEntry;
		BatchRequest batchRequest = requestEntry.getRequest();
		
		assertEquals(HttpVerb.POST.getCode(), batchRequest.getMethod());
		assertEquals("/CodeSystem", batchRequest.getUrl().getUriValue());
		
		FhirResource readCodeSystem = requestEntry.getRequestResource();
		assertTrue(readCodeSystem instanceof CodeSystem);
		
		String bundleString = objectMapper.writeValueAsString(bundle);
		
		Bundle readBundle = objectMapper.readValue(bundleString, Bundle.class);
		assertTrue(readBundle.getEntry().iterator().next() instanceof ResourceRequestEntry);
	}
	
	@Test
	public void deserializeRequestBundle() throws Exception {
		
		String jsonCoding =   "{ \"type\" : \"batch\", "
				+ "\"resourceType\" : \"Bundle\", "
				+ "\"entry\" : "
					+ "[{\"request\" : "
						+ "{\"method\" : \"POST\","
						+ "\"url\" : \"CodeSystem/$lookup\"},"
					+ "\"resource\" : "
						+ "{\"resourceType\" : \"Parameters\","
						+ "\"parameter\" : ["
							+ "{\"valueUri\" : \"http://loinc.org\","
							+ "\"name\" : \"system\"},"
							+ "{\"valueCode\" : \"23245-4\","
							+ "\"name\" : \"code\"}"
							+ "]"
						+ "}"
					+ "},"
					+ "{\"request\" : "
						+ "{\"method\" : \"POST\","
						+ "\"url\" : \"CodeSystem/$lookup\"},"
					+ "\"resource\" : "
						+ "{\"resourceType\" : \"Parameters\","
						+ "\"parameter\" : ["
							+ "{\"name\" : \"system\","
							+ "\"valueUri\" : \"http://snomed.info/sct\"}"
							+ ",{\"valueCode\" : \"263495000\","
							+ "\"name\" : \"code\"}"
						+ "]"
					+ "}}"
				+ "]}";
		
		Bundle bundle = objectMapper.readValue(jsonCoding, Bundle.class);
		
		assertEquals(BundleType.BATCH.getCode(), bundle.getType());
		
		Iterator<Entry> iterator = bundle.getItems().iterator();
		Entry bundleEntry = iterator.next();
		assertTrue(bundleEntry instanceof ParametersRequestEntry);
		ParametersRequestEntry requestEntry = (ParametersRequestEntry) bundleEntry;
		BatchRequest batchRequest = requestEntry.getRequest();
		
		assertEquals(HttpVerb.POST.getCode(), batchRequest.getMethod());
		assertEquals("CodeSystem/$lookup", batchRequest.getUrl().getUriValue());
		
		Fhir requestResource = requestEntry.getRequestResource();
		
		//Back to Domain JSON...
		Json json = new Parameters.Json(requestResource);
		LookupRequest returnedLookupRequest = objectMapper.convertValue(json, LookupRequest.class);
		assertEquals("23245-4", returnedLookupRequest.getCode());
		assertEquals("http://loinc.org", returnedLookupRequest.getSystem());
		
		bundleEntry = iterator.next();
		assertTrue(bundleEntry instanceof ParametersRequestEntry);
		requestEntry = (ParametersRequestEntry) bundleEntry;
		batchRequest = requestEntry.getRequest();
		
		assertEquals(HttpVerb.POST.getCode(), batchRequest.getMethod());
		assertEquals("CodeSystem/$lookup", batchRequest.getUrl().getUriValue());
		
		requestResource = requestEntry.getRequestResource();
		
		//Back to Domain JSON...
		json = new Parameters.Json(requestResource);
		returnedLookupRequest = objectMapper.convertValue(json, LookupRequest.class);
		assertEquals("263495000", returnedLookupRequest.getCode());
		assertEquals("http://snomed.info/sct", returnedLookupRequest.getSystem());
	}
	
	@Test
	public void buildResponseBundle() throws Exception {
		
		LookupResult lookupResult = LookupResult.builder()
				.name("test")
				.display("display")
				.addDesignation(Designation.builder()
						.value("dValue")
						.languageCode("uk").build())
				.addProperty(Property.builder()
						.code("1234")
						.description("propDescription")
						.valueString("stringValue")
						.addSubProperty(SubProperty.builder()
							.code("subCode")
							.description("subCodeDescription")
							.valueInteger(1)
							.build())
						.build())
				.build();
		
		
		Json json1 = new Parameters.Json(lookupResult);
		
		ParametersResponseEntry entry = ParametersResponseEntry.builder()
				.resource(new Parameters.Fhir(json1.parameters()))
				.response(BatchResponse.createOkResponse())
				.build();
		
		Bundle bundle = Bundle.builder()
			.language("en")
			.type(BundleType.BATCH_RESPONSE)
			.addLink("self", "http://localhost:8080/snowowl/CodeSystem")
			.addEntry(entry)
			.build();
		
		assertEquals("en", bundle.getLanguage().getCodeValue());
		assertEquals(BundleType.BATCH_RESPONSE.getCode(), bundle.getType());
		Link link = bundle.getLink().iterator().next();
		assertEquals("self", link.getRelation());
		assertEquals("http://localhost:8080/snowowl/CodeSystem", link.getUrl().getUriValue());
		
		Entry bundleEntry = bundle.getItems().iterator().next();
		assertTrue(bundleEntry instanceof ParametersResponseEntry);
		ParametersResponseEntry responseEntry = (ParametersResponseEntry) bundleEntry;
		assertEquals("200", responseEntry.getResponse().getStatus());
		
		Fhir requestResource = responseEntry.getResponseResource();
		
		//Back to Domain JSON...
		Json json = new Parameters.Json(requestResource);
		LookupResult returnedResponse = objectMapper.convertValue(json, LookupResult.class);
		assertEquals("test", returnedResponse.getName());
		assertEquals("display", returnedResponse.getDisplay());
		
	}
	
	@Test
	public void deserializeResponseBundle() throws Exception {
		String jsonResponse = "{\"resourceType\":\"Bundle\","
			+ "\"id\":\"ID\","
			+ "\"type\":\"batch-response\","
			+ "\"link\":["
				+ "{"
					+ "\"relation\":\"self\","
					+ "\"url\":\"https://b2ihealthcare.com\""
				+ "}"
			+ "],"
			+ "\"entry\":["
				+ "{"
					+ "\"resource\":{"
						+ "\"resourceType\":\"Parameters\","
						+ "\"parameter\":["
							+ "{"
								+ "\"name\":\"name\","
								+ "\"valueString\":\"LOINC\""
							+ "},"
							+ "{"
								+ "\"name\":\"version\","
								+ "\"valueString\":\"2.61\""
							+ "},"
							+ "{"
								+ "\"name\":\"display\","
								+ "\"valueString\":\"LOINC code label\""
							+ "},"
								+ "{"
									+ "\"name\":\"property\","
									+ "\"part\":["
										+ "{"
											+ "\"name\":\"code\","
											+ "\"valueCode\":\"parent\""
										+ "},"
										+ "{"
											+ "\"name\":\"value\","
											+ "\"valueCode\":\"Parent code\""
										+ "}"
									+ "]"
								+ "},"
								+ "{"
									+ "\"name\":\"designation\","
									+ "\"part\":["
										+ "{"
											+ "\"name\":\"language\","
											+ "\"valueCode\":\"en\""
										+ "},"
										+ "{"
											+ "\"name\":\"use\","
											+ "\"valueCoding\":{"
												+ "\"system\":\"http://snomed.info/sct\","
												+ "\"code\":\"900000000000013009\","
												+ "\"display\":\"Synonym\""
											+ "}"
										+ "},"
										+ "{"
											+ "\"name\":\"value\","
											+ "\"valueString\":\"SNOMED CT synonym\""
										+ "}"
									+ "]"
								+ "}"
							+ "]"
						+ "},"
						+ "\"response\":{"
							+ "\"status\":\"200\""
							+ "}"
						+ "}"
					+ "]"
				+ "}";
	
		Bundle bundle = objectMapper.readValue(jsonResponse, Bundle.class);
		
		assertEquals(BundleType.BATCH_RESPONSE.getCode(), bundle.getType());
		
		Iterator<Entry> iterator = bundle.getItems().iterator();
		Entry bundleEntry = iterator.next();
		assertTrue(bundleEntry instanceof ParametersResponseEntry);
		ParametersResponseEntry responseEntry = (ParametersResponseEntry) bundleEntry;
		
		Fhir responseResource = responseEntry.getResponseResource();
		
		//Back to Domain JSON...
		Json json = new Parameters.Json(responseResource);
		LookupResult lookupResult = objectMapper.convertValue(json, LookupResult.class);
		assertEquals("LOINC", lookupResult.getName());
	}
	
	@Test
	public void buildMixedBundle() throws Exception {
		
		LookupResult lookupResult = LookupResult.builder()
				.name("test")
				.display("display")
				.addDesignation(Designation.builder()
						.value("dValue")
						.languageCode("uk").build())
				.addProperty(Property.builder()
						.code("1234")
						.description("propDescription")
						.valueString("stringValue")
						.build())
				.build();
		
		OperationOutcome operationOutcome = OperationOutcome.builder()
				.addIssue(Issue.builder().code(IssueType.CODE_INVALID).diagnostics("Invalid code").severity(IssueSeverity.ERROR).build())
				.build();
		
		
		Json json1 = new Parameters.Json(lookupResult);
		
		ParametersResponseEntry lookupResultEntry = ParametersResponseEntry.builder()
				.resource(new Parameters.Fhir(json1.parameters()))
				.response(BatchResponse.createOkResponse())
				.build();
		
		OperationOutcomeEntry operationOutcomeEntry = OperationOutcomeEntry.builder().operationOutcome(operationOutcome).build();
		
		Bundle bundle = Bundle.builder()
				.language("en")
				.type(BundleType.BATCH_RESPONSE)
				.addLink("self", "http://localhost:8080/snowowl/CodeSystem")
				.addEntry(lookupResultEntry)
				.addEntry(operationOutcomeEntry)
				.build();
		
		assertEquals("en", bundle.getLanguage().getCodeValue());
		assertEquals(BundleType.BATCH_RESPONSE.getCode(), bundle.getType());
		Link link = bundle.getLink().iterator().next();
		assertEquals("self", link.getRelation());
		assertEquals("http://localhost:8080/snowowl/CodeSystem", link.getUrl().getUriValue());
		
		Iterator<Entry> iterator = bundle.getItems().iterator();
		Entry bundleEntry = iterator.next();
		assertTrue(bundleEntry instanceof ParametersResponseEntry);
		ParametersResponseEntry responseEntry = (ParametersResponseEntry) bundleEntry;
		assertEquals("200", responseEntry.getResponse().getStatus());
		
		Fhir requestResource = responseEntry.getResponseResource();
		
		//Back to Domain JSON...
		Json json = new Parameters.Json(requestResource);
		LookupResult returnedResponse = objectMapper.convertValue(json, LookupResult.class);
		assertEquals("test", returnedResponse.getName());
		assertEquals("display", returnedResponse.getDisplay());

		bundleEntry = iterator.next();
		assertTrue(bundleEntry instanceof OperationOutcomeEntry);
		OperationOutcomeEntry ooce = (OperationOutcomeEntry) bundleEntry;
		Collection<Issue> issues = ooce.getOperationOutcome().getIssues();
		assertEquals(1, issues.size());
		assertEquals(IssueSeverity.ERROR.getCode(), issues.iterator().next().getSeverity());
		
	}
	
	@Test
	public void deserializeResourceBundle() throws JsonProcessingException {
		
		FhirResource resource = CodeSystem.builder()
				.status(PublicationStatus.ACTIVE)
				.content(CodeSystemContentMode.COMPLETE)
				.build();
		
		String resourceString = objectMapper.writeValueAsString(resource);
		CodeSystem fhirResource = objectMapper.readValue(resourceString, CodeSystem.class);
		assertEquals(null, fhirResource.getId());
		
		ResourceRequestEntry entry = ResourceRequestEntry.builder()
				.resource(resource)
				.request(BatchRequest.createPostRequest("/url"))
				.build();
		
		Bundle bundle = Bundle.builder()
				.type(BundleType.BATCH)
				.entry(ImmutableList.of(entry))
				.build();
		
		String bundleString = objectMapper.writeValueAsString(bundle);
		Bundle readBundle = objectMapper.readValue(bundleString, Bundle.class);
		assertEquals(null, readBundle.getId());
		
	}
	
	@Test
	public void serialize() throws JsonProcessingException {
		
		FhirResource resource = CodeSystem.builder()
				.status(PublicationStatus.ACTIVE)
				.content(CodeSystemContentMode.COMPLETE)
				.build();
		
		String resourceString = objectMapper.writeValueAsString(resource);
		CodeSystem fhirResource = objectMapper.readValue(resourceString, CodeSystem.class);
		assertEquals(null, fhirResource.getId());
		
		ResourceRequestEntry entry = ResourceRequestEntry.builder()
				.resource(resource)
				.request(BatchRequest.createPostRequest("/url"))
				.build();
		
		Bundle bundle = Bundle.builder()
				.type(BundleType.BATCH)
				.entry(ImmutableList.of(entry))
				.build();
		
		String bundleString = objectMapper.writeValueAsString(bundle);
		Bundle readBundle = objectMapper.readValue(bundleString, Bundle.class);
		assertEquals(null, readBundle.getId());
		
	}
	
	@Test
	public void deserializeOperationOutcomeBundle() throws JsonProcessingException {
		
		OperationOutcome operationOutcome = OperationOutcome.builder()
				.addIssue(Issue.builder()
						.code(IssueType.CODE_INVALID)
						.diagnostics("Invalid code")
						.severity(IssueSeverity.ERROR)
						.build())
				.build();
		
		OperationOutcomeEntry operationOutcomeEntry = OperationOutcomeEntry.builder()
				.operationOutcome(operationOutcome)
				.response(new BatchResponse("404"))
				.build();
		
		Bundle bundle = Bundle.builder()
				.language("en")
				.type(BundleType.BATCH_RESPONSE)
				.addLink("self", "http://localhost:8080/snowowl/CodeSystem")
				.addEntry(operationOutcomeEntry)
				.build();
		
		String json = objectMapper.writeValueAsString(bundle);
		Bundle readBundle = objectMapper.readValue(json, Bundle.class);
		assertEquals("batch-response", readBundle.getType().getCodeValue());
		
	}
	
}
