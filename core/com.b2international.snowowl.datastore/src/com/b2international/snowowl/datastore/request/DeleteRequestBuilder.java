/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request;

import org.eclipse.emf.ecore.EObject;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 4.5
 */
public class DeleteRequestBuilder extends BaseRequestBuilder<DeleteRequestBuilder, TransactionContext, Void> implements TransactionalRequestBuilder<Void> {

	private final String componentId;
	private final Class<? extends EObject> type;
	private Boolean force = Boolean.FALSE;

	public DeleteRequestBuilder(String componentId, Class<? extends EObject> type) {
		super();
		this.componentId = componentId;
		this.type = type;
	}

	/**
	 * Forces the deletion of the component if the value is <code>true</code>.
	 * 
	 * @param whether
	 *            to force or not the deletion
	 * @return
	 */
	public DeleteRequestBuilder force(boolean force) {
		this.force = force;
		return getSelf();
	}

	@Override
	protected Request<TransactionContext, Void> doBuild() {
		return new DeleteRequest(componentId, type, force);
	}

}
