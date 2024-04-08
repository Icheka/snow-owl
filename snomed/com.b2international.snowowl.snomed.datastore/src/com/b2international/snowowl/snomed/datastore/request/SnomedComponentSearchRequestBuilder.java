/*
 * Copyright 2011-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.datastore.request;

import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.request.SnomedComponentSearchRequest.OptionKey;

/**
 * Abstract superclass for building request for SNOMED CT component searches.
 * Clients should not extend. 
 * @since 5.3
 */
public abstract class SnomedComponentSearchRequestBuilder<B extends SnomedComponentSearchRequestBuilder<B, R>, R extends PageableCollectionResource<?>> extends SnomedSearchRequestBuilder<B, R> {
	
	/**
	 * Filter matches by their active membership in the given reference set or ECL expression.
	 * @param refsetIdOrEcl
	 * @return
	 */
	public final B isActiveMemberOf(String refsetIdOrEcl) {
		return addOption(OptionKey.ACTIVE_MEMBER_OF, refsetIdOrEcl);
	}
	
	/**
	 * Filter matches by their active membership in any of the given reference sets.
	 * @param refsetIds
	 * @return
	 */
	public final B isActiveMemberOf(Iterable<String> refsetIds) {
		return addOption(OptionKey.ACTIVE_MEMBER_OF, refsetIds);
	}
	
	/**
	 * Filter matches by their membership in the given reference set or ECL expression. Matches both active and inactive memberships.
	 * @param refsetIdOrEcl
	 * @return
	 */
	public final B isMemberOf(String refsetIdOrEcl) {
		return addOption(OptionKey.MEMBER_OF, refsetIdOrEcl);
	}
	
	/**
	 * Filter matches by their membership in any of the given reference sets. Matches both active and inactive memberships.
	 * @param refsetIds
	 * @return
	 */
	public final B isMemberOf(Iterable<String> refsetIds) {
		return addOption(OptionKey.MEMBER_OF, refsetIds);
	}
	
	/**
	 * Filter matches by namespace identifier (a section of the SCTID).
	 * 
	 * @param namespaceId
	 *            - the namespace identifier as a string
	 * @return SnomedComponentSearchRequestBuilder
	 * @see Concepts
	 */
	public final B filterByNamespace(String namespaceId) {
		return addOption(OptionKey.NAMESPACE, namespaceId);
	}
	
	/**
	 * Filter matches by namespace identifiers (a section of the SCTID).
	 * 
	 * @param namespaceIds 
	 *            - the namespace identifiers
	 * @return SnomedComponentSearchRequestBuilder
	 * @see Concepts
	 */
	public final B filterByNamespaces(Iterable<String> namespaceIds) {
		return addOption(OptionKey.NAMESPACE, namespaceIds);
	}

	/**
	 * Filter matches by their namespace, using the corresponding metadata concept's SCTID.
	 * 
	 * @param namespaceConceptId
	 *            - the SCTID of the namespace concept
	 * @return SnomedComponentSearchRequestBuilder
	 * @see Concepts
	 */
	public final B filterByNamespaceConcept(String namespaceConceptId) {
		return addOption(OptionKey.NAMESPACE_CONCEPT_ID, namespaceConceptId);
	}
	
	/**
	 * Filter matches by their namespace, using the SCTIDs of allowed metadata concepts.
	 * 
	 * @param namespaceConceptIds
	 *            - the SCTIDs of allowed namespace concepts
	 * @return SnomedComponentSearchRequestBuilder
	 * @see Concepts
	 */
	public final B filterByNamespaceConcepts(Iterable<String> namespaceConceptIds) {
		return addOption(OptionKey.NAMESPACE_CONCEPT_ID, namespaceConceptIds);
	}
}
