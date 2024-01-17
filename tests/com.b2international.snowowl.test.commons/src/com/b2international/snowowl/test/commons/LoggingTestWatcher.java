/*
 * Copyright 2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.test.commons;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * @since 9.0.0
 */
public class LoggingTestWatcher extends TestWatcher {

	@Override
	@OverridingMethodsMustInvokeSuper
	protected void starting(Description description) {
		System.out.println("===== Start of " + description + " =====");
	}
	
	@Override
	@OverridingMethodsMustInvokeSuper
	protected void finished(Description description) {
		System.out.println("===== End of " + description + " =====");
	}
	
}
