/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.refset.core.automap;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

/**
 * This class is responsible for mapping labels or IDs with SNOMED CT concepts. For performance optimization the automapper uses a recursive algorithm
 * to get back a corresponding and appropriate equivalent.
 * 
 * @since 3.1
 */
public class SnomedRefSetAutoMapper {
	
	private static final int SEARCHER_STEP_LIMIT = 243;
	private static final int SEARCHER_STEP_INCREMENT_MULTIPLIER = 3;

	private final Set<String> selectedAncestorIds;
	private final RefSetAutoMapperModel model;
	
	public SnomedRefSetAutoMapper(final Set<String> selectedAncestorIds, final RefSetAutoMapperModel model) {
		this.model = model;
		this.selectedAncestorIds = selectedAncestorIds;
	}

	public Map<Integer, String> resolveValues(final IProgressMonitor monitor) {
		
		final Map<Integer, String> resolvedValues = newHashMap();
		final Map<Integer, String> values = getValuesFromColumn(model.getMappedSourceColumnIndex());
		
		final List<ExtendedLocale> locales = ApplicationContext.getServiceForClass(LanguageSetting.class).getLanguagePreference();
		final IEventBus eventBus = ApplicationContext.getServiceForClass(IEventBus.class);
		final String userId = ApplicationContext.getServiceForClass(ICDOConnectionManager.class).getUserId();
		final String branchPath = BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE).getPath();
		
		for (final Entry<Integer, String> entry : values.entrySet()) {
			
			if (monitor.isCanceled()) {
				return resolvedValues;
			}
			
			// see com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionSearchRequest.doExecute(BranchContext)
			if (entry.getValue().length() > 1) {
				
				final SnomedConceptSearchRequestBuilder request = SnomedRequests
						.prepareSearchConcept()
						.filterByActive(true)
						.filterByTerm(entry.getValue())
						.filterByExtendedLocales(locales)
						.filterByEscg(generateExpression())
						.withSearchProfile(userId)
						.withDoi()
						.setExpand("pt()");
				
				for (int limit = 1; limit < SEARCHER_STEP_LIMIT; limit *= SEARCHER_STEP_INCREMENT_MULTIPLIER) {
					
					request.setLimit(limit);
					
					final SnomedConcepts concepts = request.build(branchPath).executeSync(eventBus);
					List<SnomedConceptIndexEntry> candidates = SnomedConceptIndexEntry.fromConcepts(concepts);
					
					if (candidates.isEmpty()) {
						final SnomedConcepts fuzzyConcepts = request.withFuzzySearch().build(branchPath).executeSync(eventBus);
						final List<SnomedConceptIndexEntry> fuzzyCandidates = SnomedConceptIndexEntry.fromConcepts(fuzzyConcepts);
						
						if (fuzzyCandidates.isEmpty()) {
							continue;
						}
						
						candidates = fuzzyCandidates;
					}
					
					final Optional<SnomedConceptIndexEntry> candidate = getCandidate(candidates, entry.getKey());
					
					if (candidate.isPresent()) {
						resolvedValues.put(entry.getKey(), candidate.get().getId());
						break;
					}
					
				}
			}
			
			monitor.worked(1);
		}
		
		return resolvedValues;
	}

	private String generateExpression() {
		
		if (selectedAncestorIds.isEmpty()) {
			
			return String.format("<%s", Concepts.ROOT_CONCEPT);
			
		} else if (selectedAncestorIds.size() == 1) {
			
			return String.format("<%s", Iterables.getOnlyElement(selectedAncestorIds));
			
		} else {
			
			String firstId = FluentIterable.from(selectedAncestorIds).first().get();
			
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("<%s", firstId));
			
			for (String id : selectedAncestorIds) {
				if (!id.equals(firstId)) {
					sb.append(String.format(" UNION <%s", id));
				}
			}
			
			return sb.toString();
		}
	}

	protected Map<Integer, String> getValuesFromColumn(final int targetColumn) {
		final Map<Integer, String> collectedValues = newHashMap();
		for (final AutoMapEntry entry : model.getContent()) {
			collectedValues.put(model.getContent().indexOf(entry), getParsedValueSafe(entry, targetColumn));
		}
		return collectedValues;
	}
	
	protected Optional<SnomedConceptIndexEntry> getCandidate(final Collection<SnomedConceptIndexEntry> candidates, final int rowIndex) {
		return FluentIterable.from(candidates).first();
	}

	private String getParsedValueSafe(final AutoMapEntry entry, final int index) {
		return entry.getParsedValues().size() > index ? entry.getParsedValues().get(index) : "";
	}
	
}