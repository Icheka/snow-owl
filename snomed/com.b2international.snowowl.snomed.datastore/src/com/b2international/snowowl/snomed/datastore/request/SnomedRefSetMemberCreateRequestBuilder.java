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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.Collections;
import java.util.Map;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.Requests;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;

/**
 * <i>Builder</i> class to build requests responsible for creating SNOMED CT reference set members.
 * This class should be instantiated from the corresponding static method on the central {@link SnomedRequests} class.
 * 
 * @since 4.5
 */
public final class SnomedRefSetMemberCreateRequestBuilder 
		extends BaseRequestBuilder<SnomedRefSetMemberCreateRequestBuilder, TransactionContext, String>
		implements SnomedTransactionalRequestBuilder<String> {

	private String id;
	private Boolean active = Boolean.TRUE;
	private String moduleId;
	private String refsetId;
	private String referencedComponentId;
	private Map<String, Object> properties = Collections.emptyMap();
	
	SnomedRefSetMemberCreateRequestBuilder() {
	}
	
	public SnomedRefSetMemberCreateRequestBuilder setId(String id) {
		this.id = id;
		return getSelf();
	}
	
	public SnomedRefSetMemberCreateRequestBuilder setReferencedComponentId(String referencedComponentId) {
		this.referencedComponentId = referencedComponentId;
		return getSelf();
	}
	
	public SnomedRefSetMemberCreateRequestBuilder setActive(Boolean active) {
		this.active = active;
		return getSelf();
	}
	
	public SnomedRefSetMemberCreateRequestBuilder setModuleId(String moduleId) {
		this.moduleId = moduleId;
		return getSelf();
	}
	
	public SnomedRefSetMemberCreateRequestBuilder setRefsetId(String refsetId) {
		this.refsetId = refsetId;
		return getSelf();
	}
	
	public SnomedRefSetMemberCreateRequestBuilder setProperties(Map<String, Object> properties) {
		this.properties = properties;
		return getSelf();
	}
	
	public SnomedRefSetMemberCreateRequestBuilder setSource(Map<String, Object> source) {
		setModuleId((String) source.get(SnomedRf2Headers.FIELD_MODULE_ID));
		setReferencedComponentId((String) source.get(SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID));
		setRefsetId((String) source.get(SnomedRf2Headers.FIELD_REFSET_ID));
		setProperties(source);
		return getSelf();
	}
	
	@Override
	public Request<TransactionContext, String> doBuild() {
		final SnomedRefSetMemberCreateRequest request = new SnomedRefSetMemberCreateRequest();
		request.setId(id);
		request.setActive(active);
		request.setModuleId(moduleId);
		request.setReferencedComponentId(referencedComponentId);
		request.setRefsetId(refsetId);
		request.setProperties(properties);
		return request;
	}

	public Request<TransactionContext, Boolean> buildNoContent() {
		return Requests.noContent(build());
	}
	
}
