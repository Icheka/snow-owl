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

import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.deleteComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.*;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.assertCreated;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.request.RefSetSupport;

import io.restassured.response.ValidatableResponse;

/**
 * @since 5.7
 */
public abstract class SnomedRefSetParameterizedTest extends AbstractSnomedApiTest {

	private static final List<String> REFERENCED_COMPONENT_TYPES = List.of(SnomedConcept.TYPE, SnomedDescription.TYPE, SnomedRelationship.TYPE, SnomedConcept.REFSET_TYPE);

	private final SnomedRefSetType refSetType;

	public SnomedRefSetParameterizedTest(SnomedRefSetType refSetType) {
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
	public void acceptValidRequest() {
		final String parentConceptId = SnomedRefSetUtil.getParentConceptId(refSetType);
		for (String referencedComponentType : RefSetSupport.getSupportedReferencedComponentTypes(refSetType)) {
			String refSetId = assertCreated(createRefSet(branchPath, parentConceptId, referencedComponentType));

			getComponent(branchPath, SnomedComponentType.REFSET, refSetId)
				.statusCode(200)
				.body("type", equalTo(refSetType.name()))
				.body("referencedComponentType", equalTo(referencedComponentType));
		}
	}

	@Test
	public void createWithExistingIdentifierConcept() {
		String newIdentifierConceptId = createNewConcept(branchPath, SnomedRefSetUtil.getParentConceptId(refSetType));
		assertEquals(newIdentifierConceptId, createNewRefSet(branchPath, refSetType, newIdentifierConceptId));
	}
	
	@Test
	public void rejectInvalidParent() {
		String referencedComponentType = getFirstAllowedReferencedComponentType(refSetType);
		createRefSet(branchPath, Concepts.ROOT_CONCEPT, referencedComponentType).statusCode(400);
	}

	@Test
	public void rejectInvalidComponentType() {
		for (String referencedComponentType : REFERENCED_COMPONENT_TYPES) {
			if (!RefSetSupport.isReferencedComponentTypeSupported(refSetType, referencedComponentType)) {
				createRefSet(branchPath, Concepts.ROOT_CONCEPT, referencedComponentType).statusCode(400);
			}
		}
	}

	@Test
	public void deleteRefSet() {
		String refSetId = createNewRefSet(branchPath, refSetType);
		deleteComponent(branchPath, SnomedComponentType.REFSET, refSetId, false).statusCode(204);
		getComponent(branchPath, SnomedComponentType.REFSET, refSetId).statusCode(404);
		getComponent(branchPath, SnomedComponentType.CONCEPT, refSetId).statusCode(200);
	}
	
	@Test
	public void deleteRefSetWithMember() throws Exception {
		String referencedComponentId = getFirstMatchingComponent(branchPath, getFirstAllowedReferencedComponentType(refSetType));

		String refSetId = createNewRefSet(branchPath, refSetType);
		Json requestBody = createRefSetMemberRequestBody(refSetId, referencedComponentId)
				.with(getValidProperties(refSetType, referencedComponentId))
				.with("commitComment", "Created new reference set member");

		final String memberId = assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody));
		
		deleteComponent(branchPath, SnomedComponentType.REFSET, refSetId, false).statusCode(204);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(404);
		getComponent(branchPath, SnomedComponentType.REFSET, refSetId).statusCode(404);
		getComponent(branchPath, SnomedComponentType.CONCEPT, refSetId).statusCode(200);
	}

	@Test
	public void deleteIdentifierConcept() {
		String refSetId = createNewRefSet(branchPath, refSetType);
		deleteComponent(branchPath, SnomedComponentType.CONCEPT, refSetId, false).statusCode(204);
		getComponent(branchPath, SnomedComponentType.REFSET, refSetId).statusCode(404);
		getComponent(branchPath, SnomedComponentType.CONCEPT, refSetId).statusCode(404);
	}

	private ValidatableResponse createRefSet(IBranchPath refSetPath, String parentConceptId, String referencedComponentType) {
		return createComponent(refSetPath, SnomedComponentType.REFSET, Json.assign(
			createConceptRequestBody(parentConceptId),
			Json.object(
				"type", refSetType,
				"referencedComponentType", referencedComponentType,
				"commitComment", "Created new reference set"
			)
		));
	}
}
