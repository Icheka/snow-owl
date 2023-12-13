/*
 * Copyright 2019-2023 B2i Healthcare, https://b2ihealthcare.com
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

import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.CompareUtils;
import com.b2international.index.BulkDelete;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;
import com.b2international.snowowl.core.validation.ValidationDeleteNotification;
import com.b2international.snowowl.core.validation.ValidationRequests;
import com.b2international.snowowl.core.validation.rule.ValidationRule;
import com.b2international.snowowl.eventbus.IEventBus;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 6.20.0
 */
final class ValidationIssueDeleteRequest implements Request<ServiceProvider, Boolean> {

	private static final long serialVersionUID = 2L;
	
	@JsonProperty
	private final Set<String> resourceURIs;

	@JsonProperty
	private final Set<String> toolingIds;

	@JsonProperty
	private final Set<String> resultIds;
	
	ValidationIssueDeleteRequest(Set<String> resourceURIs, Set<String> toolingIds, Set<String> resultIds) {
		this.resourceURIs = resourceURIs;
		this.toolingIds = toolingIds;
		this.resultIds = resultIds;
	}
	
	@Override
	public Boolean execute(ServiceProvider context) {
		ExpressionBuilder query = Expressions.bool();
		
		if (!CompareUtils.isEmpty(resourceURIs)) {
			query.filter(Expressions.matchAny(ValidationIssue.Fields.RESOURCE_URI, resourceURIs));
		}
		
		if (!CompareUtils.isEmpty(toolingIds)) {
			final Set<String> rulesToDelete = ValidationRequests.rules().prepareSearch()
				.all()
				.filterByToolings(toolingIds)
				.build()
				.execute(context)
				.stream()
				.map(ValidationRule::getId)
				.collect(Collectors.toSet());
			
			query.filter(Expressions.matchAny(ValidationIssue.Fields.RULE_ID, rulesToDelete));
		}
		
		if (!CompareUtils.isEmpty(resultIds)) {
			query.filter(Expressions.matchAny(ValidationIssue.Fields.RESULT_ID, resultIds));
		} else {
			// Use "shared" result ID as the default value if not specified
			query.filter(Expressions.exactMatch(ValidationIssue.Fields.RESULT_ID, ValidationRequests.SHARED_VALIDATION_RESULT_ID));
		}
		
		return context.service(ValidationRepository.class).write(writer -> {
			
			writer.bulkDelete(new BulkDelete<>(ValidationIssue.class, query.build()));
			writer.commit();
			
			new ValidationDeleteNotification(resourceURIs, toolingIds, resultIds).publish(context.service(IEventBus.class));
			
			return Boolean.TRUE;
		});
	}

}
