/*
 * Copyright 2011-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.codesystem;

import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Captures metadata about a code system, which holds a set of concepts of medical significance (optionally with other, supporting components that
 * together make up the definition of concepts) and their corresponding unique code.
 * 
 * @since 1.0
 */
public final class CodeSystem extends TerminologyResource {
	
	private static final long serialVersionUID = 5L;
	
	public static final String RESOURCE_TYPE = "codesystems";
	
	/**
	 * @since 8.0
	 */
	public static final class CommonSettings {
		public static final String LOCALES = "locales";
	}

	@Override
	public String getResourceType() {
		return RESOURCE_TYPE;
	}
	
	/**
	 * @return the list of {@link String} formatted as {@link ExtendedLocale} representing the language content this code system carries (can be {@code null})
	 */
	@JsonIgnore
	public List<String> getLocales() {
		return getSettings() == null ? null : (List<String>) getSettings().get(CommonSettings.LOCALES);
	}
	
	public static ResourceURI uri(String codeSystemId) {
		return codeSystemId.startsWith(RESOURCE_TYPE) ? new ResourceURI(codeSystemId) : ResourceURI.of(RESOURCE_TYPE, codeSystemId);
	}
	
	public static ResourceURI uri(String codeSystemId, String path) {
		return uri(codeSystemId).withPath(path);
	}
	
	public static CodeSystem from(ResourceDocument doc) {
		CodeSystem codeSystem = new CodeSystem();
		codeSystem.setId(doc.getId());
		codeSystem.setUrl(doc.getUrl());
		codeSystem.setTitle(doc.getTitle());
		codeSystem.setLanguage(doc.getLanguage());
		codeSystem.setDescription(doc.getDescription());
		codeSystem.setStatus(doc.getStatus());
		codeSystem.setCopyright(doc.getCopyright());
		codeSystem.setOwner(doc.getOwner());
		codeSystem.setContact(doc.getContact());
		codeSystem.setUsage(doc.getUsage());
		codeSystem.setPurpose(doc.getPurpose());
		codeSystem.setBundleAncestorIds(doc.getBundleAncestorIds());
		codeSystem.setBundleId(doc.getBundleId());
		codeSystem.setCreatedAt(doc.getCreatedAt());
		codeSystem.setOid(doc.getOid());
		codeSystem.setBranchPath(doc.getBranchPath());
		codeSystem.setToolingId(doc.getToolingId());
		codeSystem.setExtensionOf(doc.getExtensionOf());
		codeSystem.setUpgradeOf(doc.getUpgradeOf());
		codeSystem.setSettings(doc.getSettings());
		return codeSystem;
	}

	public CodeSystemCreateRequestBuilder toCreateRequest() {
		return CodeSystemRequests.prepareNewCodeSystem()
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
				.setOid(getOid())
				.setBranchPath(getBranchPath())
				.setToolingId(getToolingId())
				.setExtensionOf(getExtensionOf())
				.setUpgradeOf(getUpgradeOf())
				.setSettings(getSettings());
	}
	
//	/**
//	 * Returns all code system short name dependencies and itself.
//	 */
//	@JsonIgnore
//	public SortedSet<String> getDependenciesAndSelf() {
//		ImmutableSortedSet.Builder<String> affectedCodeSystems = ImmutableSortedSet.naturalOrder();
//		affectedCodeSystems.addAll(getDependencies());
//		affectedCodeSystems.add(shortName);
//		return affectedCodeSystems.build();
//	}
	
//	/**
//	 * Returns the short names of all affected code systems
//	 */
//	@JsonIgnore
//	public SortedSet<String> getDependencies() {
//		return TerminologyRegistry.INSTANCE.getTerminology(terminologyId).getDependencies();
//	}
	
}
