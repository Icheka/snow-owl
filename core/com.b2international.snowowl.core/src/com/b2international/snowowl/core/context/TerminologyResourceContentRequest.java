/*
 * Copyright 2020-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.context;

import java.util.Objects;

import com.b2international.commons.exceptions.NotModifiedException;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.RequestHeaders;
import com.b2international.snowowl.core.events.util.ResponseHeaders;
import com.b2international.snowowl.core.rate.ApiConfiguration;
import com.b2international.snowowl.core.request.BranchRealtimeContentRequest;
import com.b2international.snowowl.core.request.BranchSnapshotContentRequest;
import com.b2international.snowowl.core.request.RepositoryRequest;
import com.b2international.snowowl.core.uri.ResourceURIPathResolver;
import com.b2international.snowowl.core.uri.ResourceURIPathResolver.PathWithVersion;
import com.google.common.base.Strings;

/**
 * @since 7.5
 */
public final class TerminologyResourceContentRequest<R> extends DelegatingRequest<TerminologyResourceContext, BranchContext, R> {

	private static final long serialVersionUID = 2L;
	
	private final boolean snapshot;
	
	public TerminologyResourceContentRequest(final Request<BranchContext, R> next) {
		this(next, true);
	}
	
	public TerminologyResourceContentRequest(final Request<BranchContext, R> next, final boolean snapshot) {
		super(next);
		this.snapshot = snapshot;
	}

	@Override
	public R execute(TerminologyResourceContext context) {
		final ResourceURI resourceURI = context.resourceURI();
		final TerminologyResource resource = context.resource();
		final PathWithVersion branchPathWithVersion = context.service(ResourceURIPathResolver.class).resolveWithVersion(context, resourceURI, resource);
		final String path = branchPathWithVersion.getPath();
		final ResourceURI versionResourceURI = branchPathWithVersion.getVersionResourceURI();
		
		if (versionResourceURI != null) {
			context = context.inject()
				.bind(ResourceURI.class, versionResourceURI)
				.bind(PathWithVersion.class, branchPathWithVersion)
				.build();
		}
		
		return new RepositoryRequest<R>(resource.getToolingId(),
			snapshot ? new BranchSnapshotContentRequest<>(path, nextWithCaching()) : new BranchRealtimeContentRequest<>(path, next())
		).execute(context);
	}

	private Request<BranchContext, R> nextWithCaching() {
		return ctx -> {
			
			final String eTag = ctx.service(RevisionSearcher.class).ref().eTag();
			
			// before executing the request, check whether we have an If-None-Match header set in the incoming request
			// if yes, check the current ETag with any of the attached values, if none matches evaluate the query otherwise send back HTTP 304
			String ifNoneMatchHeaderValue = ctx.optionalService(RequestHeaders.class).orElse(RequestHeaders.EMPTY).header(ApiConfiguration.IF_NONE_MATCH_HEADER);
			if (!Strings.isNullOrEmpty(ifNoneMatchHeaderValue) && Objects.equals(ifNoneMatchHeaderValue.replaceAll("\"", ""), eTag)) {
				throw new NotModifiedException();
			}
			
			R response = next().execute(ctx);
			
			// once we have the response ready calculate Cache-Control and ETag headers
			final ApiConfiguration apiConfiguration = ctx.service(ApiConfiguration.class);
			ctx.optionalService(ResponseHeaders.class).ifPresent(responseHeaders -> {
				responseHeaders.set(ApiConfiguration.ETAG_HEADER, eTag);
				// configure HTTP Cache-Control headers here using the currently configured global api.cache_control value
				responseHeaders.set(ApiConfiguration.CACHE_CONTROL_HEADER, apiConfiguration.getCacheControl());
			});
			
			return response;
		};
	}
}
