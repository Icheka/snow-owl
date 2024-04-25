/*
 * Copyright 2021-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.fhir.core.request.codesystem;

import org.hl7.fhir.r5.model.Parameters;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.SystemRequestBuilder;
import com.b2international.snowowl.fhir.core.operations.CodeSystemSubsumptionParameters;
import com.b2international.snowowl.fhir.core.operations.CodeSystemSubsumptionResultParameters;

/**
 * @since 8.0
 */
public final class FhirCodeSystemSubsumesRequestBuilder 
		extends BaseRequestBuilder<FhirCodeSystemSubsumesRequestBuilder, ServiceProvider, CodeSystemSubsumptionResultParameters>
		implements SystemRequestBuilder<CodeSystemSubsumptionResultParameters> {

	private CodeSystemSubsumptionParameters parameters;
	
	public FhirCodeSystemSubsumesRequestBuilder setParameters(Parameters parameters) {
		this.parameters = new CodeSystemSubsumptionParameters(parameters);
		return getSelf();
	}
	
	@Override
	protected Request<ServiceProvider, CodeSystemSubsumptionResultParameters> doBuild() {
		return new FhirCodeSystemSubsumesRequest(parameters);
	}

}