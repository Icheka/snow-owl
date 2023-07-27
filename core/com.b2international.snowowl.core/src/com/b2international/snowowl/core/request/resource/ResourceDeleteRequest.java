/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request.resource;

import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.b2international.commons.StringUtils;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.Resources;
import com.b2international.snowowl.core.authorization.AccessControl;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.DeleteRequest;
import com.b2international.snowowl.core.request.ResourceRequests;

/**
 * @since 8.12
 */
public final class ResourceDeleteRequest implements Request<TransactionContext, Boolean>, AccessControl {

	private static final long serialVersionUID = 1L;
	
	private final ResourceURI resourceUri;
	private final boolean force;

	public ResourceDeleteRequest(ResourceURI resourceUri, boolean force) {
		this.resourceUri = Objects.requireNonNull(resourceUri);
		this.force = force;
	}
	
	@Override
	public Boolean execute(TransactionContext context) {
		if (!force) {
			// check references to this resource
			SortedSet<String> dependants = ResourceRequests.prepareSearch()
				// filter for any dependency entry, be it explicit, version or with query part
				.filterByDependency(String.format("uri:%s OR uri:%s/* OR uri:%s\\?*", resourceUri, resourceUri, resourceUri))
				.setLimit(1000)
				.setFields(ResourceDocument.Fields.ID)
				.stream(context)
				.flatMap(Resources::stream)
				.map(r -> r.getResourceURI().toString())
				.collect(Collectors.toCollection(TreeSet::new));
			
			if (!dependants.isEmpty()) {
				throw new BadRequestException("Resource '%s' is being referenced by other resources and it cannot be deleted. References are %s.", resourceUri.getResourceId(), StringUtils.limitedToString(dependants, 10));
			}
		}
		
		return new DeleteRequest(resourceUri.getResourceId(), ResourceDocument.class, force).execute(context);
	}
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_EDIT;
	}

}
