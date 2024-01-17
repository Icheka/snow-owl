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
package com.b2international.snowowl.snomed.core.rest.domain;

import com.b2international.snowowl.snomed.core.domain.RelationshipValue;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipUpdateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * @since 1.0
 */
public class SnomedRelationshipRestUpdate extends AbstractSnomedComponentRestUpdate {

	private String typeId;
	private String destinationId;
	private RelationshipValue value;
	private Integer relationshipGroup;
	private Integer unionGroup;
	private String characteristicTypeId;
	private String modifierId;

	public String getTypeId() {
		return typeId;
	}

	public String getDestinationId() {
		return destinationId;
	}

	public RelationshipValue getValue() {
		return value;
	}

	public Integer getRelationshipGroup() {
		return relationshipGroup;
	}

	public Integer getUnionGroup() {
		return unionGroup;
	}

	public String getCharacteristicTypeId() {
		return characteristicTypeId;
	}

	public String getModifierId() {
		return modifierId;
	}

	public void setTypeId(final String typeId) {
		this.typeId = typeId;
	}

	public void setDestinationId(final String destinationId) {
		this.destinationId = destinationId;
	}

	public void setValue(final RelationshipValue value) {
		this.value = value;
	}

	public void setRelationshipGroup(final Integer relationshipGroup) {
		this.relationshipGroup = relationshipGroup;
	}

	public void setUnionGroup(final Integer unionGroup) {
		this.unionGroup = unionGroup;
	}

	public void setCharacteristicTypeId(final String characteristicTypeId) {
		this.characteristicTypeId = characteristicTypeId;
	}

	public void setModifierId(final String modifierId) {
		this.modifierId = modifierId;
	}

	public SnomedRelationshipUpdateRequestBuilder toRequestBuilder(final String relationshipId) {
		return SnomedRequests.prepareUpdateRelationship(relationshipId)
			.setActive(isActive())
			.setEffectiveTime(getEffectiveTime())
			.setModuleId(getModuleId())
			// .setSource(...) is not called
			.setTypeId(typeId)
			.setDestinationId(destinationId)
			.setValue(value)
			.setRelationshipGroup(relationshipGroup)
			.setUnionGroup(unionGroup)
			.setCharacteristicTypeId(characteristicTypeId)
			.setModifierId(modifierId);
	}
}
