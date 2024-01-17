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
package com.b2international.snowowl.fhir.tests.domain.capabilitystatement;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.ResourceType;
import com.b2international.snowowl.fhir.core.codesystems.SearchParamType;
import com.b2international.snowowl.fhir.core.model.capabilitystatement.Interaction;
import com.b2international.snowowl.fhir.core.model.capabilitystatement.Operation;
import com.b2international.snowowl.fhir.core.model.capabilitystatement.Resource;
import com.b2international.snowowl.fhir.core.model.capabilitystatement.SearchParam;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Resource}
 * @since 8.0.0
 */
public class ResourceTest extends FhirTest {
	
	private Resource resource;

	@Before
	public void setup() throws Exception {
		
		resource = Resource.builder()
				.type(ResourceType.CODESYSTEM)
				.profile("profile")
				.addOperation(Operation.builder()
						.name("name")
						.definition("definition")
						.documentation("documentation")
						.build())
				.addReferencePolicy(new Code("referencePolicy"))
				.addSearchInclude("searchInclude")
				.addSearchParam(SearchParam.builder()
						.definition("definition")
						.documentation("documentation")
						.name("name")
						.type(SearchParamType.STRING)
						.build())
				.addSearchRevInclude("searchRevInclude")
				.addSearchInclude("searchInclude")
				.addInteraction(Interaction.builder()
						.code("code")
						.documentation("documentation")
						.build())
				.addSupportedProfile(new Uri("supportedProfile"))
				.conditionalCreate(true)
				.conditionalDelete(new Code("conditionalDelete"))
				.conditionalRead(new Code("conditionalRead"))
				.conditionalUpdate(true)
				.documentation("documentation")
				.readHistory(false)
				.updateCreate(true)
				.versioning("versioning")
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(resource);
	}
	
	private void validate(Resource resource) {
		assertEquals(ResourceType.CODESYSTEM.getCode(), resource.getType());
		assertEquals("profile", resource.getProfile().getUriValue());
		assertEquals("referencePolicy", resource.getReferencePolicies().iterator().next().getCodeValue());
		assertEquals("name", resource.getOperations().iterator().next().getName());
		
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(resource);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(resource));
		assertThat(jsonPath.getString("type"), equalTo("CodeSystem"));
	}
	
	@Test
	public void deserialize() throws Exception {
		Resource readResource = objectMapper.readValue(objectMapper.writeValueAsString(resource), Resource.class);
		validate(readResource);
	}

}
