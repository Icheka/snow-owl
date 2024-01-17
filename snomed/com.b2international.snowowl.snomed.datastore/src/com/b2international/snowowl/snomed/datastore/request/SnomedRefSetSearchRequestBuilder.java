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
package com.b2international.snowowl.snomed.datastore.request;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSets;

/**
 * <i>Builder</i> class to build requests responsible for searching SNOMED CT reference sets. This class should be instantiated from the corresponding
 * static method on the central {@link SnomedRequests} class. Filter methods restrict the results set returned from the search requests; what passes
 * the filters will be returned as part of the pageable resultset.
 * 
 * @since 4.5
 */
public final class SnomedRefSetSearchRequestBuilder extends SnomedSearchRequestBuilder<SnomedRefSetSearchRequestBuilder, SnomedReferenceSets> {

	SnomedRefSetSearchRequestBuilder() {
	}

	@Override
	protected SearchResourceRequest<BranchContext, SnomedReferenceSets> createSearch() {
		return new SnomedRefSetSearchRequest();
	}

	public SnomedRefSetSearchRequestBuilder filterByType(SnomedRefSetType refSetType) {
		return addOption(SnomedRefSetSearchRequest.OptionKey.TYPE, refSetType);
	}

	public SnomedRefSetSearchRequestBuilder filterByTypes(Iterable<SnomedRefSetType> refSetTypes) {
		return addOption(SnomedRefSetSearchRequest.OptionKey.TYPE, refSetTypes);
	}

	public SnomedRefSetSearchRequestBuilder filterByReferencedComponentType(String referencedComponentType) {
		return addOption(SnomedRefSetSearchRequest.OptionKey.REFERENCED_COMPONENT_TYPE, referencedComponentType);
	}

	public SnomedRefSetSearchRequestBuilder filterByReferencedComponentTypes(Iterable<String> referencedComponentTypes) {
		return addOption(SnomedRefSetSearchRequest.OptionKey.REFERENCED_COMPONENT_TYPE, referencedComponentTypes);
	}

	/**
	 * Returns map type reference sets that have the exact matching map target component type. Only applicable for maps, other refsets will not match
	 * this filter.
	 * 
	 * @param mapTargetComponentType
	 *            - map target component type integer
	 */
	public SnomedRefSetSearchRequestBuilder filterByMapTargetComponentType(String mapTargetComponentType) {
		return addOption(SnomedRefSetSearchRequest.OptionKey.MAP_TARGET_COMPONENT_TYPE, mapTargetComponentType);
	}

	/**
	 * Returns map type reference sets that have the exact matching map target component types. Only applicable for maps, other refsets will not match
	 * this filter.
	 * 
	 * @param mapTargetComponentTypes
	 *            - map target component type values
	 */
	public SnomedRefSetSearchRequestBuilder filterByMapTargetComponentTypes(Iterable<String> mapTargetComponentTypes) {
		return addOption(SnomedRefSetSearchRequest.OptionKey.MAP_TARGET_COMPONENT_TYPE, mapTargetComponentTypes);
	}

}
