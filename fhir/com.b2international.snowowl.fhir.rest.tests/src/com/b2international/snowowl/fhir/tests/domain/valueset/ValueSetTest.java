/*
 * Copyright 2011-2021 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.fhir.tests.domain.valueset;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.IdentifierUse;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.codesystems.QuantityComparator;
import com.b2international.snowowl.fhir.core.model.ContactDetail;
import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.dt.*;
import com.b2international.snowowl.fhir.core.model.usagecontext.QuantityUsageContext;
import com.b2international.snowowl.fhir.core.model.valueset.Compose;
import com.b2international.snowowl.fhir.core.model.valueset.Include;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.Contains;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.Expansion;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.UriParameter;
import com.b2international.snowowl.fhir.tests.FhirTest;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link ValueSet}
 * @since 6.3
 */
public class ValueSetTest extends FhirTest {
	
	private ValueSet valueSet;

	@Before
	public void setup() throws Exception {
		
		UriParameter stringParameter = UriParameter.builder()
			.name("paramName")
			.value(new Uri("paramValue"))
			.build();
			
		UriParameter uriParameter = UriParameter.builder()
			.name("uriParamName")
			.value(new Uri("uriParamValue"))
			.build();
	
		Contains contains = Contains.builder()
			.system("systemUri")
			.isAbstract(true)
			.inactive(false)
			.version("20140131")
			.code("Code")
			.display("displayValue")
			.addDesignation(Designation.builder()
					.language("en-us")
					.value("pt")
					.build())
			.addContains(Contains.builder().build())
			.build();

		Expansion expansion = Expansion.builder()
			.identifier("identifier")
			.timestamp(TEST_DATE_STRING)
			.total(200)
			.addParameter(stringParameter)
			.addParameter(uriParameter)
			.addContains(contains)
			.build();
		
		Coding coding = Coding.builder()
				.code("codingCode")
				.display("codingDisplay")
				.build();
			
		CodeableConcept jurisdiction = CodeableConcept.builder()
				.addCoding(coding)
				.text("codingText")
				.build();
		
		valueSet = ValueSet.builder("-1")
			.url("http://who.org")
			.addIdentifier(Identifier.builder()
					.system("system")
					.use(IdentifierUse.OFFICIAL)
					.type(CodeableConcept.builder()
								.addCoding(coding)
								.text("codingText")
								.build())
					.build())
			.version("20130131")
			.name("refsetName")
			.title("refsetTitle")
			.copyright("copyright")
			.status(PublicationStatus.ACTIVE)
			.date(TEST_DATE_STRING)
			.publisher("b2i")
			.addContact(ContactDetail.builder()
					.addTelecom(ContactPoint.builder()
						.id("contactPointId")
						.build())
					.build())
			.description("descriptionString")
			.addJurisdiction(jurisdiction)
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
			.expansion(expansion)
			.compose(Compose.builder()
					.addInclude(Include.builder()
							.system("uriValue")
							.build())
					.build())
			.build();
		
		applyFilter(valueSet);
	}
	
	@Test
	public void build() {
		validate(valueSet);
	}
	
	private void validate(ValueSet valueSet) {
		assertEquals("-1", valueSet.getId().getIdValue());
		
	}

	@Test
	public void serialize() throws Exception {
		
		JsonPath jsonPath = getJsonPath(valueSet);
		assertThat(jsonPath.getString("url"), equalTo("http://who.org"));
		assertThat(jsonPath.getString("version"), equalTo("20130131"));
		assertThat(jsonPath.getString("name"), equalTo("refsetName"));
		assertThat(jsonPath.getString("description"), equalTo("descriptionString"));
		assertThat(jsonPath.getString("title"), equalTo("refsetTitle"));
		assertThat(jsonPath.get("expansion.parameter.name"), hasItem("paramName"));
		assertThat(jsonPath.get("expansion.contains.system"), hasItem("systemUri"));
	}
	
	@Test
	public void deserialize() throws Exception, JsonProcessingException {
		ValueSet readValueSet = objectMapper.readValue(objectMapper.writeValueAsString(valueSet), ValueSet.class);
		validate(readValueSet);
	}
	
}
