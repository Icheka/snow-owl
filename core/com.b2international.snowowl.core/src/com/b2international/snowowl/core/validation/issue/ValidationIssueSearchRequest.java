/*
 * Copyright 2017-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.validation.issue;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.index.Hits;
import com.b2international.index.Searcher;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;
import com.b2international.snowowl.core.request.SearchIndexResourceRequest;
import com.b2international.snowowl.core.validation.ValidationRequests;
import com.b2international.snowowl.core.validation.rule.ValidationRule;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * @since 6.0
 */
final class ValidationIssueSearchRequest extends SearchIndexResourceRequest<ServiceProvider, ValidationIssues, ValidationIssue> {
	
	private static final long serialVersionUID = 8763370532712025424L;
	
	public enum OptionKey {
		
		/**
		 * Filter matches by validation run/result identifier.
		 */
		RESULT_ID,
		
		/**
		 * Filter matches by rule identifier.
		 */
		RULE_ID,
		
		/**
		 * Filter matches by their rule's tooling ID field.
		 */
		TOOLING_ID,
		
		/**
		 * Filter matches by their rule's resourceURI field.
		 */
		RESOURCE_URI,
		
		/**
		 * Filter matches by affected component identifier(s).
		 */
		AFFECTED_COMPONENT_ID,
		
		/**
		 * Filter matches by a single value of affected component label.
		 */
		AFFECTED_COMPONENT_LABEL,
		
		/**
		 * Filter matches by that are whitelisted or not. 
		 */
		WHITELISTED,
		
		/**
		 * Filter matches by details
		 */
		DETAILS
	}
	
	
	ValidationIssueSearchRequest() {}
	
	@Override
	protected Searcher searcher(ServiceProvider context) {
		return context.service(ValidationRepository.class).searcher();
	}
	
	@Override
	protected boolean trackScores() {
		return containsKey(OptionKey.AFFECTED_COMPONENT_LABEL);
	}
	
	@Override
	protected Expression prepareQuery(ServiceProvider context) {
		final ExpressionBuilder queryBuilder = Expressions.bool();

		addIdFilter(queryBuilder, ids -> Expressions.matchAny(ValidationIssue.Fields.ID, ids));
		
		if (containsKey(OptionKey.RESULT_ID)) {
			queryBuilder.filter(Expressions.matchAny(ValidationIssue.Fields.RESULT_ID, getCollection(OptionKey.RESULT_ID, String.class)));
		} else {
			// Use "shared" result ID as the default value if not specified
			queryBuilder.filter(Expressions.exactMatch(ValidationIssue.Fields.RESULT_ID, ValidationRequests.SHARED_VALIDATION_RESULT_ID));
		}
		
		if (containsKey(OptionKey.RESOURCE_URI)) {
			queryBuilder.filter(Expressions.matchAny(ValidationIssue.Fields.RESOURCE_URI, getCollection(OptionKey.RESOURCE_URI, String.class)));
		}
		
		Set<String> filterByRuleIds = null;
		
		if (containsKey(OptionKey.TOOLING_ID)) {
			final Collection<String> toolingIds = getCollection(OptionKey.TOOLING_ID, String.class);
			
			final Set<String> ruleIds = ValidationRequests.rules().prepareSearch()
				.all()
				.filterByToolings(toolingIds)
				.setFields(ValidationRule.Fields.ID)
				.build()
				.execute(context)
				.stream()
				.map(ValidationRule::getId)
				.collect(Collectors.toSet());
			
			if (ruleIds.isEmpty()) {
				queryBuilder.filter(Expressions.matchNone());
			} else {
				filterByRuleIds = newHashSet(ruleIds);
			}
		}
		
		if (containsKey(OptionKey.RULE_ID)) {
			Set<String> ruleFilter = ImmutableSet.copyOf(getCollection(OptionKey.RULE_ID, String.class));
			if (filterByRuleIds != null) {
				SetView<String> diff = Sets.difference(ruleFilter, filterByRuleIds);
				if (!diff.isEmpty()) {
					throw new BadRequestException("Some of the ruleId filter values '%s' belong to a non-specified toolingId.", diff);
				}
				filterByRuleIds = ruleFilter;
			} else {
				filterByRuleIds = newHashSet(ruleFilter);
			}
		}
		
		if (filterByRuleIds != null) {
			queryBuilder.filter(Expressions.matchAny(ValidationIssue.Fields.RULE_ID, filterByRuleIds));
		}
		
		if (containsKey(OptionKey.AFFECTED_COMPONENT_ID)) {
			Collection<String> affectedComponentIds = getCollection(OptionKey.AFFECTED_COMPONENT_ID, String.class);
			queryBuilder.filter(Expressions.matchAny(ValidationIssue.Fields.AFFECTED_COMPONENT_ID, affectedComponentIds));
		}
		
		if (containsKey(OptionKey.AFFECTED_COMPONENT_LABEL)) {
			final String searchTerm = getString(OptionKey.AFFECTED_COMPONENT_LABEL);
			if (containsKey(OptionKey.AFFECTED_COMPONENT_ID)) {
				queryBuilder.must(Expressions.matchTextPhrase(ValidationIssue.Fields.AFFECTED_COMPONENT_LABELS_TEXT, searchTerm));
			} else {
				queryBuilder.must(
					Expressions.bool()
						.should(Expressions.dismaxWithScoreCategories(
							Expressions.matchTextPhrase(ValidationIssue.Fields.AFFECTED_COMPONENT_LABELS_TEXT, searchTerm),
							Expressions.matchTextAll(ValidationIssue.Fields.AFFECTED_COMPONENT_LABELS_PREFIX, searchTerm)
						))
						.should(Expressions.boost(Expressions.exactMatch(ValidationIssue.Fields.AFFECTED_COMPONENT_ID, searchTerm), 1000f))
					.build()
				);
			}
		}
		
		if (containsKey(OptionKey.WHITELISTED)) {
			boolean whitelisted = getBoolean(OptionKey.WHITELISTED);
			queryBuilder.filter(Expressions.match(ValidationIssue.Fields.WHITELISTED, whitelisted));
		}
		
		if (containsKey(OptionKey.DETAILS)) {
			queryBuilder.filter(Expressions.matchDynamic(ValidationIssue.Fields.DETAILS, getCollection(OptionKey.DETAILS, String.class)));
		}
		
		return queryBuilder.build();
	}

	@Override
	protected Class<ValidationIssue> getDocumentType() {
		return ValidationIssue.class;
	}

	@Override
	protected ValidationIssues toCollectionResource(ServiceProvider context, Hits<ValidationIssue> hits) {
		return new ValidationIssues(hits.getHits(), hits.getSearchAfter(), hits.getLimit(), hits.getTotal());
	}

	@Override
	protected ValidationIssues createEmptyResult(int limit) {
		return new ValidationIssues(limit, 0);
	}
	
}
