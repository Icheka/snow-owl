/*
 * Copyright 2011-2022 B2i Healthcare, https://b2ihealthcare.com
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

import java.util.Date;
import java.util.List;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.b2international.snowowl.core.request.ecl.AbstractComponentSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedSearchRequest.OptionKey;
import com.google.common.base.Splitter;

/**
 * Abstract class for SNOMED CT search request builders. It collects functionality common to SNOMED CT components.
 * 
 * @since 4.5
 */
public abstract class SnomedSearchRequestBuilder<B extends SnomedSearchRequestBuilder<B, R>, R extends PageableCollectionResource<?>> 
		extends AbstractComponentSearchRequestBuilder<B, BranchContext, R>
		implements SnomedContentRequestBuilder<R> {

	private static final String EFFECTIVE_TIME_RANGE_SEPARATOR = "...";
	private static final Splitter EFFECTIVE_TIME_RANGE_SPLITTER = Splitter.on(EFFECTIVE_TIME_RANGE_SEPARATOR);

	/**
	 * Filter to return components with the specified module id. 
	 * Commonly used module IDs are listed in the {@link com.b2international.snowowl.snomed.Concepts} class.
	 * 
	 * @param moduleId
	 * @return this builder
	 */
	public final B filterByModule(String moduleId) {
		return addOption(OptionKey.MODULE, moduleId);
	}
	
	/**
	 * Filter to return components with the specified module id set. 
	 * Commonly used module IDs are listed in the {@link com.b2international.snowowl.snomed.Concepts} class.
	 * 
	 * @param moduleIds
	 * @return
	 */
	public final B filterByModules(Iterable<String> moduleIds) {
		return addOption(OptionKey.MODULE, moduleIds);
	}

	/**
	 * Filter to return components with the specified state (active/inactive)
	 * 
	 * @param active
	 * @return this builder
	 */
	public final B filterByActive(Boolean active) {
		return addOption(OptionKey.ACTIVE, active);
	}
	
	/**
	 * Filter to return components with the specified released flag value (released|unreleased)
	 * 
	 * @param released
	 * @return this builder
	 */
	public final B filterByReleased(Boolean released) {
		return addOption(OptionKey.RELEASED, released);
	}

	/**
	 * Filter to return components with the specified effective time represented as a string in {@value DateFormats#SHORT} format represented either as single value for exact match or range value (with ... separator) as range value match. 
	 * 
	 * @param effectiveTime - the effective time filter value
	 * @return this builder
	 * @see DateFormats#SHORT
	 * @see EffectiveTimes
	 */
	public final B filterByEffectiveTime(String effectiveTime) {
		if (CompareUtils.isEmpty(effectiveTime)) {
			return getSelf(); 
		} else {
			if (EffectiveTimes.isUnset(effectiveTime)) {
				return filterByEffectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME);
			} else if (isEffectiveTimeRange(effectiveTime)) {
				final List<String> rangeValues = EFFECTIVE_TIME_RANGE_SPLITTER.splitToList(effectiveTime);
				if (rangeValues.size() != 2) {
					throw new BadRequestException("effectiveTime range filter requires a start and end argument in the form of <yyyyMMdd>...<yyyyMMdd>. Got: %s", effectiveTime);
				}
				return filterByEffectiveTime(EffectiveTimes.getEffectiveTime(rangeValues.get(0), DateFormats.SHORT), EffectiveTimes.getEffectiveTime(rangeValues.get(1), DateFormats.SHORT));
			} else {
				return filterByEffectiveTime(EffectiveTimes.getEffectiveTime(effectiveTime, DateFormats.SHORT));
			}
		}
	}
	
	private boolean isEffectiveTimeRange(String effectiveTime) {
		return effectiveTime.contains(EFFECTIVE_TIME_RANGE_SEPARATOR);
	}

	/**
	 * Filter to return components with the specified effective time represented as a long (ms since epoch) format.
	 * 
	 * @param effectiveTime - in long (ms since epoch) format
	 * @return this builder
	 * 
	 * @see EffectiveTimes
	 * @see Date#Date(long)
	 * @see Date#getTime()
	 */
	public final B filterByEffectiveTime(long effectiveTime) {
		return filterByEffectiveTime(effectiveTime, effectiveTime);
	}
	
	/**
	 * Filter to return components with the effective times that fall between the start and end dates
	 * represented as longs (ms since epoch).
	 * 
	 * @param from - effectiveTime starting effective time in long (ms since epoch) format
	 * @param to - effectiveTime ending effective time in long (ms since epoch) format
	 * @return this builder

	 * @see EffectiveTimes
	 * @see Date#Date(long)
	 * @see Date#getTime()
	 */
	public final B filterByEffectiveTime(long from, long to) {
		return addOption(OptionKey.EFFECTIVE_TIME_START, from).addOption(OptionKey.EFFECTIVE_TIME_END, to);
	}
	
	/**
	 * Set the ECL expression form to evaluate all ECL expressions on this form.
	 * 
	 * @param expressionForm
	 * @return this builder
	 */
	public final B setEclExpressionForm(String expressionForm) {
		return addOption(OptionKey.ECL_EXPRESSION_FORM, expressionForm);
	}
	
}
