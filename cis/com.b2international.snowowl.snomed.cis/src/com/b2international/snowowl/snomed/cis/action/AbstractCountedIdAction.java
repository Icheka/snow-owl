/*
 * Copyright 2011-2019 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.cis.action;

import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.cis.Identifiers;
import com.b2international.snowowl.snomed.cis.domain.SctId;
import com.b2international.snowowl.snomed.cis.domain.SctIds;
import com.b2international.snowowl.snomed.cis.request.AbstractSnomedIdentifierCountedRequestBuilder;
import com.google.common.base.MoreObjects;

/**
 * @since 4.5
 */
abstract class AbstractCountedIdAction extends AbstractIdAction<Set<String>> {

	private final String namespace;
	private final ComponentCategory category;
	private final int quantity;

	public AbstractCountedIdAction(final String namespace, final ComponentCategory category, final int quantity) {
		this.namespace = namespace;
		this.category = category;
		this.quantity = quantity;
	}
	
	@Override
	protected final Set<String> doExecute(RepositoryContext context) {
		final Object result = createRequestBuilder()
				.setNamespace(namespace)
				.setCategory(category)
				.setQuantity(quantity)
				.build()
				.execute(context);
		
		if (result instanceof SctIds) {
			return ((SctIds) result).stream().map(SctId::getSctid).collect(Collectors.toSet());
		} else {
			throw new UnsupportedOperationException("Unsupported return type: " + result);
		}
				
	}
	
	protected abstract AbstractSnomedIdentifierCountedRequestBuilder<?> createRequestBuilder();

	@Override
	protected final void doRollback(RepositoryContext context, Set<String> storedResults) {
		new Identifiers().prepareRelease()
			.setComponentIds(storedResults)
			.build()
			.execute(context);
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("namespace", namespace)
				.add("category", category)
				.add("quantity", quantity)
				.toString();
	}
}
