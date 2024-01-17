/*
 * Copyright 2018-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.repository;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.index.IndexClient;
import com.b2international.index.es.client.EsClient;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.revision.Hooks;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryInfo;
import com.b2international.snowowl.core.RepositoryInfo.Health;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.compare.DependencyComparer;
import com.b2international.snowowl.core.compare.ResourceContentComparer;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.domain.ContextConfigurer;
import com.b2international.snowowl.core.merge.ComponentRevisionConflictProcessor;
import com.b2international.snowowl.core.merge.IMergeConflictRule;
import com.b2international.snowowl.core.request.*;
import com.b2international.snowowl.core.request.ecl.EclRewriter;
import com.b2international.snowowl.core.request.version.VersioningRequestBuilder;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.Plugin;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.core.terminology.Terminology;
import com.b2international.snowowl.core.terminology.TerminologyComponent;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.b2international.snowowl.core.uri.ResourceURLSchemaSupport;

/**
 * @since 7.0
 */
public abstract class TerminologyRepositoryPlugin extends Plugin implements Terminology {

	private static final Logger LOG = LoggerFactory.getLogger("repository");
	
	@Override
	public final void run(SnowOwlConfiguration configuration, Environment env) throws Exception {
		// register terminology and component definitions
		TerminologyRegistry registry = env.service(TerminologyRegistry.class);
		registry.register(this);
		getAdditionalTerminologyComponents().values().forEach(additionalTerminologyComponent -> registry.register(getToolingId(), additionalTerminologyComponent));
		
		if (env.isServer()) {
			final DefaultRepositoryManager repositories = (DefaultRepositoryManager) env.service(RepositoryManager.class);
			final RepositoryBuilder builder = repositories.prepareCreate(getToolingId());
			
			final Repository repo = builder
					.withPreCommitHook(getTerminologyRepositoryPreCommitHook(builder.log()))
					.addTerminologyComponents(getTerminologyComponents())
					.addTerminologyComponents(getAdditionalTerminologyComponents())
					.addMappings(getAdditionalMappings())
					.bind(ComponentDeletionPolicy.class, getComponentDeletionPolicy())
					.bind(VersioningRequestBuilder.class, getVersioningRequestBuilder())
					.bind(ComponentRevisionConflictProcessor.class, getComponentRevisionConflictProcessor())
					.bind(ConceptSearchRequestEvaluator.class, getConceptSearchRequestEvaluator())
					.bind(ValueSetMemberSearchRequestEvaluator.class, getMemberSearchRequestEvaluator())
					.bind(ConceptMapMappingSearchRequestEvaluator.class, getConceptMapMappingSearchRequestEvaluator())
					.bind(QueryOptimizer.class, getQueryOptimizer())
					.bind(ContentAvailabilityInfoProvider.class, getContentAvailabilityInfoProvider())
					.bind(ContextConfigurer.class, getRequestConfigurer())
					.bind(ResourceURLSchemaSupport.class, getTerminologyURISupport())
					.bind(EclRewriter.class, getEclRewriter())
					.bind(DependencyComparer.class, getDependencyComparer())
					.bind(ResourceContentComparer.class, getContentComparer())
					.build(env);
			
			RepositoryInfo status = repo.status();
			if (status.health() == Health.GREEN) {
				LOG.info("Started repository '{}' with status '{}'", repo.id(), status.health());
			} else {
				LOG.warn("Started repository '{}' with status '{}'. Diagnosis: {}.", status.id(), status.health(), status.diagnosis());
			}
			
			// register EsClient from repository globally
			env.services().registerService(EsClient.class, repo.service(IndexClient.class).client());
		}
		afterRun(configuration, env);
	}
	
	protected EclRewriter getEclRewriter() {
		return new EclRewriter();
	}
	
	/**
	 * Subclasses may override to provide a terminology specific URI format to enforce when creating resources and/or their versions in this
	 * repository. By default it configures the {@link ResourceURLSchemaSupport#DEFAULT default implementation}, which supports any URI without any
	 * format.
	 * 
	 * @return
	 */
	protected ResourceURLSchemaSupport getTerminologyURISupport() {
		return ResourceURLSchemaSupport.DEFAULT;
	}

	/**
	 * Subclasses may override to provide a terminology specific request configurer to configure incoming requests. The default implementation of this method returns a no-op request configurer.
	 * 
	 * @return 
	 */
	protected ContextConfigurer getRequestConfigurer() {
		return ContextConfigurer.NOOP;
	}

	protected abstract ContentAvailabilityInfoProvider getContentAvailabilityInfoProvider();
	
	/**
	 * An evaluator that can evaluate generic {@link ConceptSearchRequest concept search requests}. 
	 * @return a {@link ConceptSearchRequestEvaluator} instance
	 * @see ConceptSearchRequestBuilder
	 * @see ConceptSearchRequest
	 */
	protected ConceptSearchRequestEvaluator getConceptSearchRequestEvaluator() {
		return ConceptSearchRequestEvaluator.NOOP;
	}

	/**
	 * Subclasses may override to provide a customized {@link QueryOptimizer} for the underlying terminology tooling and query language.
	 * <p>
	 * The default implementation does not suggest changes for any incoming queries.
	 * @return
	 */
	protected QueryOptimizer getQueryOptimizer() {
		return QueryOptimizer.NOOP;
	}
	
	/**
	 * An evaluator that can evaluate generic {@link ValueSetMemberSearchRequest member search requests}. 
	 * @return a {@link SetMemberSearchRequestEvaluator} instance
	 * @see ValueSetMemberSearchRequestBuilder
	 * @see ValueSetMemberSearchRequest
	 */
	protected ValueSetMemberSearchRequestEvaluator getMemberSearchRequestEvaluator() {
		return ValueSetMemberSearchRequestEvaluator.NOOP;
	}
	
	/**
	 * An evaluator that can evaluate generic {@link ConceptMapMappingSearchRequest member search requests}. 
	 * @return a {@link ConceptMapMappingSearchRequestEvaluator} instance
	 * @see ConceptMapMappingSearchRequestBuilder
	 * @see ConceptMapMappingSearchRequest
	 */
	protected ConceptMapMappingSearchRequestEvaluator getConceptMapMappingSearchRequestEvaluator() {
		return ConceptMapMappingSearchRequestEvaluator.NOOP;
	}
	
	
	/**
	 * Subclasses may override to provide customized {@link ComponentDeletionPolicy} for the underlying repository.
	 * @return
	 */
	protected ComponentDeletionPolicy getComponentDeletionPolicy() {
		return ComponentDeletionPolicy.ALLOW;
	}

	/**
	 * Additional mappings can be registered on top of the existing {@link TerminologyComponent} assigned to this terminology tooling.
	 * @return
	 */
	protected Collection<Class<?>> getAdditionalMappings() {
		return Collections.emptyList();
	}
	
	/**
	 * @return the additional terminology components for this terminology.
	 */
	protected Map<Class<?>, TerminologyComponent> getAdditionalTerminologyComponents() {
		return Collections.emptyMap();
	}

	/**
	 * Subclasses may override to provide additional service configuration via this {@link TerminologyRepositoryPlugin} after the initialization of the repository.
	 * @param configuration
	 * @param env
	 * @throws Exception
	 */
	protected void afterRun(SnowOwlConfiguration configuration, Environment env) throws Exception {
	}

	/**
	 * Subclasses may override and provide a custom precommit hook to be installed on the underlying repository. {@link BaseRepositoryPreCommitHook}
	 * is a good candidate to extend and use for any particular terminology plugin.
	 * 
	 * @param log
	 * @return
	 * @see BaseRepositoryPreCommitHook
	 */
	protected Hooks.PreCommitHook getTerminologyRepositoryPreCommitHook(Logger log) {
		return staging -> {};
	}
	
	/**
	 * Subclasses may override this method to provide custom
	 * {@link VersioningRequestBuilder} instances to customize the versioning
	 * process in the underlying terminology.
	 * 
	 * @return
	 */
	protected VersioningRequestBuilder getVersioningRequestBuilder() {
		return VersioningRequestBuilder.DEFAULT;
	}
	
	/**
	 * Subclasses may override this method to provide custom conflict processor implementation along with additional {@link IMergeConflictRule}s. By
	 * default the repository will use a {@link ComponentRevisionConflictProcessor} without any {@link IMergeConflictRule}s.
	 * 
	 * @return
	 */
	protected ComponentRevisionConflictProcessor getComponentRevisionConflictProcessor() {
		return new ComponentRevisionConflictProcessor(Collections.emptyList());
	}

	/**
	 * @return 
	 */
	protected DependencyComparer getDependencyComparer() {
		// Find the type corresponding to the "Concept" component category
		final Optional<String> conceptType = getTerminologyComponents().stream()
			.map(componentClass -> Terminology.getAnnotation(componentClass))
			.filter(annotation -> ComponentCategory.CONCEPT.equals(annotation.componentCategory()))
			.map(conceptAnnotation -> DocumentMapping.getDocType(conceptAnnotation.docType()))
			.findFirst();
		
		if (conceptType.isPresent()) {
			return new DependencyComparer.Default(conceptType.get());
		} else {
			return DependencyComparer.NOOP;
		}
	}
	
	protected ResourceContentComparer getContentComparer() {
		return ResourceContentComparer.NOOP;		
	}
}
