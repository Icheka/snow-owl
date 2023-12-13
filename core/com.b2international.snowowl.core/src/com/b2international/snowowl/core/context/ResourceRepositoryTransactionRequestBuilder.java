/*
 * Copyright 2021 B2i Healthcare, https://b2ihealthcare.com
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

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.events.RequestBuilder;
import com.b2international.snowowl.core.request.CommitResult;

/**
 * @since 8.0
 * @param <R> - the return type
 */
public interface ResourceRepositoryTransactionRequestBuilder<R> extends RequestBuilder<TransactionContext, R> {

	default AsyncRequest<CommitResult> build(String author, String commitComment) {
		return commit()
				.setAuthor(author)
				.setCommitComment(commitComment)
				.buildAsync();
	}
	
	default ResourceRepositoryCommitRequestBuilder commit() {
		return new ResourceRepositoryCommitRequestBuilder().setBody(build());
	}
	
}
