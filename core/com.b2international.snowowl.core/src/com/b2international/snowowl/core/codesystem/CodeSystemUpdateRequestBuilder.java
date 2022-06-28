/*
 * Copyright 2011-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.codesystem;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.request.BaseResourceUpdateRequestBuilder;

/**
 * @since 4.7
 */
public final class CodeSystemUpdateRequestBuilder
		extends BaseResourceUpdateRequestBuilder<CodeSystemUpdateRequestBuilder, CodeSystemUpdateRequest> {

	private String oid;
	private String branchPath;
	private ResourceURI extensionOf;

	CodeSystemUpdateRequestBuilder(String uniqueId) {
		super(uniqueId);
	}

	public CodeSystemUpdateRequestBuilder setOid(String oid) {
		this.oid = oid;
		return getSelf();
	}

	public CodeSystemUpdateRequestBuilder setBranchPath(String branchPath) {
		this.branchPath = branchPath;
		return getSelf();
	}

	public CodeSystemUpdateRequestBuilder setExtensionOf(ResourceURI extensionOf) {
		this.extensionOf = extensionOf;
		return getSelf();
	}

	@Override
	public CodeSystemUpdateRequest createResourceRequest() {
		final CodeSystemUpdateRequest req = new CodeSystemUpdateRequest(getResourceId());

		req.setOid(oid);
		req.setBranchPath(branchPath);
		req.setExtensionOf(extensionOf);

		return req;
	}
}
