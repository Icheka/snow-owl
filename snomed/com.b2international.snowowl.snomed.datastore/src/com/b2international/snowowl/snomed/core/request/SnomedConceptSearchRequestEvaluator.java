/*
 * Copyright 2020-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.request;

import java.util.Comparator;
import java.util.Objects;
import java.util.SortedSet;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.Concept;
import com.b2international.snowowl.core.domain.Concepts;
import com.b2international.snowowl.core.request.ConceptSearchRequestEvaluator;
import com.b2international.snowowl.core.request.ExpandParser;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.snomed.core.SnomedDisplayTermType;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSortedSet;

/**
 * @since 7.5
 */
public final class SnomedConceptSearchRequestEvaluator implements ConceptSearchRequestEvaluator {

	private Concept toConcept(ResourceURI codeSystem, SnomedConcept snomedConcept, String pt, boolean requestedExpand) {
		final Concept concept = toConcept(codeSystem, snomedConcept, snomedConcept.getIconId(), pt, snomedConcept.getScore());
		
		SortedSet<String> alternativeTerms = snomedConcept.getPreferredDescriptions().stream()
			.map(pd -> pd.getTerm())
			.filter(term -> !Objects.equals(term, pt)) // leave the selected primary term out of the alternative terms list
			.collect(ImmutableSortedSet.toImmutableSortedSet(Comparator.naturalOrder()));
		
		if (!alternativeTerms.isEmpty()) {
			concept.setAlternativeTerms(alternativeTerms);
		}
		
		concept.setParentIds(snomedConcept.getParentIdsAsString());
		concept.setAncestorIds(snomedConcept.getAncestorIdsAsString());
		if (requestedExpand) {
			concept.setInternalConcept(snomedConcept);
		}
		return concept;
	}
	
	@Override
	public Concepts evaluate(ResourceURI uri, ServiceProvider context, Options search) {
		
		final String preferredDisplay = search.getString(OptionKey.DISPLAY);
		SnomedDisplayTermType displayTermType;
		
		if (preferredDisplay != null) {
			displayTermType = SnomedDisplayTermType.getEnum(preferredDisplay);
		} else {
			displayTermType = SnomedDisplayTermType.PT;
		}
		
		final SnomedConceptSearchRequestBuilder req = SnomedRequests.prepareSearchConcept();
		
		evaluateIdFilterOptions(req, search);
		evaluateTermFilterOptions(req, search);
		
		if (search.containsKey(OptionKey.ACTIVE)) {
			req.filterByActive(search.getBoolean(OptionKey.ACTIVE));
		}
		
		if (search.containsKey(OptionKey.PARENT)) {
			req.filterByParents(search.getCollection(OptionKey.PARENT, String.class));
		}
		
		if (search.containsKey(OptionKey.ANCESTOR)) {
			req.filterByAncestors(search.getCollection(OptionKey.ANCESTOR, String.class));
		}
		
		if (search.containsKey(OptionKey.MORE_LIKE_THIS_QUERY)) {
			req.filterByMoreLikeThis(search.getCollection(OptionKey.MORE_LIKE_THIS_QUERY, String.class));
			req.withDoi();
		}
		
		if (search.containsKey(OptionKey.TERM_TYPE)) {
			req.filterByDescriptionType(search.getString(OptionKey.TERM_TYPE));
		}
		
		evaluateQueryOptions(req, search);
		
		boolean requestedExpand = search.containsKey(OptionKey.EXPAND);
		// make sure preferredDescriptions() and displayTermType expansion data are always loaded
		Options expand = ExpandParser.parse("preferredDescriptions()")
				.merge(requestedExpand ? search.getOptions(OptionKey.EXPAND) : Options.empty());
		
		if (!Strings.isNullOrEmpty(displayTermType.getExpand())) {
			expand = ExpandParser.parse(displayTermType.getExpand()).merge(expand);
		}
		
		SnomedConcepts matches = req
				.setLocales(search.getList(OptionKey.LOCALES, ExtendedLocale.class))
				.setSearchAfter(search.getString(OptionKey.AFTER))
				.setLimit(search.get(OptionKey.LIMIT, Integer.class))
				.setFields(search.getList(OptionKey.FIELDS, String.class))
				.setExpand(expand)
				.sortBy(search.containsKey(SearchResourceRequest.OptionKey.SORT_BY) ? search.getList(SearchResourceRequest.OptionKey.SORT_BY, SearchResourceRequest.Sort.class) : null)
				.build(uri)
				.execute(context);

		return new Concepts(
			matches
				.stream()
				.map(concept -> toConcept(uri, concept, displayTermType.getLabel(concept), requestedExpand))
				.collect(Collectors.toList()), 
			matches.getSearchAfter(), 
			matches.getLimit(), 
			matches.getTotal()
		);
	}
	
}

