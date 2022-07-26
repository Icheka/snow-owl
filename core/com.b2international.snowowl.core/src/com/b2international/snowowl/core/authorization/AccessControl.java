/*
 * Copyright 2019-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.authorization;

import java.util.List;
import java.util.Map;

import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.context.TerminologyResourceRequest;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.monitoring.MonitoredRequest;
import com.google.common.collect.Lists;

/**
 * Represents an authorization context where a permission is required to get access.
 * 
 * @since 7.2
 */
public interface AccessControl {

	default List<Permission> getPermissions(ServiceProvider context, Request<ServiceProvider, ?> req) {
		return List.of(Permission.requireAny(getOperation(), getResources(context, req)));
	}
	
	/**
	 * @return a {@link Permission}s required to access/execute/etc. this request.
	 */
	default List<String> getResources(ServiceProvider context, Request<ServiceProvider, ?> req) {
		final List<String> accessedResources = Lists.newArrayList();
		
		if (!(this instanceof Request<?, ?>)) {
			throw new UnsupportedOperationException("AccessControl interface needs to be declared on Request implementations");
		}
		
		collectAccessedResources(context, req, accessedResources);

		// log a warning if the request does not support any known request execution contexts and fall back to superuser permission requirement
		if (accessedResources.isEmpty()) {
			context.log().warn("Request '{}' implicitly requires superuser permission which might be incorrect.", MonitoredRequest.toJson(context, req, Map.of()));
			accessedResources.add(Permission.ALL);
		}
		
		return accessedResources;
	}

	/**
	 * Using the current request and its context, collect and add all resources that are being accessed by this request and thus require permission
	 * authorization before proceeding to execution.
	 * 
	 * @param context
	 * @param req
	 * @param accessedResources
	 */
	default void collectAccessedResources(ServiceProvider context, Request<ServiceProvider, ?> req, final List<String> accessedResources) {
		// extract resourceUri format (new 8.x format)
		TerminologyResourceRequest<?> terminologyResourceRequest = Request.getNestedRequest(req, TerminologyResourceRequest.class);
		if (terminologyResourceRequest != null) {
			// accept both full resourceURI as resource and without resource type (as ID/path is often enough)
			accessedResources.add(Permission.asResource(terminologyResourceRequest.getResourceURI(context).toString()));
			accessedResources.add(Permission.asResource(terminologyResourceRequest.getResourceURI(context).withoutResourceType()));
			// if a resource that is being accessed is part of a bundle and the user has access to that bundle then it has access to the resource as well
			accessedResources.add(terminologyResourceRequest.getResource(context).getBundleId());
			accessedResources.addAll(Collections3.toImmutableSet(terminologyResourceRequest.getResource(context).getBundleAncestorIds()));
			// ensure Root bundle is not present when checking access
			accessedResources.remove(IComponent.ROOT_ID);
		}
	}

	/**
	 * @return the operation for this request access control configuration
	 */
	String getOperation();

}
