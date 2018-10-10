/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.api.rest.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.api.rest.AbstractRestService;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 7.0
 */
@Api(value = "Metrics", description="Metrics", tags = { "metrics" })
@RestController
public class MetricsRestService extends AbstractRestService {
	
	@Autowired
	private PrometheusMeterRegistry registry;
	
	@ApiOperation(
			value="Retrieve metrics data about Snow Owl",
			notes="Retrive metrics data about Snow Owl which is parsable by the Prometheus monitoring system.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = String.class),
	})
	@GetMapping(value = "/metrics", produces = { AbstractRestService.TEXT_MEDIA_TYPE })
	public String getMetrics() {
		return registry.scrape();
	}

}
