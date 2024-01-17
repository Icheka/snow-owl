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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.b2international.index.query.Expressions;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.request.SearchPageableCollectionResourceRequestBuilder;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SystemRequestBuilder;
import com.b2international.snowowl.core.validation.issue.ValidationIssueSearchRequest.OptionKey;

/**
 * @since 6.0
 */
public final class ValidationIssueSearchRequestBuilder
		extends SearchPageableCollectionResourceRequestBuilder<ValidationIssueSearchRequestBuilder, ServiceProvider, ValidationIssues>
		implements SystemRequestBuilder<ValidationIssues> {

	ValidationIssueSearchRequestBuilder() {}
	
	public ValidationIssueSearchRequestBuilder filterByResultId(final String resultId) {
		return addOption(OptionKey.RESULT_ID, resultId);
	}
	
	public ValidationIssueSearchRequestBuilder filterByResultIds(final Iterable<String> resultIds) {
		return addOption(OptionKey.RESULT_ID, resultIds);
	}

	public ValidationIssueSearchRequestBuilder filterByRule(final String ruleId) {
		return addOption(OptionKey.RULE_ID, ruleId);
	}
	
	public ValidationIssueSearchRequestBuilder filterByRules(final Iterable<? extends String> ruleIds) {
		return addOption(OptionKey.RULE_ID, ruleIds);
	}
	
	public ValidationIssueSearchRequestBuilder filterByResourceUri(String resourceUri) {
		return addOption(OptionKey.RESOURCE_URI, resourceUri);
	}
	
	public ValidationIssueSearchRequestBuilder filterByResourceUri(ResourceURI resourceUri) {
		return addOption(OptionKey.RESOURCE_URI, resourceUri == null ? null : resourceUri.toString());
	}
	
	public ValidationIssueSearchRequestBuilder filterByResourceUris(Collection<ResourceURI> resourceUris) {
		return addOption(OptionKey.RESOURCE_URI, resourceUris == null ? null : resourceUris.stream().map(ResourceURI::toString).collect(Collectors.toSet()));
	}
	
	public ValidationIssueSearchRequestBuilder filterByTooling(String toolingId) {
		return addOption(OptionKey.TOOLING_ID, toolingId);
	}
	
	public ValidationIssueSearchRequestBuilder filterByTooling(Iterable<String> toolingIds) {
		return addOption(OptionKey.TOOLING_ID, toolingIds);
	}
	
	public ValidationIssueSearchRequestBuilder filterByAffectedComponentId(String affectedComponentId) {
		return addOption(OptionKey.AFFECTED_COMPONENT_ID, affectedComponentId);
	}
	
	public ValidationIssueSearchRequestBuilder filterByAffectedComponentId(Iterable<String> affectedComponentIds) {
		return addOption(OptionKey.AFFECTED_COMPONENT_ID, affectedComponentIds);
	}
	
	public ValidationIssueSearchRequestBuilder filterByAffectedComponentLabel(String affectedComponentLabel) {
		return addOption(OptionKey.AFFECTED_COMPONENT_LABEL, affectedComponentLabel);
	}
	
	public ValidationIssueSearchRequestBuilder isWhitelisted(boolean whitelisted) {
		return addOption(OptionKey.WHITELISTED, whitelisted);
	}
	
	public ValidationIssueSearchRequestBuilder filterByDetails(Map<String, Object> details) {
		return filterByDetails(details == null ? null : details.entrySet().stream().map(e -> Expressions.toDynamicFieldFilter(e.getKey(), e.getValue())).toList());
	}
	
	public ValidationIssueSearchRequestBuilder filterByDetails(List<String> detailPropertyFilters) {
		return addOption(OptionKey.DETAILS, detailPropertyFilters);
	} 
	
	@Override
	protected SearchResourceRequest<ServiceProvider, ValidationIssues> createSearch() {
		return new ValidationIssueSearchRequest();
	}

}