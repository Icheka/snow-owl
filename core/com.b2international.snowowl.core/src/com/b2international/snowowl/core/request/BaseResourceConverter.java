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
package com.b2international.snowowl.core.request;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.index.Hits;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.plugin.ClassPathScanner;
import com.b2international.snowowl.core.request.expand.BaseResourceExpander;
import com.b2international.snowowl.core.request.expand.ResourceExpanderExtension;
import com.google.common.collect.Iterables;

/**
 * @since 4.0
 * @param <T> - document type
 * @param <R> - domain type
 * @param <CR> - collection resource type
 */
public abstract class BaseResourceConverter<T, R, CR extends CollectionResource<R>> extends BaseResourceExpander<R> {

	protected BaseResourceConverter(ServiceProvider context, Options expand, List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}

	public final CR convert(Hits<T> hits) {
		return convert(hits.getHits(), hits.getSearchAfter(), hits.getLimit(), hits.getTotal());
	}
	
	/**
	 * Convert a single internal index based entity to a resource based representation.
	 * 
	 * @param component
	 * @return
	 */
	public final R convert(T component) {
		return Iterables.getOnlyElement(convert(Collections.singleton(component), null, 1, 1));
	}

	/**
	 * Convert multiple internal index based entities to resource based representations.
	 * 
	 * @param components
	 * @param searchAfter
	 * @param limit
	 * @param total
	 * @return
	 */
	public final CR convert(Collection<T> components, String searchAfter, int limit, int total) {
		final List<R> results = components
				.stream()
				.map(this::toResource)
				.collect(Collectors.toList());
		
		if (!results.isEmpty()) {
			// perform additional transformations on the responses in bulk when needed
			alterResults(results);
			
			if (!expand().isEmpty()) {
				// expand using the current converter
				expand(results);
				// expand via plugins 
				expandViaPlugins(results);
			}
		}
		
		return createCollectionResource(results, searchAfter, limit, total);
	}
	
	/**
	 * Subclasses may optionally alter the result documents in bulk based on some
	 * logic if needed. Primarily alter the result in the
	 * {@link #toResource(Object)} method call if all information is available
	 * during the conversion. If for performance reasons results must be altered in
	 * bulk, then use this method.
	 * 
	 * @param results
	 */
	protected void alterResults(List<R> results) {
	}

	@Override
	public void expand(List<R> results) {
		// by default no expansions are performed
	}

	private final void expandViaPlugins(List<R> results) {
		context().optionalService(ClassPathScanner.class).ifPresent(scanner -> {
			scanner.getComponentsByInterface(ResourceExpanderExtension.class)
				.stream()
				.filter(expanderExtension -> expanderExtension.canExpand(getType()))
				.forEach(expanderExtension -> expanderExtension.create(context(), expand(), locales(), getType()).expand(results));
		});
	}

	protected abstract CR createCollectionResource(List<R> results, String searchAfter, int limit, int total);

	protected abstract R toResource(T entry);

	protected final LocalDate toEffectiveTime(final Long effectiveTimeAsLong) {
		return effectiveTimeAsLong == null ? null : EffectiveTimes.toDate(effectiveTimeAsLong);
	}
	
}
