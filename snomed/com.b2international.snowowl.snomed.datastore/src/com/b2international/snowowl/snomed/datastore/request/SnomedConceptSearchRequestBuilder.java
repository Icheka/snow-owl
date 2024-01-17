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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.List;

import com.b2international.commons.http.AcceptLanguageHeader;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.DescriptionKnnFilterSupport;
import com.b2international.snowowl.core.request.KnnFilter;
import com.b2international.snowowl.core.request.KnnFilterSupport;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.search.TermFilter;
import com.b2international.snowowl.core.request.search.TermFilterSupport;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;

/**
 * <i>Builder</i> class to build requests responsible for searching SNOMED CT concepts. This class should be instantiated from the corresponding
 * static method on the central {@link SnomedRequests} class. Filter methods restrict the results set returned from the search requests; what passes
 * the filters will be returned as part of the pageable resultset.
 * 
 * @since 4.5
 */
public final class SnomedConceptSearchRequestBuilder extends SnomedComponentSearchRequestBuilder<SnomedConceptSearchRequestBuilder, SnomedConcepts>
		implements TermFilterSupport<SnomedConceptSearchRequestBuilder>, KnnFilterSupport<SnomedConceptSearchRequestBuilder>, DescriptionKnnFilterSupport<SnomedConceptSearchRequestBuilder> {

	/**
	 * Special term based sort key for sorting by preferred term based on locale and term.
	 */
	public static final String TERM_SORT = "term";

	/**
	 * Protected constructor. This class should be instantiated using the central {@link SnomedRequests} class.
	 */
	SnomedConceptSearchRequestBuilder() {
	}

	/**
	 * Enables degree-of-interest-based searching: concepts that are more often referred to in a clinical setting are preferred over less frequently
	 * used ones.
	 * <p>
	 * This filter affects the score of each result, even if no other score-based constraint is present. If results should be returned in order of
	 * relevance, specify {@link SearchResourceRequest#SCORE} as one of the sort fields.
	 * 
	 * @return <code>this</code> search request builder, for method chaining
	 * @see <a href="https://www.nlm.nih.gov/research/umls/Snomed/core_subset.html">The CORE Problem List Subset of SNOMED CT&reg;</a>
	 * @see #withDoi(Boolean)
	 */
	public final SnomedConceptSearchRequestBuilder withDoi() {
		return withDoi(true);
	}

	/**
	 * Enables/Disables degree-of-interest-based searching: concepts that are more often referred to in a clinical setting are preferred over less
	 * frequently used ones.
	 * <p>
	 * If enabled, this setting affects the score of each result, even if no other score-based constraint is present. If results should be returned in
	 * order of relevance, specify {@link SearchResourceRequest#SCORE} as one of the sort fields.
	 * 
	 * @return <code>this</code> search request builder, for method chaining
	 * @see <a href="https://www.nlm.nih.gov/research/umls/Snomed/core_subset.html">The CORE Problem List Subset of SNOMED CT&reg;</a>
	 * @see #withDoi(Boolean)
	 */
	public final SnomedConceptSearchRequestBuilder withDoi(Boolean withDoi) {
		return addOption(SnomedConceptSearchRequest.OptionKey.USE_DOI, withDoi);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SnomedConceptSearchRequestBuilder filterByTerm(TermFilter termFilter) {
		return addOption(SnomedConceptSearchRequest.OptionKey.TERM, termFilter);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public SnomedConceptSearchRequestBuilder filterByKnn(KnnFilter knnFilter) {
		return addOption(SnomedConceptSearchRequest.OptionKey.KNN, knnFilter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SnomedConceptSearchRequestBuilder filterByDescriptionKnn(KnnFilter knnFilter) {
		return addOption(SnomedConceptSearchRequest.OptionKey.DESCRIPTION_KNN, knnFilter);
	}

	/**
	 * Filters the concepts based on the type of its descriptions where the description type is specified by an ECL expression. representing the type.
	 * E.g.: "900000000000003001" for <i>Fully Specified Name</i>.
	 * 
	 * @param type
	 *            - description type represented concept identifiers or ECL expressions
	 * @return SnomedConceptSearchRequestBuilder
	 * 
	 * @see SnomedConcepts
	 */
	public final SnomedConceptSearchRequestBuilder filterByDescriptionType(String type) {
		return addOption(SnomedConceptSearchRequest.OptionKey.DESCRIPTION_TYPE, type);
	}

	/**
	 * Filters the concepts based on the semantic tag of its descriptions.
	 * 
	 * @param semanticTags
	 * @return {@link SnomedConceptSearchRequestBuilder}
	 * @see SnomedDescription
	 */
	public final SnomedConceptSearchRequestBuilder filterBySemanticTags(Iterable<String> semanticTags) {
		return addOption(SnomedConceptSearchRequest.OptionKey.SEMANTIC_TAG, semanticTags);
	}

	/**
	 * Filter matches in the stated tree by the specified Expression Constraint Language (ECL) expression. See <a href="http://snomed.org/ecl">ECL
	 * Specification and Guide</a> for more details.
	 * 
	 * @param expression
	 *            - ECL expression
	 * @return SnomedConceptSearchRequestBuilder
	 */
	public final SnomedConceptSearchRequestBuilder filterByStatedEcl(String expression) {
		return addOption(SnomedConceptSearchRequest.OptionKey.STATED_ECL, expression);
	}

	/**
	 * Filter that matches the specified parent identifier amongst the <b>direct</b> inferred super types. E.g.: a filter that returns the direct
	 * <i>inferred</i> children of the specified parent.
	 * 
	 * @param parentId
	 *            the SNOMED CT concept ID of the parent concept
	 * @return SnomedConceptSearchRequestBuilder
	 */
	public final SnomedConceptSearchRequestBuilder filterByParent(String parentId) {
		return addOption(SnomedConceptSearchRequest.OptionKey.PARENT, parentId);
	}

	/**
	 * Filter matches to have any of the specified parent identifiers amongst the direct inferred super types. E.g.:a filter that returns the direct
	 * <i>inferred</i> children of the specified parents.
	 * 
	 * @param parentIds
	 *            set of parent ids
	 * @return SnomedConceptSearchRequestBuilder
	 */
	public final SnomedConceptSearchRequestBuilder filterByParents(Iterable<String> parentIds) {
		return addOption(SnomedConceptSearchRequest.OptionKey.PARENT, parentIds);
	}

	/**
	 * Filter matches to have the specified parent identifier amongst the direct stated super types. E.g.: a filter that returns the direct
	 * <i>stated</i> children of the specified parent.
	 * 
	 * @param parentId
	 * @return SnomedConceptSearchRequestBuilder
	 */
	public final SnomedConceptSearchRequestBuilder filterByStatedParent(String parentId) {
		return addOption(SnomedConceptSearchRequest.OptionKey.STATED_PARENT, parentId);
	}

	/**
	 * Filter matches to have the specified parent identifier amongst the direct stated super types. E.g.:a filter that returns the direct
	 * <i>stated</i> children of the specified parents.
	 * 
	 * @param parentIds
	 *            set of parent ids
	 * @return SnomedConceptSearchRequestBuilder
	 */
	public final SnomedConceptSearchRequestBuilder filterByStatedParents(Iterable<String> parentIds) {
		return addOption(SnomedConceptSearchRequest.OptionKey.STATED_PARENT, parentIds);
	}

	/**
	 * Filter matches to have the specified ancestor identifier amongst the inferred super types (including direct as well). E.g.:a filter that
	 * returns all of the <i>inferred</i> (direct and non-direct) children of the specified parent.
	 * 
	 * @param ancestorId
	 * @return SnomedConceptSearchRequestBuilder
	 */
	public final SnomedConceptSearchRequestBuilder filterByAncestor(String ancestorId) {
		return addOption(SnomedConceptSearchRequest.OptionKey.ANCESTOR, ancestorId);
	}

	/**
	 * Filter matches to have any of the specified ancestor identifier amongst the inferred super types (including direct as well). E.g.:a filter that
	 * returns all of the <i>inferred</i> (direct and non-direct) children of the specified parents
	 * 
	 * @param ancestorIds
	 *            collection of ancestor IDs
	 * @return SnomedConceptSearchRequestBuilder
	 */
	public final SnomedConceptSearchRequestBuilder filterByAncestors(Iterable<String> ancestorIds) {
		return addOption(SnomedConceptSearchRequest.OptionKey.ANCESTOR, ancestorIds);
	}

	/**
	 * Filter matches to have the specified ancestor identifier amongst the stated super types (including direct as well). E.g.:a filter that returns
	 * all of the <i>stated</i> (direct and non-direct) children of the specified parent.
	 * 
	 * @param ancestorId
	 * @return SnomedConceptSearchRequestBuilder
	 */
	public final SnomedConceptSearchRequestBuilder filterByStatedAncestor(String ancestorId) {
		return addOption(SnomedConceptSearchRequest.OptionKey.STATED_ANCESTOR, ancestorId);
	}

	/**
	 * Filter matches to have any of the specified ancestor identifier amongst the stated super types (including direct as well). E.g.:a filter that
	 * returns all of the <i>stated</i> (direct and non-direct) children of the specified parents.
	 * 
	 * @param ancestorIds
	 * @return SnomedConceptSearchRequestBuilder
	 */
	public final SnomedConceptSearchRequestBuilder filterByStatedAncestors(Iterable<String> ancestorIds) {
		return addOption(SnomedConceptSearchRequest.OptionKey.STATED_ANCESTOR, ancestorIds);
	}

	/**
	 * Filter matches to have the specified definition status. Supports ECL expressions.
	 * 
	 * @param definitionStatusId
	 *            - id of the definition status concept or an ECL expression
	 * @return SnomedConceptSearchRequestBuilder
	 */
	public final SnomedConceptSearchRequestBuilder filterByDefinitionStatus(String definitionStatusId) {
		return addOption(SnomedConceptSearchRequest.OptionKey.DEFINITION_STATUS, definitionStatusId);
	}

	/**
	 * Filters concept that has a matching description with any acceptability in the given Accept-Language reference sets.
	 * 
	 * @param acceptLanguage
	 * @return
	 * @see AcceptLanguageHeader#parseHeader(String)
	 */
	public SnomedConceptSearchRequestBuilder filterByDescriptionLanguageRefSet(String acceptLanguage) {
		return filterByDescriptionLanguageRefSet(AcceptLanguageHeader.WILDCARD.equals(acceptLanguage) ? null : AcceptLanguageHeader.parseHeader(acceptLanguage));
	}

	/**
	 * Filters concept that has a matching description with any acceptability in the given list of {@link ExtendedLocale}s.
	 * 
	 * @param extendedLocales
	 * @return
	 */
	public SnomedConceptSearchRequestBuilder filterByDescriptionLanguageRefSet(List<ExtendedLocale> extendedLocales) {
		return addOption(SnomedDescriptionSearchRequest.OptionKey.LANGUAGE_REFSET, extendedLocales);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.b2international.snowowl.datastore.request.RevisionSearchRequestBuilder#createSearch()
	 */
	@Override
	protected SearchResourceRequest<BranchContext, SnomedConcepts> createSearch() {
		return new SnomedConceptSearchRequest();
	}
}
