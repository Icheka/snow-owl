/*
 * Copyright 2022-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.attachments.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.NotEmpty;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.attachments.InternalAttachmentRegistry;
import com.b2international.snowowl.core.events.Request;

/*package*/ class UploadChunkRequest implements Request<ServiceProvider, Long> {

	@NotNull
	private final UUID attachmentId;
	
	@NotEmpty
	private final byte[] chunk;
	
	/*package*/ UploadChunkRequest(final UUID attachmentId, final byte[] chunk) {
		this.attachmentId = attachmentId;
		this.chunk = chunk;
	}

	@Override
	public Long execute(final ServiceProvider context) {
		final InternalAttachmentRegistry service = (InternalAttachmentRegistry) context.service(AttachmentRegistry.class);
		final long bytesWritten = service.uploadChunk(attachmentId, chunk);
		return bytesWritten;
	}
}
