/*
 * Copyright 2011-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.rest.admin;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.b2international.snowowl.core.messaging.MessagingRequests;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.CoreApiConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Spring controller for exposing messaging API.
 * 
 * @since 7.0 
 */
@Tag(description="Administration", name = CoreApiConfig.ADMINISTRATION)
@RestController
@RequestMapping(value = "/messages")
public class MessagingRestService extends AbstractRestService {

	@PostMapping(value = "/send", consumes = { AbstractRestService.TEXT_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(
		summary = "Send message to connected users",
		description = "Sends an informational message to all connected users; the message is displayed "
				+ "in the desktop application immediately."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Message sent")
	})
	public void sendMessage(
			@RequestBody
			@Parameter(description = "the message to send")
			final String message) {
		
		MessagingRequests.prepareSendMessage(message)
			.buildAsync()
			.execute(getBus())
			.getSync();
	}
}
