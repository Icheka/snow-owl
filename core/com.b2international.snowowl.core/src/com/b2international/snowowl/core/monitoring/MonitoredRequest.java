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
package com.b2international.snowowl.core.monitoring;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.StringUtils;
import com.b2international.commons.json.Json;
import com.b2international.commons.metric.Metrics;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.RequestHeaders;
import com.b2international.snowowl.core.events.util.ResponseHeaders;
import com.b2international.snowowl.core.identity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Timer.Sample;

/**
 * @since 4.5
 */
public final class MonitoredRequest<R> extends DelegatingRequest<ServiceProvider, ServiceProvider, R> {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = LoggerFactory.getLogger("request");
	
	public MonitoredRequest(Request<ServiceProvider, R> next) {
		super(next);
	}
	
	@Override
	public R execute(ServiceProvider context) {
		final MeterRegistry registry = context.service(MeterRegistry.class);
		final Sample responseTimeSample = Timer.start(registry);
		try {
			return next(context);
		} finally {
			final Tags tags = Tags.of("context", getContextId());
			tags.and("context", DEFAULT_CONTEXT_ID);
			final long responseTime = responseTimeSample.stop(registry.timer("response_time", tags));
			final Map<String, Object> additionalInfo = Maps.newHashMap();
			
			Json metrics = Json.object(
				"responseTime", TimeUnit.NANOSECONDS.toMillis(responseTime)
			);
			
			// append all externally measured request specific metrics
			metrics = metrics.merge(context.service(Metrics.class).getMeasurements());
			
			// register metrics
			additionalInfo.put("metrics", metrics);
			// register user sub
			context.optionalService(User.class).ifPresent(user -> {
				additionalInfo.put("user", Map.of(
					"sub", user.getUserId()
				));
			});
			// report metrics to log 
			LOG.info(toJson(context, next(), additionalInfo));
			// and also append them as response headers if requested via X-Profile request header
			if (context.service(RequestHeaders.class).has("X-Profile")) {
				String metricsValue;
				try {
					final ObjectMapper mapper = context.service(ObjectMapper.class);
					metricsValue = mapper.writeValueAsString(metrics);
				} catch (Throwable e) {
					metricsValue = "Unable to get request metrics: " + e.getMessage();
				}
				context.service(ResponseHeaders.class).set("X-Metrics", metricsValue);
			}
			
		}
	}

	public static String toJson(ServiceProvider context, Request<ServiceProvider, ?> request, final Map<String, Object> additionalInfo) {
		try {
			final ObjectMapper mapper = context.service(ObjectMapper.class);
			final Map<String, Object> body = mapper.convertValue(request, Map.class);
			body.putAll(additionalInfo);
			return mapper.writeValueAsString(truncateArrays(body));
		} catch (Throwable e) {
			return "Unable to get request description: " + e.getMessage();
		}
	}

	private static Map<String, Object> truncateArrays(Map<String, Object> json) {
		return Maps.transformValues(json, propertyValue -> {
			if (propertyValue instanceof Map<?, ?>) {
				return truncateArrays((Map<String, Object>) propertyValue);
			} else if (propertyValue instanceof Iterable<?>) {
				// TODO support arrays
				return StringUtils.limitedToString((Iterable<?>) propertyValue, 10);
			} else {
				return propertyValue;
			}
		});
	}
	
}
