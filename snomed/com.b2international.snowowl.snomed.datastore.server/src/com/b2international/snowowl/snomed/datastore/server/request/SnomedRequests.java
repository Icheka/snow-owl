/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.server.request;

import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.server.request.Branching;
import com.b2international.snowowl.datastore.server.request.RepositoryCommitRequestBuilder;
import com.b2international.snowowl.datastore.server.request.RepositoryRequests;
import com.b2international.snowowl.datastore.server.request.Reviews;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSets;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;

/**
 * @since 4.5
 */
public abstract class SnomedRequests {

	private static final String REPOSITORY_ID = SnomedDatastoreActivator.REPOSITORY_UUID;

	private SnomedRequests() {
	}
	
	public static <B> RepositoryCommitRequestBuilder prepareCommit(String userId, String branch) {
		return RepositoryRequests.prepareCommit(userId, REPOSITORY_ID, branch);
	}
	
	public static SnomedConceptSearchRequestBuilder prepareSearch(String branch) {
		return new SnomedConceptSearchRequestBuilder(branch);
	}
	
	public static SnomedConceptGetRequestBuilder prepareGet(String branch) {
		return new SnomedConceptGetRequestBuilder(branch);
	}
	
	public static Request<TransactionContext, Void> prepareDeleteComponent(String componentId, Class<? extends EObject> type) {
		return new SnomedComponentDeleteRequest(componentId, type);
	}
	
	public static Request<TransactionContext, ?> prepareDeleteMember(String memberId) {
		return prepareDeleteComponent(memberId, SnomedRefSetMember.class);
	}
	
	public static Request<TransactionContext, ?> prepareDeleteConcept(String conceptId) {
		return prepareDeleteComponent(conceptId, Concept.class);
	}
	
	public static Request<TransactionContext, ?> prepareDeleteDescription(String descriptionId) {
		return prepareDeleteComponent(descriptionId, Description.class);
	}
	
	public static Request<TransactionContext, ?> prepareDeleteRelationship(String relationshipId) {
		return prepareDeleteComponent(relationshipId, Relationship.class);
	}
	
	public static Request<TransactionContext, SnomedReferenceSetMember> prepareNewMember(String moduleId, String referencedComponentId, String referenceSetId) {
		return prepareNewMember(moduleId, referencedComponentId, referenceSetId, Collections.<String, Object>emptyMap());
	}
	
	public static Request<TransactionContext, SnomedReferenceSetMember> prepareNewMember(String moduleId, String referencedComponentId, String referenceSetId, Map<String, Object> properties) {
		final SnomedRefSetMemberCreateRequest req = new SnomedRefSetMemberCreateRequest();
		req.setModuleId(moduleId);
		req.setReferencedComponentId(referencedComponentId);
		req.setReferenceSetId(referenceSetId);
		req.setProperties(properties);
		return req;
	}
	
	public static SnomedRefSetCreateRequestBuilder prepareNewRefSet() {
		return new SnomedRefSetCreateRequestBuilder();
	}
	
	public static SnomedConceptCreateRequestBuilder prepareNewConcept() {
		return new SnomedConceptCreateRequestBuilder();
	}
	
	public static SnomedDescriptionCreateRequestBuilder prepareNewDescription() {
		return new SnomedDescriptionCreateRequestBuilder();
	}
	
	public static SnomedRelationshipCreateRequestBuilder prepareNewRelationship() {
		return new SnomedRelationshipCreateRequestBuilder();
	}
	
	// TODO migrate initial API to builders
	public static Request<ServiceProvider, SnomedReferenceSets> prepareGetReferenceSets(String branch) {
		return RepositoryRequests.wrap(REPOSITORY_ID, branch, new SnomedRefSetReadAllRequest());
	}
	
	public static Request<ServiceProvider, SnomedReferenceSet> prepareGetReferenceSet(String branch, String referenceSetId) {
		return RepositoryRequests.wrap(REPOSITORY_ID, branch, new SnomedRefSetReadRequest(referenceSetId));
	}
	
	public static Request<ServiceProvider, SnomedReferenceSetMembers> prepareGetReferenceSetMembers(String branch, int offset, int limit) {
		return RepositoryRequests.wrap(REPOSITORY_ID, branch, new SnomedRefSetMemberReadAllRequest(offset, limit));
	}
	
	public static Request<ServiceProvider, SnomedReferenceSetMember> prepareGetReferenceSetMember(String branch, String memberId) {
		return RepositoryRequests.wrap(REPOSITORY_ID, branch, new SnomedRefSetMemberReadRequest(memberId));
	}

	public static Branching branching() {
		return RepositoryRequests.branching(REPOSITORY_ID);
	}

	public static Reviews review() {
		return RepositoryRequests.reviews(REPOSITORY_ID);
	}

}
