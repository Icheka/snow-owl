/*
 * Copyright 2011-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.datastore.converter;

import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.BaseRevisionResourceConverter;
import com.b2international.snowowl.core.request.expand.BaseResourceExpander;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSets;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetMemberSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * @since 4.5
 */
public final class SnomedReferenceSetConverter extends BaseRevisionResourceConverter<SnomedConceptDocument, SnomedReferenceSet, SnomedReferenceSets> {
	
	public SnomedReferenceSetConverter(BranchContext context, Options expand, List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}

	@Override
	protected SnomedReferenceSets createCollectionResource(List<SnomedReferenceSet> results, String searchAfter, int limit, int total) {
		return new SnomedReferenceSets(results, searchAfter, limit, total);
	}
	
	@Override
	public void expand(List<SnomedReferenceSet> results) {
		new ModuleExpander(context(), expand(), locales()).expand(results);
		expandMembers(results);
	}

	private void expandMembers(List<SnomedReferenceSet> results) {
		if (expand().containsKey("members")) {
			Options expandOptions = expand().get("members", Options.class);
			
			for (SnomedReferenceSet refSet : results) {
				SnomedRefSetMemberSearchRequestBuilder req = SnomedRequests.prepareSearchMember()
						.filterByRefSet(refSet.getId())
						.setLocales(locales())
						.setExpand(expandOptions.get(EXPAND_OPTION_KEY, Options.class))
						.setLimit(BaseResourceExpander.getLimit(expandOptions));
				
				refSet.setMembers(req.build().execute(context()));
			}
		}
	}
	
	@Override
	public SnomedReferenceSet toResource(SnomedConceptDocument entry) {
		final SnomedReferenceSet refset = new SnomedReferenceSet();
		refset.setId(entry.getId());
		refset.setEffectiveTime(EffectiveTimes.toDate(entry.getEffectiveTime()));
		refset.setActive(entry.isActive());
		refset.setReleased(entry.isReleased());
		refset.setModuleId(entry.getModuleId());
		refset.setIconId(entry.getIconId());
		refset.setScore(entry.getScore());
		refset.setReferencedComponentType(entry.getReferencedComponentType());
		refset.setMapTargetComponentType(entry.getMapTargetComponentType());
		refset.setMapSourceComponentType(entry.getMapSourceComponentType());
		refset.setType(entry.getRefSetType());
		return refset;
	}

}
