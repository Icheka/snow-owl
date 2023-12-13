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
package com.b2international.snowowl.snomed.core;

import java.util.List;

import org.slf4j.Logger;

import com.b2international.index.revision.Hooks.PreCommitHook;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.compare.DependencyComparer;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.ContextConfigurer;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContextDescriptions;
import com.b2international.snowowl.core.merge.ComponentRevisionConflictProcessor;
import com.b2international.snowowl.core.plugin.Component;
import com.b2international.snowowl.core.repository.ComponentDeletionPolicy;
import com.b2international.snowowl.core.repository.CompositeComponentDeletionPolicy;
import com.b2international.snowowl.core.repository.ContentAvailabilityInfoProvider;
import com.b2international.snowowl.core.repository.TerminologyRepositoryPlugin;
import com.b2international.snowowl.core.request.*;
import com.b2international.snowowl.core.request.version.VersioningRequestBuilder;
import com.b2international.snowowl.core.setup.ConfigurationRegistry;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.uri.ResourceURLSchemaSupport;
import com.b2international.snowowl.core.validation.eval.ValidationRuleEvaluator;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.merge.SnomedComponentRevisionConflictProcessor;
import com.b2international.snowowl.snomed.core.request.SnomedConceptSearchRequestEvaluator;
import com.b2international.snowowl.snomed.core.request.SnomedQueryOptimizerFactory;
import com.b2international.snowowl.snomed.core.uri.SnomedURLSchemaSupport;
import com.b2international.snowowl.snomed.core.version.SnomedVersioningRequest;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.index.change.SnomedRepositoryPreCommitHook;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.request.*;
import com.b2international.snowowl.snomed.datastore.request.ModuleRequest.ModuleIdProvider;
import com.b2international.snowowl.snomed.validation.SnomedQueryValidationRuleEvaluator;
import com.google.common.collect.ImmutableList;

/**
 * @since 7.0
 */
@Component
public final class SnomedPlugin extends TerminologyRepositoryPlugin {

	/**
	 * Unique identifier of the bundle. ID: {@value}
	 */
	public static final String PLUGIN_ID = "com.b2international.snowowl.snomed.datastore"; //$NON-NLS-1$
	
	@Override
	public void addConfigurations(ConfigurationRegistry registry) {
		registry.add("snomed", SnomedCoreConfiguration.class);
	}
	
	@Override
	public void init(SnowOwlConfiguration configuration, Environment env) throws Exception {
		final SnomedCoreConfiguration coreConfig = configuration.getModuleConfig(SnomedCoreConfiguration.class);
		env.services().registerService(SnomedCoreConfiguration.class, coreConfig);
		
		// register SNOMED CT Query based validation rule evaluator
		ValidationRuleEvaluator.Registry.register(new SnomedQueryValidationRuleEvaluator());
	}
	
	@Override
	protected ResourceURLSchemaSupport getTerminologyURISupport() {
		return new SnomedURLSchemaSupport();
	}
	
	@Override
	protected ContentAvailabilityInfoProvider getContentAvailabilityInfoProvider() {
		return context -> {
			return SnomedRequests.prepareSearchConcept()
				.setLimit(0)
				.filterById(Concepts.ROOT_CONCEPT)
				.build()
				.execute(context)
				.getTotal() > 0;
		};
	}
	
	@Override
	protected ContextConfigurer getRequestConfigurer() {
		return new ContextConfigurer() {
			@Override
			public <C extends ServiceProvider> C configure(C context) {
				// enhance all branch context by attaching the Synonyms cache to it
				if (context instanceof BranchContext) {
					BranchContext branchContext = (BranchContext) context;
					branchContext.bind(Synonyms.class, new Synonyms(branchContext));
					branchContext.bind(ModuleIdProvider.class, c -> c.getModuleId());
					branchContext.bind(NamespaceIdProvider.class, NamespaceIdProvider.DEFAULT);
					return (C) branchContext;
				} else {
					return context;
				}
			}
		};
	}
	
	@Override
	protected ConceptSearchRequestEvaluator getConceptSearchRequestEvaluator() {
		return new SnomedConceptSearchRequestEvaluator();
	}
	
	@Override
	protected QueryOptimizer getQueryOptimizer() {
		return new SnomedQueryOptimizerFactory();
	}
	
	@Override
	protected ComponentDeletionPolicy getComponentDeletionPolicy() {
		return CompositeComponentDeletionPolicy.of(SnomedDocument.class, doc -> !((SnomedDocument) doc).isReleased());
	}
	
	@Override
	public String getToolingId() {
		return SnomedTerminologyComponentConstants.TOOLING_ID;
	}
	
	@Override
	public String getName() {
		return "SNOMED CT";
	}
	
	@Override
	public boolean isEffectiveTimeSupported() {
		return true;
	}
	
	@Override
	public List<Class<? extends IComponent>> getTerminologyComponents() {
		return ImmutableList.<Class<? extends IComponent>>of(
			SnomedConcept.class,
			SnomedDescription.class,
			SnomedRelationship.class,
			SnomedReferenceSet.class,
			SnomedReferenceSetMember.class
		);
	}
	
	@Override
	protected VersioningRequestBuilder getVersioningRequestBuilder() {
		return (config) -> new TransactionalRequest(
			config.getUser(), 
			VersioningRequestBuilder.defaultCommitComment(config), 
			new SnomedVersioningRequest(config), 
			0L, 
			DatastoreLockContextDescriptions.CREATE_VERSION
		);
	}
	
	@Override
	protected PreCommitHook getTerminologyRepositoryPreCommitHook(Logger log) {
		return new SnomedRepositoryPreCommitHook(log);
	}
	
	@Override
	protected ComponentRevisionConflictProcessor getComponentRevisionConflictProcessor() {
		return new SnomedComponentRevisionConflictProcessor();
	}
	
	@Override
	protected ValueSetMemberSearchRequestEvaluator getMemberSearchRequestEvaluator() {
		return new SnomedValueSetMemberSearchRequestEvaluator();
	}
	
	//TODO: Refactor SnomedConceptMapSearchRequestEvaluator
	@Override
	protected ConceptMapMappingSearchRequestEvaluator getConceptMapMappingSearchRequestEvaluator() {
		return new SnomedConceptMapSearchRequestEvaluator();
	}
	
	@Override
	protected DependencyComparer getDependencyComparer() {
		return new SnomedDependencyComparer();
	}
}
