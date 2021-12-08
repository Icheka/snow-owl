/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.internal;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.revision.DefaultRevisionIndex;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.TimestampProvider;
import com.b2international.snowowl.core.ResourceTypeConverter;
import com.b2international.snowowl.core.authorization.AnyResourceAuthorization;
import com.b2international.snowowl.core.authorization.PermissionResourceAuthorization;
import com.b2international.snowowl.core.authorization.ResourceAuthorization;
import com.b2international.snowowl.core.config.IndexSettings;
import com.b2international.snowowl.core.config.RepositoryConfiguration;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.monitoring.MonitoringConfiguration;
import com.b2international.snowowl.core.plugin.ClassPathScanner;
import com.b2international.snowowl.core.plugin.Component;
import com.b2international.snowowl.core.repository.PathTerminologyResourceResolver;
import com.b2international.snowowl.core.setup.ConfigurationRegistry;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.Plugin;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.b2international.snowowl.core.uri.DefaultResourceURIPathResolver;
import com.b2international.snowowl.core.uri.ResourceURIPathResolver;
import com.b2international.snowowl.core.version.VersionDocument;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

/**
 * @since 3.3
 */
@Component
public final class SnowOwlPlugin extends Plugin {

	private static final String RESOURCES_INDEX = "resources";

	@Override
	public void init(SnowOwlConfiguration configuration, Environment env) {
		env.services().registerService(TerminologyRegistry.class, TerminologyRegistry.INSTANCE);
		env.services().registerService(ResourceURIPathResolver.class, new DefaultResourceURIPathResolver(true));
		env.services().registerService(PathTerminologyResourceResolver.class, new PathTerminologyResourceResolver.Default());
		env.services().registerService(TimestampProvider.class, new TimestampProvider.Default());
		env.services().registerService(ResourceTypeConverter.Registry.class, new ResourceTypeConverter.Registry(env.service(ClassPathScanner.class)));
		
		// configure monitoring support
		final MonitoringConfiguration monitoringConfig = configuration.getModuleConfig(MonitoringConfiguration.class);
		if (monitoringConfig.isEnabled()) {
			final PrometheusMeterRegistry registry = createRegistry(monitoringConfig);
			env.services().registerService(MeterRegistry.class, registry);
		} else {
			// XXX this works like a NOOP registry if you do NOT register any additional registries to it
			env.services().registerService(MeterRegistry.class, new CompositeMeterRegistry());
		}
	}
	
	@Override
	public void preRun(SnowOwlConfiguration configuration, Environment env) throws Exception {
		if (env.isServer()) {
			final ObjectMapper mapper = env.service(ObjectMapper.class);
			final Index resourceIndex = Indexes.createIndex(
				RESOURCES_INDEX, 
				mapper, 
				new Mappings(ResourceDocument.class, VersionDocument.class), 
				env.service(IndexSettings.class).forIndex(env.service(RepositoryConfiguration.class).getIndexConfiguration(), RESOURCES_INDEX)
			);
			
			final RevisionIndex revisionIndex = new DefaultRevisionIndex(resourceIndex, env.service(TimestampProvider.class), mapper);
			env.services().registerService(ResourceRepository.class, new ResourceRepository(revisionIndex));
			
			// The default resource authorization filtering chain; can be customized or replaced entirely in other plugins at startup
			final ResourceAuthorization resourceAuthorization = new AnyResourceAuthorization(new PermissionResourceAuthorization());
			env.services().registerService(ResourceAuthorization.class, resourceAuthorization);
		}
	}

	private PrometheusMeterRegistry createRegistry(final MonitoringConfiguration monitoringConfig) {
		final PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
		
		Map<String, String> tags = newHashMapWithExpectedSize(1 + monitoringConfig.getTags().size());
		// always set the application tag to snow_owl
		tags.put("application", "snow_owl");
		// override with tags coming from the config file
		tags.putAll(monitoringConfig.getTags());

		// configure the tags
		final List<Tag> commonTags = tags.entrySet()
				.stream()
				.map(entry -> Tag.of(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
		
		registry.config().commonTags(commonTags);
		
		// configure default JVM and node metrics
		new ClassLoaderMetrics().bindTo(registry);
		new JvmGcMetrics().bindTo(registry);
		new JvmMemoryMetrics().bindTo(registry);
		new JvmThreadMetrics().bindTo(registry);
		new UptimeMetrics().bindTo(registry);
		new ProcessorMetrics().bindTo(registry);
		new LogbackMetrics().bindTo(registry);
		new FileDescriptorMetrics().bindTo(registry);
		
		return registry;
	}

	@Override
	public void addConfigurations(ConfigurationRegistry registry) {
		registry.add("monitoring", MonitoringConfiguration.class);
	}

}