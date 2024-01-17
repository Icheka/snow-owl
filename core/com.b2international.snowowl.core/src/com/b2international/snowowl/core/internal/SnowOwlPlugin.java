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
package com.b2international.snowowl.core.internal;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.elasticsearch.core.Set;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.commons.metric.Metrics;
import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.revision.DefaultRevisionIndex;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionBranch.BranchNameValidator;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.TimestampProvider;
import com.b2international.snowowl.core.DeprecationLogger;
import com.b2international.snowowl.core.ResourceTypeConverter;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.collection.TerminologyResourceCollectionToolingSupport;
import com.b2international.snowowl.core.config.IndexSettings;
import com.b2international.snowowl.core.config.RepositoryConfiguration;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.monitoring.MonitoringConfiguration;
import com.b2international.snowowl.core.plugin.ClassPathScanner;
import com.b2international.snowowl.core.plugin.Component;
import com.b2international.snowowl.core.repository.JsonSupport;
import com.b2international.snowowl.core.repository.PathTerminologyResourceResolver;
import com.b2international.snowowl.core.request.suggest.ConceptSuggester;
import com.b2international.snowowl.core.setup.ConfigurationRegistry;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.Plugin;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.b2international.snowowl.core.uri.DefaultResourceURIPathResolver;
import com.b2international.snowowl.core.uri.ResourceURIPathResolver;
import com.b2international.snowowl.core.uri.TerminologyResourceURIPathResolver;
import com.b2international.snowowl.core.version.VersionDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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
		env.services().registerService(DeprecationLogger.class, new DeprecationLogger());
		
		final ClassPathScanner scanner = env.service(ClassPathScanner.class);
		
		final ObjectMapper mapper = JsonSupport.getDefaultObjectMapper();
		mapper.registerModule(new PrimitiveCollectionModule());
		mapper.registerModule(new JavaTimeModule());
		env.services().registerService(ObjectMapper.class, mapper);
		
		env.services().registerService(TerminologyRegistry.class, TerminologyRegistry.INSTANCE);
		env.services().registerService(ResourceURIPathResolver.class, new DefaultResourceURIPathResolver(scanner.getComponentsByInterface(TerminologyResourceURIPathResolver.class), true));
		env.services().registerService(PathTerminologyResourceResolver.class, new PathTerminologyResourceResolver.Default());
		env.services().registerService(TimestampProvider.class, new TimestampProvider.Default());
		env.services().registerService(ResourceTypeConverter.Registry.class, new ResourceTypeConverter.Registry(scanner));
		env.services().registerService(ConceptSuggester.Registry.class, new ConceptSuggester.Registry(scanner, mapper));
		env.services().registerService(TerminologyResourceCollectionToolingSupport.Registry.class, new TerminologyResourceCollectionToolingSupport.Registry(scanner));
		
		// configure global branch name validator
		env.services().registerService(BranchNameValidator.class, new BranchNameValidator.Default(
			RevisionBranch.DEFAULT_ALLOWED_BRANCH_NAME_CHARACTER_SET, 
			RevisionBranch.DEFAULT_MAXIMUM_BRANCH_NAME_LENGTH, 
			Set.of(
				RevisionBranch.MAIN_PATH,
				ResourceURI.HEAD,
				ResourceURI.LATEST,
				ResourceURI.NEXT
			)
		));
		
		// configure monitoring support
		// add default global implementation of no-op request metrics, in case of some request needs it too early
		env.services().registerService(Metrics.class, Metrics.NOOP);
		
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