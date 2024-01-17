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
package com.b2international.snowowl.core.bundle;

import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.Resources;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.internal.ResourceDocument.Builder;
import com.b2international.snowowl.core.request.ResourceRequests;

/**
 * @since 8.0
 */
public final class Bundle extends Resource {

	private static final long serialVersionUID = 1L;

	public static final String RESOURCE_TYPE = "bundles";
	
	/**
	 * @since 8.0
	 */
	public static final class Expand {
		public static final String RESOURCES = "resources";
	}
	
	// Expandable content
	private Resources resources;
	
	public Resources getResources() {
		return resources;
	}
	
	public void setResources(Resources resources) {
		this.resources = resources;
	}
	
	@Override
	public String getResourceType() {
		return RESOURCE_TYPE;
	}

	public BundleCreateRequestBuilder toCreateRequest() {
		return ResourceRequests.bundles().prepareCreate()
				.setId(getId())
				.setUrl(getUrl())
				.setTitle(getTitle())
				.setLanguage(getLanguage())
				.setDescription(getDescription())
				.setStatus(getStatus())
				.setCopyright(getCopyright())
				.setOwner(getOwner())
				.setContact(getContact())
				.setUsage(getUsage())
				.setPurpose(getPurpose())
				.setBundleId(getBundleId())
				.setSettings(getSettings());
	}
	
	public BundleUpdateRequestBuilder toUpdateRequest() {
		return ResourceRequests.bundles().prepareUpdate(getId())
				.setUrl(getUrl())
				.setTitle(getTitle())
				.setLanguage(getLanguage())
				.setDescription(getDescription())
				.setStatus(getStatus())
				.setCopyright(getCopyright())
				.setOwner(getOwner())
				.setContact(getContact())
				.setUsage(getUsage())
				.setPurpose(getPurpose())
				.setBundleId(getBundleId())
				.setSettings(getSettings());
	}

	@Override
	public Builder toDocumentBuilder() {
		return ResourceDocument.builder()
				.resourceType(getResourceType())
				.id(getId())
				.url(getUrl())
				.title(getTitle())
				.language(getLanguage())
				.description(getDescription())
				.status(getStatus())
				.copyright(getCopyright())
				.owner(getOwner())
				.contact(getContact())
				.usage(getUsage())
				.purpose(getPurpose())
				// by default resources converted from domain representations are not hidden
				.hidden(false)
				.bundleAncestorIds(getBundleAncestorIds())
				.bundleId(getBundleId())
				.settings(getSettings());
	}

	public static ResourceURI uri(String bundleUri) {
		return bundleUri.startsWith(RESOURCE_TYPE + Branch.SEPARATOR) ? new ResourceURI(bundleUri) : ResourceURI.of(RESOURCE_TYPE, bundleUri);
	}
	
	public static ResourceURI uri(String bundleUri, String versionId) {
		return uri(bundleUri).withPath(versionId);
	}
	
	public static Bundle from(ResourceDocument doc) {
		final Bundle bundle = new Bundle();
		bundle.setId(doc.getId());
		bundle.setUrl(doc.getUrl());
		bundle.setTitle(doc.getTitle());
		bundle.setLanguage(doc.getLanguage());
		bundle.setDescription(doc.getDescription());
		bundle.setStatus(doc.getStatus());
		bundle.setCopyright(doc.getCopyright());
		bundle.setOwner(doc.getOwner());
		bundle.setContact(doc.getContact());
		bundle.setUsage(doc.getUsage());
		bundle.setPurpose(doc.getPurpose());
		bundle.setBundleAncestorIds(doc.getBundleAncestorIds());
		bundle.setBundleId(doc.getBundleId());
		bundle.setCreatedAt(doc.getCreatedAt());
		bundle.setUpdatedAt(doc.getUpdatedAt());
		bundle.setSettings(doc.getSettings());
		return bundle;
	}
}
