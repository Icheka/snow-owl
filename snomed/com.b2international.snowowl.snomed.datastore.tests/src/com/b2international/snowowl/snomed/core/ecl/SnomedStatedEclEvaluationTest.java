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
package com.b2international.snowowl.snomed.core.ecl;

import static com.b2international.index.revision.Revision.Expressions.ids;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.statedAncestors;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.statedParents;
import static com.b2international.snowowl.test.commons.snomed.DocumentBuilders.concept;
import static com.b2international.snowowl.test.commons.snomed.DocumentBuilders.relationship;
import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.validation.IResourceValidator;
import org.junit.Before;
import org.junit.Test;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.index.Index;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snomed.ecl.EclStandaloneSetup;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.config.IndexConfiguration;
import com.b2international.snowowl.core.config.RepositoryConfiguration;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.ecl.DefaultEclParser;
import com.b2international.snowowl.core.ecl.DefaultEclSerializer;
import com.b2international.snowowl.core.ecl.EclParser;
import com.b2international.snowowl.core.ecl.EclSerializer;
import com.b2international.snowowl.core.request.BranchSnapshotContentRequest;
import com.b2international.snowowl.core.request.ecl.EclRewriter;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.tree.Trees;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.snomed.RandomSnomedIdentiferGenerator;
import com.b2international.snowowl.test.commons.snomed.TestBranchContext;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

/**
 * @since 5.15.1
 */
public class SnomedStatedEclEvaluationTest extends BaseRevisionIndexTest {

	private static final Injector INJECTOR = new EclStandaloneSetup().createInjectorAndDoEMFRegistration();
	
	private static final String ROOT_CONCEPT = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String HAS_ACTIVE_INGREDIENT = Concepts.HAS_ACTIVE_INGREDIENT;
	private static final String SUBSTANCE = Concepts.SUBSTANCE;
	private static final String STATED_CONCEPT = RandomSnomedIdentiferGenerator.generateConceptId();

	private BranchContext context;
	
	@Before
	public void setup() {
		SnomedCoreConfiguration config = new SnomedCoreConfiguration();
		config.setConcreteDomainSupported(true);
		
		RepositoryConfiguration repositoryConfig = new RepositoryConfiguration();
		IndexConfiguration indexConfiguration = new IndexConfiguration();
		indexConfiguration.setResultWindow(IndexClientFactory.DEFAULT_RESULT_WINDOW);
		repositoryConfig.setIndexConfiguration(indexConfiguration);
		
		context = TestBranchContext.on(MAIN)
				.with(EclParser.class, new DefaultEclParser(INJECTOR.getInstance(IParser.class), INJECTOR.getInstance(IResourceValidator.class)))
				.with(EclSerializer.class, new DefaultEclSerializer(INJECTOR.getInstance(ISerializer.class)))
				.with(Index.class, rawIndex())
				.with(RevisionIndex.class, index())
				.with(SnomedCoreConfiguration.class, config)
				.with(RepositoryConfiguration.class, repositoryConfig)
				.with(ResourceURI.class, CodeSystem.uri("SNOMEDCT"))
				.with(EclRewriter.class, new EclRewriter())
				.build();
	}
	
	@Override
	protected void configureMapper(ObjectMapper mapper) {
		super.configureMapper(mapper);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.registerModule(new PrimitiveCollectionModule());
	}
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableSet.of(SnomedDescriptionIndexEntry.class, SnomedConceptDocument.class, SnomedRelationshipIndexEntry.class, SnomedRefSetMemberIndexEntry.class);
	}
	
	@Test
	public void statedRefinementWithZeroToOneCardinalityInAttributeConjuction() throws Exception {
		generateTestHierarchy();
		final Expression actual = eval(String.format("<<%s:[1..*]{[0..1]%s=<<%s}", ROOT_CONCEPT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		
		final Expression expected = and(
				descendantsOrSelfOf(ROOT_CONCEPT),
				ids(ImmutableSet.of(STATED_CONCEPT))
				);
		assertEquals(expected, actual);
	}
	
	private Expression eval(String expression) {
		return new BranchSnapshotContentRequest<>(MAIN, SnomedRequests.prepareEclEvaluation(expression).setExpressionForm(Trees.STATED_FORM).build())
				.execute(context)
				.getSync();		
	}
	
	private void generateTestHierarchy() {
		indexRevision(MAIN, 
			concept(STATED_CONCEPT).statedParents(Long.parseLong(ROOT_CONCEPT)).build(),
			relationship(STATED_CONCEPT, HAS_ACTIVE_INGREDIENT, SUBSTANCE, Concepts.STATED_RELATIONSHIP).relationshipGroup(1).build()
		);
	}
	
	private Expression descendantsOrSelfOf(String...conceptIds) {
		return Expressions.bool()
				.should(ids(ImmutableSet.copyOf(conceptIds)))
				.should(statedParents(ImmutableSet.copyOf(conceptIds)))
				.should(statedAncestors(ImmutableSet.copyOf(conceptIds))).build();
	}
	
	private static Expression and(Expression left, Expression right) {
		return Expressions.bool().filter(left).filter(right).build();
	}

}
