/*
 * Copyright 2018-2021 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.fhir.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.b2international.snowowl.fhir.tests.domain.*;
import com.b2international.snowowl.fhir.tests.domain.capabilitystatement.*;
import com.b2international.snowowl.fhir.tests.domain.codesystem.CodeSystemTest;
import com.b2international.snowowl.fhir.tests.domain.codesystem.ConceptPropertyTest;
import com.b2international.snowowl.fhir.tests.domain.codesystem.ConceptTest;
import com.b2international.snowowl.fhir.tests.domain.codesystem.FilterTest;
import com.b2international.snowowl.fhir.tests.domain.conceptmap.*;
import com.b2international.snowowl.fhir.tests.domain.operationdefinition.OperationDefinitionTest;
import com.b2international.snowowl.fhir.tests.domain.operationdefinition.OverloadTest;
import com.b2international.snowowl.fhir.tests.domain.operationdefinition.ParameterTest;
import com.b2international.snowowl.fhir.tests.domain.structuredefinition.*;
import com.b2international.snowowl.fhir.tests.domain.valueset.*;
import com.b2international.snowowl.fhir.tests.dt.*;
import com.b2international.snowowl.fhir.tests.serialization.parameterized.*;

/**
 * FHIR test suite.
 * @since 6.4
 */
@RunWith(Suite.class)
@SuiteClasses({ 

	//Generic tests
	SnomedUriParsingTest.class,
	ExceptionTest.class,

	//Data type tests
	PrimitiveDataTypeTest.class,
	CodingTest.class,
	CodeableConcepTest.class,
	IdentiferTest.class,
	PeriodTest.class,
	ReferenceTest.class,
	ContactPointTest.class,
	ContactDetailTest.class,
	MetaTest.class,
	NarrativeTest.class,
	QuantityTest.class,
	SimpleQuantityTest.class,
	RangeTest.class,
	SignatureTest.class,
	
	//parameterized
	ParameterDeserializationTest.class,
	ParameterSerializationTest.class,
	PropertySerializationTest.class,
	DesignationSerializationTest.class,
	LookupRequestDeserializationTest.class,
	LookupResultSerializationTest.class,
	ValidateCodeSystemCodeRequestTest.class,
	ValidateCodeResultTest.class,
	TranslateRequestDeserializationTest.class,
	TranslateResultSerializationTest.class,
	SubsumptionRequestDeserializationTest.class,
	ExpandValueSetRequestDeserializationTest.class,

	//Domain models
	TypedPropertySerializationTest.class,
	
	//Common
	IssueTest.class,
	OperationOutcomeTest.class,
	BundleEntryTest.class,
	BundleTest.class,
	
	//CodeSystem
	CodeSystemTest.class,
	ConceptPropertyTest.class,
	FilterTest.class,
	ConceptTest.class,
	
	//ValueSet
	ValueSetConceptTest.class,
	ValueSetFilterTest.class,
	IncludeTest.class,
	ComposeTest.class,
	ExpansionParameterTest.class,
	ContainsTest.class,
	ExpansionTest.class,
	QuantityUsageContextTest.class,
	RangeUsageContextTest.class,
	CodeableConceptUsageContextTest.class,
	ValueSetTest.class,

	//ConceptMap
	UnMappedTest.class,
	DependsOnTest.class,
	TargetTest.class,
	ConceptMapElementTest.class,
	GroupTest.class,
	ConceptMapTest.class,
	
	//StructureDefinition
	DiscriminatorTest.class,
	SlicingTest.class,
	BaseTest.class,
	BindingTest.class,
	ExampleTest.class,
	TypeTest.class,
	ConstraintTest.class,
	MappingElementTest.class,
	ElementDefinitionTest.class,
	StructureViewTest.class,
	MappingTest.class,
	StructureDefinitionTest.class,
	
	//CapabilityStatement
	SupportedMessageTest.class,
	EndpointTest.class,
	MessagingTest.class,
	DocumentTest.class,
	ImplementationTest.class,
	SoftwareTest.class,
	SecurityTest.class,
	InteractionTest.class,
	ResourceTest.class,
	SearchParamTest.class,
	OperationTest.class,
	RestTest.class,
	CapabilityStatementTest.class,
	
	//OperationDefinition
	com.b2international.snowowl.fhir.tests.domain.operationdefinition.BindingTest.class,
	OverloadTest.class,
	ParameterTest.class,
	OperationDefinitionTest.class
	
})
public class AllFhirTests {
}
