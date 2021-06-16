/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.context.ResourceRepositoryRequestBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.BaseResourceSearchRequest.OptionKey;

/**
 * @since 8.0
 */
public abstract class BaseResourceSearchRequestBuilder<RB extends BaseResourceSearchRequestBuilder<RB, R>, R>
		extends SearchResourceRequestBuilder<RB, RepositoryContext, R>
		implements ResourceRepositoryRequestBuilder<R> {

	/**
	 * Filter matches by a {@link TermFilter} configuration
	 * 
	 * @param termFilter - configuration
	 * @return this builder
	 */
	public final RB filterByTitle(final TermFilter termFilter) {
		return addOption(OptionKey.TITLE, termFilter);
	}
	
	/**
	 * "Smart" search by title (taking prefixes, stemming, etc. into account)
	 * 
	 * @param title - the title to search for
	 * @return this builder
	 */
	public final RB filterByTitle(final String title) {
		return filterByTitle(title != null ? TermFilter.defaultTermMatch(title) : null);
	}
	
	/**
	 * Exact case sensitive match on title
	 * 
	 * @param exactTitle - the title to search for
	 * @return this builder
	 */
	public final RB filterByExactTitle(final String exactTitle) {
		return filterByTitle(exactTitle != null ? TermFilter.exactTermMatch(exactTitle) : null);
	}
	
	/**
	 * Exact case insensitive ASCII folded match on title
	 * 
	 * @param exactTitle - the title to search for
	 * @return this builder
	 */
	public final RB filterByExactTitleIgnoreCase(final String exactTitle) {
		return filterByTitle(exactTitle != null ? TermFilter.exactIgnoreCaseTermMatch(exactTitle) : null);
	}
	
	/**
	 * Filters matches by their title (exact match).
	 * 
	 * @param titles - at least one of these titles match
	 * @return this builder
	 */
	public final RB filterByTitleExact(Iterable<String> titles) {
		return addOption(OptionKey.TITLE_EXACT, titles);
	}

}
