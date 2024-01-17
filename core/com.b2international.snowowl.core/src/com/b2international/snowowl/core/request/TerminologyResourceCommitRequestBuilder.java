/*
 * Copyright 2021-2022 B2i Healthcare, https://b2ihealthcare.com
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

import java.util.Set;

import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.context.TerminologyResourceContentRequestBuilder;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 8.0
 */
public class TerminologyResourceCommitRequestBuilder 
		extends RepositoryCommitRequestBuilder<TerminologyResourceCommitRequestBuilder>
		implements TerminologyResourceContentRequestBuilder<CommitResult> {
	
	public static final Set<String> READ_ONLY_STATUSES = Set.of(Resource.RETIRED_STATUS);

	@Override
	public boolean snapshot() {
		return false;
	}

	@Override
	public Request<BranchContext, CommitResult> wrap(Request<BranchContext, CommitResult> req) {
		return new TerminologyResourceStatusCheckRequest<>(TerminologyResourceContentRequestBuilder.super.wrap(req), READ_ONLY_STATUSES);
	}
	
}
