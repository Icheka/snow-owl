/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;

import org.tartarus.snowball.ext.EnglishStemmer;

import com.b2international.index.compat.TextConstants;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.Concept;
import com.b2international.snowowl.core.domain.Concepts;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

/**
 * A generic concept suggestion request that uses the generic search functionality to return related concepts of
 * interest to the user.
 * 
 * @since 7.7
 * @see ConceptSearchRequest
 * @see ConceptSuggestionRequestBuilder
 */
public final class ConceptSuggestionRequest extends SearchResourceRequest<BranchContext, Concepts> {

	private static final Enum<?> QUERY = com.b2international.snowowl.core.request.ConceptSearchRequestEvaluator.OptionKey.QUERY;
	private static final Enum<?> MUST_NOT_QUERY = com.b2international.snowowl.core.request.ConceptSearchRequestEvaluator.OptionKey.MUST_NOT_QUERY;
	
	// Split terms at delimiter or whitespace separators
	private static final Splitter TOKEN_SPLITTER = Splitter.on(TextConstants.WHITESPACE_OR_DELIMITER_MATCHER)
			.trimResults()
			.omitEmptyStrings();
	
	private static final int SCROLL_LIMIT = 1000;

	@Min(1)
	private int topTokenCount;
	
	@Min(1)
	private int minOccurrenceCount;
	
	void setTopTokenCount(int topTokenCount) {
		this.topTokenCount = topTokenCount;
	}
	
	void setMinOccurrenceCount(int minOccurrenceCount) {
		this.minOccurrenceCount = minOccurrenceCount;
	}

	@Override
	protected Concepts createEmptyResult(int limit) {
		return new Concepts(limit, 0);
	}

	@Override
	protected Concepts doExecute(BranchContext context) throws IOException {
		// Get the suggestion base set of concepts
		final ConceptSearchRequestBuilder baseRequestBuilder = new ConceptSearchRequestBuilder()
			.filterByInclusions(getCollection(QUERY, String.class))
			.filterByExclusions(getCollection(MUST_NOT_QUERY, String.class))
			.setLimit(SCROLL_LIMIT)
			.setLocales(locales());
		
		final SearchResourceRequestIterator<ConceptSearchRequestBuilder, Concepts> itr = new SearchResourceRequestIterator<>(
				baseRequestBuilder, 
				builder -> builder.build().execute(context));
		
		// Gather tokens
		final Multiset<String> tokenOccurrences = HashMultiset.create(); 
		final EnglishStemmer stemmer = new EnglishStemmer();

		while (itr.hasNext()) {
			final Concepts suggestionBase = itr.next();

			FluentIterable.from(suggestionBase)
				.transformAndConcat(concept -> getAllTerms(concept))
				.transform(term -> term.toLowerCase(Locale.US))
				.transformAndConcat(lowerCaseTerm -> TOKEN_SPLITTER.splitToList(lowerCaseTerm))
				// TODO: US-UK spelling variant replacements?
				.transform(token -> stemToken(stemmer, token))
				.copyInto(tokenOccurrences);
		}
		
		final String topTokens = Multisets.copyHighestCountFirst(tokenOccurrences)
				.elementSet()
				.stream()
				.limit(topTokenCount)
				.collect(Collectors.joining(" "));
		
		/* 
		 * Run a search with the top tokens and minimum number of matches, excluding everything
		 * that was included previously (you don't need to add previous exclusions as they only
		 * had an effect on included concepts).
		 */
		return new ConceptSearchRequestBuilder()
			.filterByAnyTerm(topTokens)
			.filterByExclusions(getCollection(QUERY, String.class))
			.setMinTermMatch(minOccurrenceCount)
			.setLimit(limit())
			.setSearchAfter(searchAfter())
			.setLocales(locales())
			.build()
			.execute(context);
	}

	private List<String> getAllTerms(Concept concept) {
		return ImmutableList.<String>builder()
			.add(concept.getTerm())
			.addAll(concept.getAlternativeTerms())
			.build();
	}

	private String stemToken(EnglishStemmer stemmer, String token) {
		stemmer.setCurrent(token);
		stemmer.stem();
		return stemmer.getCurrent();
	}
}
