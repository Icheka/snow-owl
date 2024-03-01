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

import java.util.Set;

import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.NotEmpty;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.authorization.AccessControl;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.google.common.base.Strings;

/**
 * @since 4.5
 */
final class SnomedRefSetCreateRequest implements Request<TransactionContext, String>, AccessControl {

	public static final Set<String> STRUCTURAL_ATTRIBUTE_VALUE_SETS = Set.of(
		Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR,
		Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR,
		Concepts.REFSET_RELATIONSHIP_REFINABILITY
	);	
	
	@NotNull
	private final SnomedRefSetType type;
	
	@NotEmpty
	private final String referencedComponentType;
	
	private String mapTargetComponentType = TerminologyRegistry.UNSPECIFIED;
	
	private String mapSourceComponentType = TerminologyRegistry.UNSPECIFIED;
	
	private String identifierId;
	
	SnomedRefSetCreateRequest(SnomedRefSetType type, String referencedComponentType) {
		this.type = type;
		this.referencedComponentType = referencedComponentType;
	}

	// Used by SnomedConceptCreateRequest to check if the type matches the parent concept of the new identifier concept 
	SnomedRefSetType getRefSetType() {
		return type;
	}

	void setIdentifierId(String identifierId) {
		this.identifierId = identifierId;
	}
	
	void setMapTargetComponentType(String mapTargetComponentType) {
		this.mapTargetComponentType = mapTargetComponentType;
	}
	
	void setMapSourceComponentType(String mapSourceComponentType) {
		this.mapSourceComponentType = mapSourceComponentType;
	}

	@Override
	public String execute(TransactionContext context) {
		RefSetSupport.checkType(type, referencedComponentType);
		
		if (Strings.isNullOrEmpty(identifierId)) {
			throw new BadRequestException("Reference set identifier ID may not be null or empty.");
		}
		
		final SnomedConceptDocument concept;
		try {
			concept = context.lookup(identifierId, SnomedConceptDocument.class);
			if (concept.getRefSetType() != null) {
				throw new BadRequestException("Identifier concept %s has been already registered as refset", identifierId);
			}
		} catch (ComponentNotFoundException e) {
			throw e.toBadRequestException();
		}
		
		final SnomedConceptDocument.Builder updatedConcept = SnomedConceptDocument.builder(concept);

		final SnomedReferenceSet refSet = new SnomedReferenceSet();
		refSet.setType(type);
		refSet.setReferencedComponentType(referencedComponentType);
		
		if (SnomedRefSetUtil.isMapping(type)) {
			refSet.setMapTargetComponentType(mapTargetComponentType);
			refSet.setMapSourceComponentType(mapSourceComponentType);
		}

		updatedConcept.refSet(refSet);
		context.update(concept, updatedConcept.build());
		return identifierId;
	}
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_EDIT;
	}
}
