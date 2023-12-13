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
package com.b2international.snowowl.fhir.tests.domain.structuredefinition;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.structuredefinition.MappingElement;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link MappingElement}
 * @since 8.0.0
 */
public class MappingElementTest extends FhirTest {
	
	private MappingElement mappingElement;

	@Before
	public void setup() throws Exception {
		
		mappingElement = MappingElement.builder()
				.id("id")
				.identity("identity")
				.map("map")
				.comment("comment")
				.language("en")
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(mappingElement);
	}

	private void validate(MappingElement mappingElement) {
		assertEquals("id", mappingElement.getId());
		assertEquals("identity", mappingElement.getIdentity().getIdValue());
		assertEquals("map", mappingElement.getMap());
		assertEquals("comment", mappingElement.getComment());
		assertEquals("en", mappingElement.getLanguage().getCodeValue());
		
	}

	@Test
	public void serialize() throws Exception {
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(mappingElement));
		assertThat(jsonPath.getString("id"), equalTo("id"));
		assertThat(jsonPath.getString("identity"), equalTo("identity"));
		assertThat(jsonPath.getString("map"), equalTo("map"));
		assertThat(jsonPath.getString("comment"), equalTo("comment"));
		assertThat(jsonPath.getString("language"), equalTo("en"));
	}
	
	@Test
	public void deserialize() throws Exception {
		MappingElement readMappingElement = objectMapper.readValue(objectMapper.writeValueAsString(mappingElement), MappingElement.class);
		validate(readMappingElement);
	}
}
