/*
 * Copyright 2021-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.request;

import java.util.Map;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.b2international.snowowl.core.context.ResourceRepositoryTransactionRequestBuilder;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 8.0
 */
public abstract class BaseResourceUpdateRequestBuilder<RB extends BaseResourceUpdateRequestBuilder<RB, R>, R extends BaseResourceUpdateRequest>
		extends BaseRequestBuilder<RB, TransactionContext, Boolean>
		implements ResourceRepositoryTransactionRequestBuilder<Boolean> {

	private final String resourceId;

	private String url;
	private String title;
	private String language;
	private String description;
	private String status;
	private String copyright;
	private String owner;
	private String contact;
	private String usage;
	private String purpose;
	private String bundleId;
	private Map<String, Object> settings;

	protected BaseResourceUpdateRequestBuilder(final String resourceId) {
		super();
		this.resourceId = resourceId;
	}
	
	protected final String getResourceId() {
		return resourceId;
	}

	public final RB setUrl(String url) {
		this.url = url;
		return getSelf();
	}

	public final RB setTitle(String title) {
		this.title = title;
		return getSelf();
	}

	public final RB setLanguage(String language) {
		this.language = language;
		return getSelf();
	}

	public final RB setDescription(String description) {
		this.description = description;
		return getSelf();
	}

	public final RB setStatus(String status) {
		this.status = status;
		return getSelf();
	}

	public final RB setCopyright(String copyright) {
		this.copyright = copyright;
		return getSelf();
	}

	public final RB setOwner(String owner) {
		this.owner = owner;
		return getSelf();
	}

	public final RB setContact(String contact) {
		this.contact = contact;
		return getSelf();
	}

	public final RB setUsage(String usage) {
		this.usage = usage;
		return getSelf();
	}

	public final RB setPurpose(String purpose) {
		this.purpose = purpose;
		return getSelf();
	}

	public final RB setBundleId(String bundleId) {
		this.bundleId = bundleId;
		return getSelf();
	}
	
	public final RB setSettings(Map<String, Object> settings) {
		this.settings = settings;
		return getSelf();
	}

	public abstract R createResourceRequest();

	@Override
	protected final Request<TransactionContext, Boolean> doBuild() {
		final R req = createResourceRequest();
		init(req);
		return req;
	}

	@OverridingMethodsMustInvokeSuper
	protected void init(R req) {
		req.setUrl(url);
		req.setTitle(title);
		req.setLanguage(language);
		req.setDescription(description);
		req.setStatus(status);
		req.setCopyright(copyright);
		req.setOwner(owner);
		req.setContact(contact);
		req.setUsage(usage);
		req.setPurpose(purpose);
		req.setBundleId(bundleId);
		req.setSettings(settings);
	}
}
