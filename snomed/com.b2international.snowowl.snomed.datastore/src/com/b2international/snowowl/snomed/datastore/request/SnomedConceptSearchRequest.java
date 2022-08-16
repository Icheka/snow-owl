/*
 * Copyright 2011-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.ancestors;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.parents;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.statedAncestors;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.statedParents;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.SortBy.Builder;
import com.b2international.index.query.SortBy.Order;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.repository.RevisionDocument;
import com.b2international.snowowl.core.request.KnnFilter;
import com.b2international.snowowl.core.request.ecl.AbstractComponentSearchRequest;
import com.b2international.snowowl.core.request.search.TermFilter;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.ecl.EclExpression;
import com.b2international.snowowl.snomed.core.tree.Trees;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionUtils;
import com.b2international.snowowl.snomed.datastore.converter.SnomedConceptConverter;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.google.common.base.Strings;

/**
 * SNOMED CT Concept search request implementation.
 * 
 * NOTE: Intentionally not a final class. Plug-in developers may subclass it to customize the behaviour for the own API.
 * 
 * @since 4.5
 */
public class SnomedConceptSearchRequest extends SnomedComponentSearchRequest<SnomedConcepts, SnomedConceptDocument> {

	private static final long serialVersionUID = 1L;
	
	private static final float MIN_DOI_VALUE = 1.05f;
	private static final float MAX_DOI_VALUE = 10288.383f;
	
	public enum OptionKey {

		/**
		 * Description term to (smart) match
		 */
		TERM,
		
		/**
		 * Description type to match
		 */
		DESCRIPTION_TYPE,
		
		/**
		 * Description semantic tag(s) to match
		 */
		SEMANTIC_TAG,

		/**
		 * ECL expression to match on the state form
		 */
		STATED_ECL,
		
		/**
		 * The definition status to match
		 */
		DEFINITION_STATUS,
		
		/**
		 * Parent concept ID that can be found in the inferred direct super type hierarchy
		 */
		PARENT,
		
		/**
		 * Ancestor concept ID that can be found in the inferred super type hierarchy (includes direct parents)
		 */
		ANCESTOR, 
		
		/**
		 * Parent concept ID that can be found in the stated direct super type hierarchy
		 */
		STATED_PARENT,
		
		/**
		 * Ancestor concept ID that can be found in the stated super type hierarchy (includes direct stated parents as well)
		 */
		STATED_ANCESTOR,
		
		/**
		 * Enable score boosting using DOI field
		 */
		USE_DOI, 
		
		/**
		 * Match concept descriptions where the description has language membership in one of the provided locales
		 */
		LANGUAGE_REFSET, 
		
		/**
		 * Knn filter to match concepts against a specified query vector
		 */
		KNN,
	}
	
	protected SnomedConceptSearchRequest() {}

	@Override
	protected Enum<?> getSpecialOptionKey() {
		return OptionKey.TERM;
	}
	
	@Override
	protected void collectAdditionalFieldsToFetch(Set<String> fieldsToLoad) {
		// load preferred descriptions field if not requested, but either pt or fsn is expanded
		if (!fieldsToLoad.contains(SnomedConceptDocument.Fields.PREFERRED_DESCRIPTIONS) 
				&& (expand().containsKey(SnomedConcept.Expand.FULLY_SPECIFIED_NAME) || expand().containsKey(SnomedConcept.Expand.PREFERRED_TERM))) {
			fieldsToLoad.add(SnomedConceptDocument.Fields.PREFERRED_DESCRIPTIONS);
		}
	}
	
	@Override
	protected String extractSpecialOptionValue(Options options, Enum<?> key) {
		return options.get(OptionKey.TERM, TermFilter.class).getSingleTermOrNull();
	}
	
	@Override
	protected Expression prepareQuery(BranchContext context) {
		ExpressionBuilder queryBuilder = Expressions.bool();
		
		addActiveClause(queryBuilder);
		addReleasedClause(queryBuilder);
		addIdFilter(queryBuilder, RevisionDocument.Expressions::ids);
		addEclFilter(context, queryBuilder, SnomedSearchRequest.OptionKey.MODULE, SnomedDocument.Expressions::modules);
		addEclFilter(context, queryBuilder, OptionKey.DEFINITION_STATUS, SnomedConceptDocument.Expressions::definitionStatusIds);
		addNamespaceFilter(queryBuilder);
		addNamespaceConceptIdFilter(context, queryBuilder);
		addEffectiveTimeClause(queryBuilder);
		addActiveMemberOfClause(context, queryBuilder);
		addMemberOfClause(context, queryBuilder);
		
		addFilter(queryBuilder, OptionKey.PARENT, String.class, SnomedConceptDocument.Expressions::parents);
		addFilter(queryBuilder, OptionKey.STATED_PARENT, String.class, SnomedConceptDocument.Expressions::statedParents);
		
		if (containsKey(OptionKey.ANCESTOR)) {
			final Collection<String> ancestorIds = getCollection(OptionKey.ANCESTOR, String.class);
			queryBuilder.filter(Expressions.bool()
					.should(parents(ancestorIds))
					.should(ancestors(ancestorIds))
					.build());
		}
		
		if (containsKey(OptionKey.STATED_ANCESTOR)) {
			final Collection<String> ancestorIds = getCollection(OptionKey.STATED_ANCESTOR, String.class);
			queryBuilder.filter(Expressions.bool()
					.should(statedParents(ancestorIds))
					.should(statedAncestors(ancestorIds))
					.build());
		}

		if (containsKey(OptionKey.STATED_ECL)) {
			if (containsKey(AbstractComponentSearchRequest.OptionKey.ECL)) {
				throw new BadRequestException("Unable to filter concepts by both stated and inferred ECL expression. Use either one of the two, not both.");
			}
			
			final String ecl = getString(OptionKey.STATED_ECL);
			Expression statedEclExpression = EclExpression.of(ecl, Trees.STATED_FORM).resolveToExpression(context).getSync(3, TimeUnit.MINUTES);
			if (statedEclExpression.isMatchNone()) {
				throw new NoResultException();
			} else if (!statedEclExpression.isMatchAll()) {
				queryBuilder.filter(statedEclExpression);
			}
		}
		
		addEclFilter(context, queryBuilder);
		
		final Expression queryExpression;
		
		if (containsKey(OptionKey.SEMANTIC_TAG)) {
			queryBuilder.filter(SnomedConceptDocument.Expressions.semanticTags(getCollection(OptionKey.SEMANTIC_TAG, String.class)));
		}
		
		if (containsKey(OptionKey.TERM)) {
			final ExpressionBuilder bq = Expressions.bool();
			// nest current query
			bq.filter(queryBuilder.build());
			queryBuilder = bq;
			
			final TermFilter termFilter = containsKey(OptionKey.TERM) ? get(OptionKey.TERM, TermFilter.class) : null;
			final Map<String, Float> conceptScoreMap = executeDescriptionSearch(context, termFilter);
			
			if (termFilter != null) {
				try {
					// XXX filtering multiple ID values via the term parameter is not supported, filterById can be used for that use case and should not be handled here unless a use case is provided for it
					final String singleTerm = termFilter.getSingleTermOrNull();
					if (!Strings.isNullOrEmpty(singleTerm)) {
						final ComponentCategory category = SnomedIdentifiers.getComponentCategory(singleTerm);
						if (category == ComponentCategory.CONCEPT) {
							conceptScoreMap.put(singleTerm, Float.MAX_VALUE);
						}
					}
				} catch (IllegalArgumentException e) {
					// ignored
				}
			}
			
			if (conceptScoreMap.isEmpty()) {
				throw new NoResultException();
			}
			
			queryBuilder.filter(RevisionDocument.Expressions.ids(conceptScoreMap.keySet()));
			
			queryExpression = Expressions.scriptScore(queryBuilder.build(), "doiFactor", Map.of("termScores", conceptScoreMap, "useDoi", containsKey(OptionKey.USE_DOI), "minDoi", MIN_DOI_VALUE, "maxDoi", MAX_DOI_VALUE));
		} else if (containsKey(OptionKey.USE_DOI)) {
			queryExpression = Expressions.scriptScore(queryBuilder.build(), "doi");
		} else {
			queryExpression = queryBuilder.build();
		}
		
		return queryExpression;
	}

	@Override
	protected void toQuerySortBy(BranchContext context, Builder sortBuilder, Sort sort) {
		if (sort instanceof SortField) {
			SortField sortField = (SortField) sort;
			if (SnomedConceptSearchRequestBuilder.TERM_SORT.equals(sortField.getField())) {
				final Set<String> synonymIds = context.service(Synonyms.class).get();
				final Map<String, Object> args = Map.of("locales", SnomedDescriptionUtils.getLanguageRefSetIds(context, locales()), "synonymIds", synonymIds);
				sortBuilder.sortByScript("termSort", args, sort.isAscending() ? Order.ASC : Order.DESC);
				return;
			}
		}
		super.toQuerySortBy(context, sortBuilder, sort);
	}
	
	@Override
	protected boolean trackScores() {
		return containsKey(OptionKey.TERM) || containsKey(OptionKey.USE_DOI) || containsKey(OptionKey.KNN);
	}

	@Override
	protected Class<SnomedConceptDocument> getSelect() {
		return SnomedConceptDocument.class;
	}
	
	@Override
	protected SnomedConcepts toCollectionResource(BranchContext context, Hits<SnomedConceptDocument> hits) {
		if (limit() < 1 || hits.getTotal() < 1) {
			return new SnomedConcepts(limit(), hits.getTotal());
		} else {
			return new SnomedConceptConverter(context, expand(), locales()).convert(hits);
		}
	}
	
	@Override
	protected SnomedConcepts createEmptyResult(int limit) {
		return new SnomedConcepts(limit, 0);
	}
	
	@Override
	protected KnnFilter getKnnFilter() {
		return get(OptionKey.KNN, KnnFilter.class);
	}
	
	@Override
	protected String getKnnField() {
		return SnomedConceptDocument.Fields.SIMILARITY_FIELD;
	}

	private Expression addSearchProfile(final Expression searchProfileQuery, final Expression query) {
		if (searchProfileQuery == null) {
			return query;
		} else {
			return Expressions.bool()
					.filter(searchProfileQuery)
					.filter(query)
					.build();
		}
	}
	
	private Map<String, Float> executeDescriptionSearch(BranchContext context, TermFilter termFilter) {
		final SnomedDescriptionSearchRequestBuilder requestBuilder = SnomedRequests.prepareSearchDescription()
			.all()
			.filterByActive(true)
			.setFields(SnomedDescriptionIndexEntry.Fields.ID, SnomedDescriptionIndexEntry.Fields.CONCEPT_ID)
			.sortBy(SCORE);
		
		if (containsKey(SnomedConceptSearchRequest.OptionKey.LANGUAGE_REFSET)) {
			List<ExtendedLocale> extendedLocales = getList(SnomedDescriptionSearchRequest.OptionKey.LANGUAGE_REFSET, ExtendedLocale.class);
			requestBuilder.filterByLanguageRefSets(SnomedDescriptionUtils.getLanguageRefSetIds(context, extendedLocales));
		}
			
		applyIdFilter(requestBuilder, (rb, ids) -> rb.filterByConcepts(ids));
		
		if (containsKey(OptionKey.DESCRIPTION_TYPE)) {
			final String type = getString(OptionKey.DESCRIPTION_TYPE);
			requestBuilder.filterByType(type);
		}
		
		if (termFilter != null) {
			requestBuilder.filterByTerm(termFilter);
		}
		
		final Collection<SnomedDescription> items = requestBuilder
			.build()
			.execute(context)
			.getItems();
		
		final Map<String, Float> conceptMap = newHashMap();
		
		for (SnomedDescription description : items) {
			if (!conceptMap.containsKey(description.getConceptId())) {
				conceptMap.put(description.getConceptId(), description.getScore());
			}
		}
		
		return conceptMap;
	}

}
