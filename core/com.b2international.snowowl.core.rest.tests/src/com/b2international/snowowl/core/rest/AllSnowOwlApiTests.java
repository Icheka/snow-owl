/*
 * Copyright 2011-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.rest;

import org.junit.ClassRule;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.b2international.snowowl.core.bundle.BundleApiTest;
import com.b2international.snowowl.core.rest.auth.AuthorizationTest;
import com.b2international.snowowl.core.rest.auth.BasicAuthenticationTest;
import com.b2international.snowowl.core.rest.bundle.BundleRestApiTest;
import com.b2international.snowowl.core.rest.codesystem.CodeSystemApiDependencyTest;
import com.b2international.snowowl.core.rest.codesystem.CodeSystemApiTest;
import com.b2international.snowowl.core.rest.rate.RateLimitTest;
import com.b2international.snowowl.core.rest.resource.ResourceApiTest;
import com.b2international.snowowl.core.rest.resource.TerminologyResourceCollectionApiTest;
import com.b2international.snowowl.test.commons.SnowOwlAppRule;

/**
 * @since 1.0
 */
@RunWith(Suite.class)
@SuiteClasses({ 
	BasicAuthenticationTest.class,
	AuthorizationTest.class,
	RateLimitTest.class,
	CodeSystemApiTest.class,
	CodeSystemApiDependencyTest.class,
	ResourceApiTest.class,
	TerminologyResourceCollectionApiTest.class,
	BundleApiTest.class,
	BundleRestApiTest.class,
})
public class AllSnowOwlApiTests {
	
	@ClassRule
	public static final TestRule appRule = SnowOwlAppRule.snowOwl(AllSnowOwlApiTests.class).bootRestApi();

}
