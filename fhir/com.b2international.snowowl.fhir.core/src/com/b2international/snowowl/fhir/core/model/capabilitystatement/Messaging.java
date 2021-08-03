/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.capabilitystatement;

import java.util.Collection;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.fhir.core.search.Mandatory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


/**
 * FHIR Capability statement Messaging backbone definition.
 * @since 8.0.0
 */
//@JsonDeserialize(builder = Messaging.Builder.class)
public class Messaging {
	
	//@JsonProperty("endpoint")
	//private final Collection<Endpoint> endpoints;
	
	

}
