/*
 * Copyright 2023 B2i Healthcare, https://b2ihealthcare.com
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

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.commons.exceptions.IllegalQueryParameterException;
import com.b2international.index.query.QueryParseException;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.RepositoryBranchContext;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 8.10
 */
public final class BranchSnapshotContentRequest<B> extends DelegatingRequest<RepositoryContext, BranchContext, B> {

	private static final long serialVersionUID = 1L;
	
	private final String branchPath;
	
	public BranchSnapshotContentRequest(String branchPath, Request<BranchContext, B> next) {
		super(next);
		this.branchPath = checkNotNull(branchPath, "branchPath");
	}

	@Override
	public B execute(RepositoryContext context) {
		var index = context.service(RevisionIndex.class);
		return index.read(branchPath, searcher -> {
			try {
				return next(new RepositoryBranchContext(context, branchPath, searcher));
			} catch (QueryParseException e) {
				throw new IllegalQueryParameterException(e.getMessage());
			}
		});
	}
	
}
