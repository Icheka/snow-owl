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
package com.b2international.snowowl.snomed.core.rest;

import org.junit.ClassRule;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.snomed.core.branch.BranchCompareRequestTest;
import com.b2international.snowowl.snomed.core.branch.SnomedBranchRequestTest;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.io.SnomedRefSetDSVExportTest;
import com.b2international.snowowl.snomed.core.io.commit.CommitInfoRequestTest;
import com.b2international.snowowl.snomed.core.issue.EclSerializerTest;
import com.b2international.snowowl.snomed.core.issue.IssueSO2503RemoteJobDynamicMappingFix;
import com.b2international.snowowl.snomed.core.request.ConceptMapSearchMappingRequestSnomedMapTypeReferenceSetTest;
import com.b2international.snowowl.snomed.core.request.ConceptSearchRequestSnomedTest;
import com.b2international.snowowl.snomed.core.request.SnomedOptimizationApiTest;
import com.b2international.snowowl.snomed.core.request.ValueSetMemberSearchSnomedReferenceSetTest;
import com.b2international.snowowl.snomed.core.rest.branches.SnomedBranchingApiTest;
import com.b2international.snowowl.snomed.core.rest.branches.SnomedMergeApiTest;
import com.b2international.snowowl.snomed.core.rest.branches.SnomedMergeConflictTest;
import com.b2international.snowowl.snomed.core.rest.classification.SnomedClassificationApiTest;
import com.b2international.snowowl.snomed.core.rest.components.*;
import com.b2international.snowowl.snomed.core.rest.ext.SnomedComponentEffectiveTimeRestoreTest;
import com.b2international.snowowl.snomed.core.rest.ext.SnomedExtensionCreationTest;
import com.b2international.snowowl.snomed.core.rest.ext.SnomedExtensionUpgradeTest;
import com.b2international.snowowl.snomed.core.rest.io.SnomedExportApiTest;
import com.b2international.snowowl.snomed.core.rest.io.SnomedImportApiTest;
import com.b2international.snowowl.snomed.core.rest.io.SnomedImportRowValidatorTest;
import com.b2international.snowowl.snomed.core.rest.perf.SnomedConceptCreatePerformanceTest;
import com.b2international.snowowl.snomed.core.rest.perf.SnomedMergePerformanceTest;
import com.b2international.snowowl.snomed.core.rest.suggest.SnomedSuggestApiTest;
import com.b2international.snowowl.snomed.core.rest.versioning.SnomedVersioningApiTest;
import com.b2international.snowowl.test.commons.BundleStartRule;
import com.b2international.snowowl.test.commons.Resources;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.b2international.snowowl.test.commons.SnowOwlAppRule;

/**
 * @since 1.0
 */
@RunWith(Suite.class)
@SuiteClasses({ 
	// RF2 release handling, imported content verification
	SnomedRf2NextReleaseImportTest.class,
	SnomedRf2ContentImportTest.class,
	// High-level issue test cases, Java API test cases
	IssueSO2503RemoteJobDynamicMappingFix.class,
	Issue3019FixDeletionOfReferringMembersTest.class,
	EclSerializerTest.class,
	// Optimization Java API Test
	SnomedOptimizationApiTest.class,
	// RESTful API test cases
	// Branching API
	SnomedBranchRequestTest.class,
	BranchCompareRequestTest.class,
	SnomedCompareRestRequestTest.class,
	SnomedBranchingApiTest.class,
	// Core Component API
	SnomedConceptApiTest.class,
	SnomedConceptSearchApiTest.class,
	SnomedConceptInactivationApiTest.class,
	SnomedDescriptionApiTest.class,
	SnomedRelationshipApiTest.class,
	SnomedConcreteValueApiTest.class,
	SnomedPartialLoadingApiTest.class,
	SnomedComponentInactivationApiTest.class,
	// RefSet/Member API
	SnomedRefSetApiTest.class,
	SnomedOfficialRefSetTest.class,
	SnomedComplexMapBlockRefSetTest.class,
	SnomedOfficialRefSetMemberTest.class,
	SnomedComplexMapBlockRefSetMemberTest.class,
	SnomedRefSetMemberApiTest.class,
	SnomedRefSetBulkApiTest.class,
	// Expression Labeling API
	SnomedExpressionLabelTest.class,
	// Generic API
	ConceptSearchRequestSnomedTest.class,
	ValueSetMemberSearchSnomedReferenceSetTest.class,
//	ConceptMapCompareSnomedMapTypeReferenceSetTest.class,
	ConceptMapSearchMappingRequestSnomedMapTypeReferenceSetTest.class,
	ConceptMapCompareDsvExportTest.class,
	SnomedSuggestApiTest.class,
	// Branch Merge API test cases
	SnomedMergeApiTest.class,
	SnomedMergeConflictTest.class,
	// Classification API
	SnomedClassificationApiTest.class,
	// Import API
	SnomedImportApiTest.class,
	SnomedImportRowValidatorTest.class,
	// Export API
	SnomedRefSetDSVExportTest.class,
	SnomedExportApiTest.class,
	// Module dependency test cases - they modify the MAIN branch so should be executed after tests that rely on MAIN branch stuff
	SnomedModuleDependencyRefsetTest.class,
	SnomedVersioningApiTest.class,
	// Extension test cases
	SnomedComponentEffectiveTimeRestoreTest.class,
	SnomedExtensionCreationTest.class,
	SnomedExtensionUpgradeTest.class, 
	// Performance test cases, should be the last tests to perform
	SnomedReferenceSetDeletionPerformanceTest.class,
	SnomedConceptCreatePerformanceTest.class,
	SnomedMergePerformanceTest.class,
	CommitInfoRequestTest.class
})
public class AllSnomedApiTests {

	@ClassRule
	public static final RuleChain appRule = RuleChain
			.outerRule(SnowOwlAppRule.snowOwl(AllSnomedApiTests.class))
			.around(new BundleStartRule("org.eclipse.jetty.osgi.boot"))
			.around(new BundleStartRule("com.b2international.snowowl.core.rest"))
			// import the 20210731 Full Release up until 20210131, the last Delta will be imported via the SnomedRf2NextReleaseImportTest
			.around(new SnomedContentRule(SnomedContentRule.SNOMEDCT, Resources.Snomed.MINI_RF2_INT_20210731, Rf2ReleaseType.FULL)
					.importUntil("20210131"))
			// create a new Extension CodeSystem that supports ComplexMapBlock reference set types by importing the relevant metadata and a few examples, based on the 20200131 release delta
			.around(new SnomedContentRule(SnomedContentRule.SNOMEDCT_COMPLEX_MAP_BLOCK_EXT, Resources.Snomed.MINI_RF2_COMPLEX_BLOCK_MAP, Rf2ReleaseType.DELTA)
					.extensionOf(CodeSystem.uri("SNOMEDCT/2020-01-31")))
			;

}
