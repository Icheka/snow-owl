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
package com.b2international.snowowl.core.authorization;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.b2international.commons.exceptions.UnauthorizedException;
import com.b2international.snowowl.core.RequestContext;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.RequestHeaders;
import com.b2international.snowowl.core.identity.AuthorizationHeaderVerifier;
import com.b2international.snowowl.core.identity.IdentityProvider;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.monitoring.MonitoredRequest;
import com.b2international.snowowl.core.util.PlatformUtil;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

/**
 * @since 7.2
 * @param <R> - the return value's type
 */
public final class AuthorizedRequest<R> extends DelegatingRequest<ServiceProvider, ServiceProvider, R> {

	private static final long serialVersionUID = 1L;
	
	public static final String AUTHORIZATION_HEADER = "Authorization";
	
	public AuthorizedRequest(Request<ServiceProvider, R> next) {
		super(next);
	}

	@Override
	public R execute(ServiceProvider context) {
		final Collection<Request<?, ?>> requests = getNestedRequests();
		
		// if a User object is already attached to the context, that means we have already authenticated the user, no need to do it again
		User user = context.optionalService(User.class).orElse(null);
		final ServiceProvider userContext;
		
		if (user != null) {
			 userContext = context;
		} else {
			final RequestHeaders requestHeaders = context.service(RequestHeaders.class);
			final String authorizationToken = requestHeaders.header(AUTHORIZATION_HEADER);

			final IdentityProvider identityProvider = context.service(IdentityProvider.class);
			// if there is no authentication configured
			if (IdentityProvider.UNPROTECTED == identityProvider) {
				// allow execution as SYSTEM user
				user = User.SYSTEM;
			} else if (Strings.isNullOrEmpty(authorizationToken)) {
				// allow login requests in
				if (requests.stream().allMatch(req -> req.getClass().isAnnotationPresent(Unprotected.class))) {
					user = User.SYSTEM;
				} else {
					// if there is authentication configured, but no authorization token found prevent execution and throw UnauthorizedException
					Request<?, ?> request = Iterables.getFirst(requests, null);
					if (PlatformUtil.isDevVersion()) {
						System.err.println(request);
					}
					throw new UnauthorizedException("Missing authorization token")
							.withDeveloperMessage("Unable to execute request '%s' without a proper authorization token. Supply one either as a standard HTTP Authorization header or via the token query parameter.", request.getType());
				}
			} else {
				// verify authorization header value
				user = context.service(AuthorizationHeaderVerifier.class).auth(authorizationToken);
				if (user == null) {
					throw new UnauthorizedException("Incorrect authorization token");
				}
			}

			// inject the user to the current RequestContext so that the entire request execution tree can access it
			// create a context with the User object injected
			context.service(RequestContext.class).bind(User.class, user);
			
			// generate new context with authorized bus instance
			userContext = context.inject()
					// and EventBus configured with header to access token in async execution scenarios
					.bind(IEventBus.class, new AuthorizedEventBus(context.service(IEventBus.class), requestHeaders.headers()))
					.build();
		}
		
		if (!User.SYSTEM.equals(user) && !user.isAdministrator()) {
			// authorize user whether it is permitted to execute the request(s) or not
			List<Permission> requiredPermissionsToExecute = requests
					.stream()
					.filter(AccessControl.class::isInstance)
					.map(AccessControl.class::cast)
					.flatMap(ac -> {
						List<Permission> permissions = ac.getPermissions(userContext, next());
						if (permissions.isEmpty()) {
							context.log().warn("No permissions required to execute request '{}'.", MonitoredRequest.toJson(context, next(), Map.of()));
						}
						return permissions.stream();
					})
					.collect(Collectors.toList());
			
			if (!requiredPermissionsToExecute.isEmpty()) {
				// throw a Forbidden Exception if the user does not have permission to perform the request
				context.optionalService(AuthorizationService.class)
					.orElse(AuthorizationService.DEFAULT)
					.checkPermission(context, user, requiredPermissionsToExecute);
			}
		}
		
		return next(userContext);
		
	}

}
