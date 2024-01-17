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

import com.b2international.snowowl.fhir.core.codesystems.IdentifierUse;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.codesystems.QuantityComparator;
import com.b2international.snowowl.fhir.core.model.ContactDetail;
import com.b2international.snowowl.fhir.core.model.dt.*;
import com.b2international.snowowl.fhir.core.model.structuredefinition.*;
import com.b2international.snowowl.fhir.core.model.typedproperty.StringProperty;
import com.b2international.snowowl.fhir.core.model.usagecontext.QuantityUsageContext;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link StructureDefinition}
 * @since 8.0.0
 */
public class StructureDefinitionTest extends FhirTest {
	
	private StructureDefinition structureDefinition;

	@Before
	public void setup() throws Exception {
		
		structureDefinition = StructureDefinition.builder("id")
			.addContact(ContactDetail.builder()
					.name("name")
					.addTelecom(ContactPoint.builder()
							.period(Period.builder().build())
							.rank(1)
							.system("system")
							.value("value")
							.build())
					.build())
			.addContext("context")
			.addContextInvariant("contextInvariant")
			.contextType(new Code("contextTypeCode"))
			.addIdentifier(Identifier.builder()
			.use(IdentifierUse.OFFICIAL)
					.system(new Uri("www.hl7.org"))
					.value("OID:1234.1234")
					.build())
			.addJurisdiction(CodeableConcept.builder()
				.addCoding(Coding.builder()
						.code("codingCode")
						.display("codingDisplay")
						.build())
				.text("codingText")
				.build())
			.addKeyword(Coding.builder()
				.code("keywordCode")
				.display("keywordDisplay")
				.build())
			.addUseContext(QuantityUsageContext.builder()
					.code(Coding.builder()
							.code("coding")
							.display("codingDisplay")
							.build())
					.value(Quantity.builder()
							.code("valueCode")
							.unit("ms")
							.value(Double.valueOf(1))
							.comparator(QuantityComparator.GREATER_THAN)
							.build())
					.id("usageContextId")
					.build())
			.baseDefinition("baseDefinition")
			.copyright("copyright")
			.date(TEST_DATE_STRING)
			.derivation("derivation")
			.description("description")
			.differential(StructureView.builder()
						.addElementDefinition(ElementDefinition.builder()
								.defaultValue(new StringProperty("defValue"))
								.addAlias("alias")
								.addCode(Coding.builder()
									.code("coding")
									.display("codingDisplay")
									.build())
								.addCondition(new Id("condition"))
								.addMapping(MappingElement.builder()
									.comment("comment")
									.id("id")
									.identity("identity")
									.language("en")
									.map("map")
									.build())
								.path("path")
								.build())
						.build())
			.experimental(true)
			.fhirVersion("fhirVersion")
			.addMapping(Mapping.builder()
					.comment("mappingComment")
					.identity("identity")
					.name("name")
					.uri("uri")
					.build())
			.kind(new Code("kind"))
			.status(PublicationStatus.ACTIVE)
			.title("title")
			.type("type")
			.version("version")
			.build();
		
		applyFilter(structureDefinition);
	
	}
	
	private void validate(StructureDefinition structureDefinition) {
		assertEquals("id", structureDefinition.getId().getIdValue());
	}
	
	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(structureDefinition);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(structureDefinition));
		assertThat(jsonPath.getString("id"), equalTo("id"));
	}
	
	@Test
	public void deserialize() throws Exception {
		StructureDefinition readStructureDefinition = objectMapper.readValue(objectMapper.writeValueAsString(structureDefinition), StructureDefinition.class);
		validate(readStructureDefinition);
	}
}
