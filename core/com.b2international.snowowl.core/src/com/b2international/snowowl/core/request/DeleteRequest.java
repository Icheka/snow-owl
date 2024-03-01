/*
 * Copyright 2011-2024 B2i Healthcare, https://b2ihealthcare.com
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

import java.util.List;

import jakarta.validation.constraints.NotNull;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.authorization.AccessControl;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.repository.RevisionDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.5
 */
public final class DeleteRequest implements Request<TransactionContext, Boolean>, AccessControl {

	private static final long serialVersionUID = 1L;

	@JsonProperty
	@NotNull
	private String componentId;
	
	@NotNull
	private Class<? extends RevisionDocument> type;
	
	@NotNull
	private Boolean force;

	public DeleteRequest(String componentId, Class<? extends RevisionDocument> type, Boolean force) {
		this.componentId = componentId;
		this.type = type;
		this.force = force;
	}
	
	@Override
	public Boolean execute(TransactionContext context) {
		try {
			context.delete(context.lookup(componentId, type), force);
		} catch (ComponentNotFoundException e) {
			// ignore, probably already deleted
		}
		return Boolean.TRUE;
	}

	public String getComponentId() {
		return componentId;
	}
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_EDIT;
	}
	
	@Override
	public void collectAccessedResources(ServiceProvider context, Request<ServiceProvider, ?> req, List<String> accessedResources) {
		accessedResources.add(componentId);
		
		/*
		 * XXX: Permit deleting the component if the user has edit permission on the
		 * component itself or any of its container resource and bundle(s) -- the
		 * justification is that if the user has such permissions, they would be able to
		 * delete the entire resource or even multiple resources alongside the current
		 * one.
		 */
		AccessControl.super.collectAccessedResources(context, req, accessedResources);
	}
}
