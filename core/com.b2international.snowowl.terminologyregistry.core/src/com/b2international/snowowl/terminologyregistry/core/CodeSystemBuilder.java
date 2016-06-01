/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.terminologyregistry.core;

import java.util.Map;

import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.b2international.snowowl.terminologymetadata.TerminologymetadataFactory;
import com.b2international.snowowl.terminologyregistry.core.builder.BaseCodeSystemBuilder;

/**
 * @since 4.7
 */
public class CodeSystemBuilder extends BaseCodeSystemBuilder<CodeSystemBuilder, CodeSystem, CodeSystemEntry> {
	
	@Override
	public CodeSystemBuilder withAdditionalProperties(final Map<String, String> valueMap) {
		// no additional values
		return getSelf();
	}

	@Override
	public String getRepositoryUuid() {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public CodeSystem create() {
		return TerminologymetadataFactory.eINSTANCE.createCodeSystem();
	}

}
