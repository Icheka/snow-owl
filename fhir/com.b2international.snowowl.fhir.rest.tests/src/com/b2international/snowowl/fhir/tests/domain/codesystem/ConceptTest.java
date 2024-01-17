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
package com.b2international.snowowl.fhir.tests.domain.codesystem;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.codesystem.Concept;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.property.CodeConceptProperty;
import com.b2international.snowowl.fhir.core.model.property.ConceptProperty;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests {@link Concept}.
 * @since 6.3
 */
public class ConceptTest extends FhirTest {
	
	private Concept concept;

	@Before
	public void setup() throws Exception {
		
		concept = Concept.builder()
			.code("conceptCode")
			.definition("Definition")
			.display("Label")
			.addDesignation(Designation.builder()
				.languageCode("uk_en")
				.use(Coding.builder()
					.code("internal")
					.system("https://b2ihealthcare.com/test")
					.build()
					)
				.value("conceptLabel_uk")
				.build())
			.addProperty(CodeConceptProperty.builder()
					.code("childConcept")
					.value(new Code("childId"))
					.build())
			.build();
	}
	
	@Test
	public void build() throws Exception {
		
		assertEquals(new Code("conceptCode"), concept.getCode());
		ConceptProperty conceptProperty = concept.getProperties().iterator().next();
		assertTrue(conceptProperty instanceof CodeConceptProperty);
		
	}
	
	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(concept);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(concept));
		assertThat(jsonPath.getString("code"), equalTo("conceptCode"));
		assertThat(jsonPath.getString("display"), equalTo("Label"));
		assertThat(jsonPath.getString("definition"), equalTo("Definition"));
		assertThat(jsonPath.getString("designation[0].language"), equalTo("uk_en"));
		assertThat(jsonPath.getString("designation[0].use.code"), equalTo("internal"));
		assertThat(jsonPath.getString("property[0].code"), equalTo("childConcept"));
	}
	
	@Test
	public void deserialize() throws Exception {
		
		Concept readConcept = objectMapper.readValue(objectMapper.writeValueAsString(concept), Concept.class);
		assertEquals(new Code("conceptCode"), readConcept.getCode());
	}

}
