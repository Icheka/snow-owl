/*
 * Copyright 2017-2022 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.core.rest.components;

import static com.b2international.snowowl.snomed.core.rest.SnomedApiTestConstants.*;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.deleteComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.searchComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRefSetRestRequests.updateRefSetComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRefSetRestRequests.updateRefSetMemberEffectiveTime;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.*;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getNextAvailableEffectiveDate;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.hamcrest.CoreMatchers.equalTo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.Pair;
import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.google.common.collect.Maps;

import io.restassured.response.ValidatableResponse;

/**
 * @since 5.7
 */
public abstract class SnomedRefSetMemberParameterizedTest extends AbstractSnomedApiTest {

	private static final List<String> REFERENCED_COMPONENT_TYPES = List.of(SnomedConcept.TYPE, SnomedDescription.TYPE, SnomedRelationship.TYPE);
	
	private static final Map<SnomedRefSetType, String> REFSET_CACHE = Maps.newHashMap();

	private final SnomedRefSetType refSetType;

	public SnomedRefSetMemberParameterizedTest(SnomedRefSetType refSetType) {
		this.refSetType = refSetType;
	}

	@Before
	public void ensureIdentifierParentConceptExists() {
		final String parentConceptId = SnomedRefSetUtil.getParentConceptId(refSetType);
		final int statusCode = getComponent(branchPath, SnomedComponentType.CONCEPT, parentConceptId)
			.extract()
			.statusCode();
		
		if (statusCode == 404) {
			createComponent(branchPath, SnomedComponentType.CONCEPT, createConceptRequestBody(Concepts.REFSET_ALL)
				.with("id", parentConceptId)
				.with("commitComment", "Created parent concept for reference set type '" + refSetType + "'")).statusCode(201);
		}
	}

	@Test
	public void rejectNonExistentRefSetId() throws Exception {
		String refSetId = reserveComponentId(null, ComponentCategory.CONCEPT);
		String referencedComponentId = createReferencedComponent(branchPath, refSetType);

		Json requestBody = createRefSetMemberRequestBody(refSetId, referencedComponentId)
				.with(getValidProperties(refSetType, referencedComponentId))
				.with("commitComment", "Created new reference set member with non-existent refSetId");

		createComponent(branchPath, SnomedComponentType.MEMBER, requestBody).statusCode(400);
	}

	@Test
	public void rejectNonExistentComponentId() throws Exception {
		String refSetId = createNewRefSet(branchPath, refSetType);
		String referencedComponentId = reserveComponentId(null, getFirstAllowedReferencedComponentCategory(refSetType));

		Json requestBody = createRefSetMemberRequestBody(refSetId, referencedComponentId)
				.with(getValidProperties(refSetType, referencedComponentId))
				.with("commitComment", "Created new reference set member with non-existent referencedComponentId");

		createComponent(branchPath, SnomedComponentType.MEMBER, requestBody).statusCode(400);
	}

	@Test
	public void rejectMismatchedComponentType() throws Exception {
		String refSetId = getOrCreateRefSet(branchPath, refSetType);
		String referencedComponentType = getFirstAllowedReferencedComponentType(refSetType);

		for (String disallowedComponentType : REFERENCED_COMPONENT_TYPES) {
			if (!referencedComponentType.equals(disallowedComponentType)) {
				String referencedComponentId = getFirstMatchingReferencedComponent(branchPath, disallowedComponentType);

				Json requestBody = createRefSetMemberRequestBody(refSetId, referencedComponentId)
						.with(getValidProperties(refSetType, referencedComponentId))
						.with("commitComment", "Created new reference set member with mismatched referencedComponentId");

				String expectedMessage = String.format("'%s' reference set can't reference '%s | %s' component. Only '%s' components are allowed.",
						refSetId, 
						referencedComponentId, 
						disallowedComponentType, 
						referencedComponentType);

				createComponent(branchPath, SnomedComponentType.MEMBER, requestBody)
				.statusCode(400)
				.body("message", equalTo(expectedMessage));
			}
		}
	}

	@Test
	public void rejectInvalidProperties() throws Exception {
		// Simple type reference sets can't be tested with this method
		Assume.assumeFalse(SnomedRefSetType.SIMPLE.equals(refSetType));

		String refSetId = getOrCreateRefSet(branchPath, refSetType);
		String componentId = createNewComponent(branchPath, getFirstAllowedReferencedComponentType(refSetType));

		Json requestBody = createRefSetMemberRequestBody(refSetId, componentId)
				.with(getInvalidProperties())
				.with("commitComment", "Created new reference set member with invalid additional properties");

		createComponent(branchPath, SnomedComponentType.MEMBER, requestBody).statusCode(400);
	}

	@Test
	public void acceptValidRequest() throws Exception {
		final Pair<String, String> member = createRefSetMember();
		final String memberId = member.getA();
		final String referencedComponentId = member.getB();

		ValidatableResponse response = getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200);
		for (Entry<String, Object> validProperty : getValidProperties(refSetType, referencedComponentId).entrySet()) {
			response.body(validProperty.getKey(), equalTo(validProperty.getValue()));
		}
	}

	@Test
	public void acceptValidUpdate() throws Exception {
		// Simple type reference sets can't be tested with this method
		Assume.assumeFalse(SnomedRefSetType.SIMPLE.equals(refSetType));

		final Pair<String, String> member = createRefSetMember();
		final String memberId = member.getA();
		final String referencedComponentId = member.getB();		

		Json updateRequest = Json.object(
		)
				.with(getUpdateProperties(referencedComponentId))
				.with("commitComment", "Updated reference set member");

		updateRefSetComponent(branchPath, SnomedComponentType.MEMBER, memberId, updateRequest, false).statusCode(204);

		ValidatableResponse response = getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200);
		for (Entry<String, Object> validProperty : getUpdateProperties(referencedComponentId).entrySet()) {
			response.body(validProperty.getKey(), equalTo(validProperty.getValue()));
		}
	}

	@Test
	public void inactivateMember() throws Exception {
		final Pair<String, String> member = createRefSetMember();
		final String memberId = member.getA();

		Json updateRequest = Json.object(
			"active", false,
			"moduleId", Concepts.MODULE_ROOT,
			"commitComment", "Inactivated reference set member and changed module"
		);

		updateRefSetComponent(branchPath, SnomedComponentType.MEMBER, memberId, updateRequest, false)
			.statusCode(204);
		
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId)
			.statusCode(200)
			.body("active", equalTo(false))
			.body("moduleId", equalTo(Concepts.MODULE_ROOT));
	}

	@Test
	public void updateMemberEffectiveTime() throws Exception {
		final Pair<String, String> member = createRefSetMember();
		final String memberId = member.getA();
		LocalDate effectiveTime = getNextAvailableEffectiveDate(SnomedContentRule.SNOMEDCT_ID);

		Json updateRequest = Json.object(
			"effectiveTime", EffectiveTimes.format(effectiveTime, DateFormats.SHORT),
			"commitComment", "Updated effective time on reference set member without force flag"
		);
		
		// The update does not go through anymore, rejected with an error
		updateRefSetComponent(branchPath, SnomedComponentType.MEMBER, memberId, updateRequest, false)
			.statusCode(400);
	}

	@Test
	public void forceUpdateMemberEffectiveTime() throws Exception {
		final Pair<String, String> member = createRefSetMember();
		final String memberId = member.getA();
		LocalDate effectiveTime = getNextAvailableEffectiveDate(SnomedContentRule.SNOMEDCT_ID);

		updateRefSetMemberEffectiveTime(branchPath, memberId, effectiveTime);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200)
			.body("effectiveTime", equalTo(EffectiveTimes.format(effectiveTime, DateFormats.SHORT)))
			.body("released", equalTo(true));
	}

	@Test
	public void deleteMember() throws Exception {
		final Pair<String, String> member = createRefSetMember();
		final String memberId = member.getA();
		deleteComponent(branchPath, SnomedComponentType.MEMBER, memberId, false).statusCode(204);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(404);
	}

	@Test
	public void deleteReleasedMember() throws Exception {
		final Pair<String, String> member = createRefSetMember();
		final String memberId = member.getA();
		LocalDate effectiveTime = getNextAvailableEffectiveDate(SnomedContentRule.SNOMEDCT_ID);
		updateRefSetMemberEffectiveTime(branchPath, memberId, effectiveTime);

		// A published component can not be deleted without the force flag enabled
		deleteComponent(branchPath, SnomedComponentType.MEMBER, memberId, false).statusCode(409);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200);
	}

	@Test
	public void forceDeleteReleasedMember() throws Exception {
		final Pair<String, String> member = createRefSetMember();
		final String memberId = member.getA();
		LocalDate effectiveTime = getNextAvailableEffectiveDate(SnomedContentRule.SNOMEDCT_ID);
		updateRefSetMemberEffectiveTime(branchPath, memberId, effectiveTime);

		deleteComponent(branchPath, SnomedComponentType.MEMBER, memberId, true).statusCode(204);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(404);
	}
	
	@Test
	public void testRestorationOfEffectiveTimeOnMutablePropertyChange() {
		// XXX skip simple type refsets
		Assume.assumeFalse(SnomedRefSetType.SIMPLE.equals(refSetType));
		
		final String shortName = getOrCreateCodeSystem();

		// create the previous revision of the member
		final Pair<String, String> member = createRefSetMember();
		final String memberId = member.getA();
		final String referencedComponentId = member.getB();

		final LocalDate effectiveTime = getNextAvailableEffectiveDate(shortName);
		createVersion(shortName, effectiveTime).statusCode(201);

		// update properties
		final Json updateRequest = getUpdateProperties(referencedComponentId)
				.with("commitComment", "Updated reference set member");

		updateRefSetComponent(branchPath, SnomedComponentType.MEMBER, memberId, updateRequest, false).statusCode(204);

		// Updating a member's properties should unset the effective time
		final ValidatableResponse updateResponse = getComponent(branchPath, SnomedComponentType.MEMBER, memberId)
				.statusCode(200)
				.body("released", equalTo(true))
				.body("effectiveTime", equalTo(null));

		// check updated properties
		for (Entry<String, Object> updateProperty : getUpdateProperties(referencedComponentId).entrySet()) {
			updateResponse.body(updateProperty.getKey(), equalTo(updateProperty.getValue()));
		}

		// revert back to original values
		final Json revertUpdateRequest = getValidProperties(refSetType, referencedComponentId)
				.with("commitComment", "Reverted previous update of reference set member");

		updateRefSetComponent(branchPath, SnomedComponentType.MEMBER, memberId, revertUpdateRequest, false).statusCode(204);

		// Getting the member back to its originally released state should restore the effective time
		ValidatableResponse revertResponse = getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200)
			.body("released", equalTo(true))
			.body("effectiveTime", equalTo(effectiveTime.format(DateTimeFormatter.BASIC_ISO_DATE)));

		for (Entry<String, Object> validProperty : getValidProperties(refSetType, referencedComponentId).entrySet()) {
			revertResponse.body(validProperty.getKey(), equalTo(validProperty.getValue()));
		}

	}

	protected abstract String getOrCreateCodeSystem();

	/** 
	 * Creates a member for the first applicable matching referenced component and returns the memberId and the referencedComponentId in a Pair.
	 */
	private Pair<String, String> createRefSetMember() {
		String referencedComponentId = getFirstMatchingComponent(branchPath, getFirstAllowedReferencedComponentType(refSetType));

		String refSetId = getOrCreateRefSet(branchPath, refSetType);
		Json requestBody = createRefSetMemberRequestBody(refSetId, referencedComponentId)
				.with(getValidProperties(refSetType, referencedComponentId))
				.with("commitComment", "Created new reference set member");
		
		ValidatableResponse response = createComponent(branchPath, SnomedComponentType.MEMBER, requestBody).statusCode(201);
		
		return Pair.of(lastPathSegment(response.extract().header("Location")), referencedComponentId);
	}

	private String getFirstMatchingReferencedComponent(IBranchPath branchPath, String referencedComponentType) {
		return searchComponent(branchPath, getSnomedComponentType(referencedComponentType), Json.object("limit", 1)).extract().jsonPath().<String>getList("items.id").get(0);
	}

	private SnomedComponentType getSnomedComponentType(String referencedComponentType) {
		switch (referencedComponentType) {
		case SnomedConcept.TYPE:
			return SnomedComponentType.CONCEPT;
		case SnomedDescription.TYPE:
			return SnomedComponentType.DESCRIPTION;
		case SnomedRelationship.TYPE:
			return SnomedComponentType.RELATIONSHIP;
		default:
			throw new UnsupportedOperationException("Not implemented case for: " + referencedComponentType);
		}
	}

	private static String getOrCreateRefSet(IBranchPath branchPath, SnomedRefSetType refSetType) {
		return REFSET_CACHE.computeIfAbsent(refSetType, type -> createNewRefSet(branchPath, type));
	}

	private Json getUpdateProperties(String referencedComponentId) {
		switch (refSetType) {
		case ASSOCIATION:
			return Json.object(
				SnomedRf2Headers.FIELD_TARGET_COMPONENT_ID, Concepts.NAMESPACE_ROOT
			);
		case ATTRIBUTE_VALUE:
			return Json.object(
				SnomedRf2Headers.FIELD_VALUE_ID, Concepts.NAMESPACE_ROOT
			);
		case COMPLEX_MAP:
			return Json.object(
				SnomedRf2Headers.FIELD_MAP_TARGET, "complexMapTarget2",
				SnomedRf2Headers.FIELD_MAP_GROUP, 1,
				SnomedRf2Headers.FIELD_MAP_PRIORITY, 1,
				SnomedRf2Headers.FIELD_MAP_RULE, "complexMapRule2",
				SnomedRf2Headers.FIELD_MAP_ADVICE, "complexMapAdvice2",
				SnomedRf2Headers.FIELD_CORRELATION_ID, Concepts.NAMESPACE_ROOT
			);
		case COMPLEX_BLOCK_MAP:
			return Json.object(
				SnomedRf2Headers.FIELD_MAP_TARGET, "complexBlockMapTarget2",
				SnomedRf2Headers.FIELD_MAP_GROUP, 1,
				SnomedRf2Headers.FIELD_MAP_PRIORITY, 1,
				SnomedRf2Headers.FIELD_MAP_RULE, "complexBlockMapRule2",
				SnomedRf2Headers.FIELD_MAP_ADVICE, "complexBlockMapAdvice2",
				SnomedRf2Headers.FIELD_CORRELATION_ID, Concepts.NAMESPACE_ROOT,
				SnomedRf2Headers.FIELD_MAP_BLOCK, 2
			);
		case DESCRIPTION_TYPE:
			return Json.object(
				SnomedRf2Headers.FIELD_DESCRIPTION_FORMAT, Concepts.NAMESPACE_ROOT,
				SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH, 101
			);
		case EXTENDED_MAP:
			return Json.object(
				SnomedRf2Headers.FIELD_MAP_TARGET, "extendedMapTarget2",
				SnomedRf2Headers.FIELD_MAP_GROUP, 11,
				SnomedRf2Headers.FIELD_MAP_PRIORITY, 11,
				SnomedRf2Headers.FIELD_MAP_RULE, "extendedMapRule2",
				SnomedRf2Headers.FIELD_MAP_ADVICE, "extendedMapAdvice2",
				SnomedRf2Headers.FIELD_CORRELATION_ID, Concepts.NAMESPACE_ROOT,
				SnomedRf2Headers.FIELD_MAP_CATEGORY_ID, Concepts.NAMESPACE_ROOT
			);
		case LANGUAGE:
			return Json.object(
				SnomedRf2Headers.FIELD_ACCEPTABILITY_ID, Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED
			);
		case MODULE_DEPENDENCY:
			return Json.object(
				SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME, "20170224",
				SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME, "20170225"
			);
		case SIMPLE:
			return Json.object();
		case SIMPLE_MAP:
			return Json.object(
				SnomedRf2Headers.FIELD_MAP_TARGET, "simpleMapTarget2"
			);
		case SIMPLE_MAP_TO:
			return Json.object(
				SnomedRf2Headers.FIELD_MAP_SOURCE, "simpleMapSource2"
			);
		case SIMPLE_MAP_WITH_DESCRIPTION:
			return Json.object(
				SnomedRf2Headers.FIELD_MAP_TARGET, "mapTarget2",
				SnomedRf2Headers.FIELD_MAP_TARGET_DESCRIPTION, "mapTargetDescription2"
			);
		case OWL_AXIOM:
			return Json.object(
				SnomedRf2Headers.FIELD_OWL_EXPRESSION, SnomedApiTestConstants.owlAxiom2(referencedComponentId)
			);
		case OWL_ONTOLOGY:
			return Json.object(
				SnomedRf2Headers.FIELD_OWL_EXPRESSION, OWL_ONTOLOGY_2
			);
		case MRCM_DOMAIN:
			return Json.object(
				SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT, DOMAIN_CONSTRAINT_2,
				SnomedRf2Headers.FIELD_MRCM_PARENT_DOMAIN, "", // unset on purpose
				SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT, PROXIMAL_PRIMITIVE_CONSTRAINT_2,
				SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_REFINEMENT, "", // unset on purpose
				SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION, DOMAIN_TEMPLATE_FOR_PRECOORDINATION_2,
				SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION, DOMAIN_TEMPLATE_FOR_POSTCOORDINATION_2,
				SnomedRf2Headers.FIELD_MRCM_GUIDEURL, "" // unset on purpose
			);
		case MRCM_ATTRIBUTE_DOMAIN:
			return Json.object(
				SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, DOMAIN_ID_2,
				SnomedRf2Headers.FIELD_MRCM_GROUPED, Boolean.FALSE,
				SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, ATTRIBUTE_CARDINALITY_2,
				SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, ATTRIBUTE_IN_GROUP_CARDINALITY_2,
				SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, RULE_STRENGTH_ID_2,
				SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, CONTENT_TYPE_ID_2
			);
		case MRCM_ATTRIBUTE_RANGE:
			return Json.object(
				SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT, RANGE_CONSTRAINT_2,
				SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_RULE, ATTRIBUTE_RULE_2,
				SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, RULE_STRENGTH_ID_2,
				SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, CONTENT_TYPE_ID_2
			);
		case MRCM_MODULE_SCOPE:
			return Json.object(
				SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID, RULE_REFSET_ID_2
			);
		default:
			throw new IllegalStateException("Unexpected reference set type '" + refSetType + "'.");
		}
	}

	private Map<String, Object> getInvalidProperties() {
		switch (refSetType) {
		case ASSOCIATION:
			return Json.object(
				SnomedRf2Headers.FIELD_TARGET_COMPONENT_ID, ""
			);
		case ATTRIBUTE_VALUE:
			return Json.object(
				SnomedRf2Headers.FIELD_VALUE_ID, ""
			);
		case COMPLEX_MAP:
			// Invalid because FIELD_MAP_PRIORITY is not set
			return Json.object(
				SnomedRf2Headers.FIELD_MAP_TARGET, "complexMapTarget",
				SnomedRf2Headers.FIELD_MAP_GROUP, (byte) 0,
				SnomedRf2Headers.FIELD_MAP_RULE, "complexMapRule",
				SnomedRf2Headers.FIELD_MAP_ADVICE, "complexMapAdvice",
				SnomedRf2Headers.FIELD_CORRELATION_ID, Concepts.REFSET_CORRELATION_NOT_SPECIFIED
			);
		case DESCRIPTION_TYPE:
			return Json.object(
				SnomedRf2Headers.FIELD_DESCRIPTION_FORMAT, "",
				SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH, 100
			);
		case EXTENDED_MAP:
			return Json.object(
				SnomedRf2Headers.FIELD_MAP_TARGET, "",
				SnomedRf2Headers.FIELD_MAP_GROUP, (byte) 10,
				SnomedRf2Headers.FIELD_MAP_PRIORITY, (byte) 10,
				SnomedRf2Headers.FIELD_MAP_RULE, "extendedMapRule",
				SnomedRf2Headers.FIELD_MAP_ADVICE, "extendedMapAdvice",
				SnomedRf2Headers.FIELD_CORRELATION_ID, Concepts.REFSET_CORRELATION_NOT_SPECIFIED,
				SnomedRf2Headers.FIELD_MAP_CATEGORY_ID, Concepts.MAP_CATEGORY_NOT_CLASSIFIED
			);
		case COMPLEX_BLOCK_MAP:
			return Json.object(
				SnomedRf2Headers.FIELD_MAP_TARGET, "complexBlockMapTarget",
				SnomedRf2Headers.FIELD_MAP_GROUP, 10,
				SnomedRf2Headers.FIELD_MAP_PRIORITY, 10,
				SnomedRf2Headers.FIELD_MAP_RULE, "complexBlockMapRule",
				SnomedRf2Headers.FIELD_MAP_ADVICE, "complexBlockMapAdvice",
				SnomedRf2Headers.FIELD_CORRELATION_ID, Concepts.REFSET_CORRELATION_NOT_SPECIFIED,
				SnomedRf2Headers.FIELD_MAP_BLOCK, "not an integer"
			);
		case LANGUAGE:
			return Json.object(
				SnomedRf2Headers.FIELD_ACCEPTABILITY_ID, ""
			);
		case MODULE_DEPENDENCY:
			return Json.object(
				SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME, "abc"
			);
		case SIMPLE_MAP:
			return Json.object(
				SnomedRf2Headers.FIELD_MAP_TARGET, ""
			);
		case SIMPLE_MAP_TO:
			return Json.object(
				SnomedRf2Headers.FIELD_MAP_SOURCE, ""
			);
		case SIMPLE_MAP_WITH_DESCRIPTION:
			return Json.object(
				SnomedRf2Headers.FIELD_MAP_TARGET, "",
				SnomedRf2Headers.FIELD_MAP_TARGET_DESCRIPTION, "mapTargetDescription"
			);
		case OWL_AXIOM: //$FALL-THROUGH$
		case OWL_ONTOLOGY:
			return Json.object(
				SnomedRf2Headers.FIELD_OWL_EXPRESSION, ""
			);
		case MRCM_DOMAIN:
			return Json.object(
				SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT, "",
				SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT, "",
				SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION, "",
				SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION, ""
			);
		case MRCM_ATTRIBUTE_DOMAIN:
			return Json.object(
				SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, "",
				SnomedRf2Headers.FIELD_MRCM_GROUPED, "booleanValue",
				SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, "",
				SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, "",
				SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, "1234",
				SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, ""
			);
		case MRCM_ATTRIBUTE_RANGE:
			return Json.object(
				SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT, "",
				SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_RULE, "",
				SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, "",
				SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, "1234"
			);
		case MRCM_MODULE_SCOPE:
			return Json.object(
				SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID, ""
			);
		default:
			throw new IllegalStateException("Unexpected reference set type '" + refSetType + "'.");
		}
	}
}
