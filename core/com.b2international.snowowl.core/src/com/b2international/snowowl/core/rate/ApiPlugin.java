/*
 * Copyright 2019-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.rate;

import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.plugin.Component;
import com.b2international.snowowl.core.setup.ConfigurationRegistry;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.Plugin;
import com.google.common.annotations.VisibleForTesting;

/**
 * @since 7.2
 */
@Component
public class ApiPlugin extends Plugin {

	@Override
	public void addConfigurations(ConfigurationRegistry registry) {
		registry.add("api", ApiConfiguration.class);
	}
	
	@Override
	public void init(SnowOwlConfiguration configuration, Environment env) throws Exception {
		ApiConfiguration apiConfig = configuration.getModuleConfig(ApiConfiguration.class);
		env.services().registerService(ApiConfiguration.class, apiConfig);
		initRateLimiter(env, apiConfig.getRateLimit());
	}

	@VisibleForTesting
	public void initRateLimiter(Environment env, RateLimitConfig config) {
		final RateLimiter limiter;
		if (config.isEnabled()) {
			limiter = new Bucket4jRateLimiter(config);
		} else {
			limiter = RateLimiter.NOOP;
		}
		env.services().registerService(RateLimiter.class, limiter);
	}
	
}
