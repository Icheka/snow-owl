/*
 * Copyright 2021-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request.resource;

import java.util.*;
import java.util.stream.Collectors;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.Dependency;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.internal.DependencyDocument;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.internal.ResourceDocument.Builder;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.request.BaseResourceUpdateRequest;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.version.Version;

/**
 * @since 8.0
 */
public abstract class BaseTerminologyResourceUpdateRequest extends BaseResourceUpdateRequest {

	private static final long serialVersionUID = 2L;
	
	// generic terminology resource update properties
	private String oid;
	private String branchPath;
	
	@Deprecated
	private ResourceURI extensionOf;
	
	private List<Dependency> dependencies;
	
//	private String iconPath; // TODO should we support custom icons for resources?? branding??
	
	// runtime fields
	private transient List<Dependency> mergedDependencies;
	
	public final void setOid(String oid) {
		this.oid = oid;
	}
	
	public final void setBranchPath(String branchPath) {
		this.branchPath = branchPath;
	}
	
	public final void setDependencies(List<Dependency> dependencies) {
		this.dependencies = dependencies;
	}
	
	/**
	 * @deprecated - replaced by {@link #setDependencies(List)}, will be removed in 9.0
	 * @param extensionOf
	 */
	public final void setExtensionOf(ResourceURI extensionOf) {
		this.extensionOf = extensionOf;
	}
	
//	public final void setIconPath(final String iconPath) {
//		this.iconPath = iconPath;
//	}
	
	public BaseTerminologyResourceUpdateRequest(String componentId) {
		super(componentId);
	}

	@Override
	protected boolean updateSpecializedProperties(TransactionContext context, ResourceDocument resource, Builder updated) {
		boolean changed = false;
		
		changed |= updateOid(context, resource.getOid(), updated);
		changed |= updateBranchPath(context, updated, resource.getBranchPath(), resource.getToolingId());
		changed |= updateDependencies(context, updated, resource.getDependencies(), resource.getId());
		
//		changed |= updateProperty(iconPath, codeSystem::getIconPath, updated::iconPath);
		return changed;
	}
	
	private boolean updateDependencies(TransactionContext context, Builder resource, SortedSet<DependencyDocument> oldDependencies, String resourceId) {
		// throw error if using both the old and the new way of attaching dependencies to a resource
		if (extensionOf != null) {
			if (!CompareUtils.isEmpty(dependencies)) {
				throw new BadRequestException("Using both the deprecated 'extensionOf' parameter along with the new dependencies array is not supported. Stick to the old format or migrate to the new.");
			}

			// make sure we merge dependencies into a single mergedDependencies List, detect duplicates and old API usage
			final List<Dependency> mergedDependencies = new ArrayList<>();
			
			if (extensionOf != null) {
				mergedDependencies.add(Dependency.of(extensionOf, TerminologyResource.DependencyScope.EXTENSION_OF));
			}
			
			this.mergedDependencies = mergedDependencies; 
		} else {
			this.mergedDependencies = dependencies;
		}
		
		// TODO validate valid reference to dependency resourceUri
		
		// handle extensionOf dependency if configured
		Optional<Dependency> extensionOfDependency = mergedDependencies != null ? mergedDependencies.stream().filter(Dependency::isExtensionOf).findFirst() : Optional.empty();
		var extensionOfUri = extensionOfDependency.map(Dependency::getResourceUri).orElse(null);
		
		
		if (extensionOfUri != null) {
			if (extensionOfUri.isHead() || extensionOfUri.isLatest()) {
				throw new BadRequestException("Base terminology resource version was not expicitly given (can not be empty, "
					+ "LATEST or HEAD) in 'extensionOf' dependency %s.", extensionOfUri);
			}
			
			final String versionId = extensionOfUri.getResourceUri().getPath();
			
			final Optional<Version> extensionOfVersion = ResourceRequests.prepareSearchVersion()
					.one()
					.filterByResource(extensionOfUri.getResourceUri().withoutPath())
					.filterByVersionId(versionId)
					.build()
					.execute(context)
					.first();
			
			if (!extensionOfVersion.isPresent()) {
				throw new BadRequestException("Couldn't find base terminology resource version for 'extensionOf' dependency %s.", extensionOfUri);
			}
			
			// The working branch prefix is determined by the extensionOf code system version's path
			final String newResourceBranchPath = Branch.get(extensionOfVersion.get().getBranchPath(), resourceId);
			
			if (branchPath != null && !branchPath.equals(newResourceBranchPath)) {
				throw new BadRequestException("Branch path is inconsistent with 'extensionOf' dependency ('%s' given, should be '%s').", branchPath, newResourceBranchPath);
			}
			
			resource.branchPath(newResourceBranchPath);
			
			// XXX no need to return here, we are only able to get here by having at least one extensionOf entry in the dependencies array, and that will be handled in the subsequent if block
		}
		
		final SortedSet<DependencyDocument> newDependencies = mergedDependencies != null ? mergedDependencies.stream().map(Dependency::toDocument).collect(Collectors.toCollection(TreeSet::new)) : null;
		if (mergedDependencies != null && !Objects.equals(newDependencies, oldDependencies)) {
			// check duplicates in new dependency array
			BaseTerminologyResourceCreateRequest.checkDuplicateDependencies(context, mergedDependencies);
			// verify references to new dependency array
			BaseTerminologyResourceCreateRequest.checkNonExtensionOfDependencyReferences(context, mergedDependencies);
			
			// make sure we auto-migrate the old fields to the new value
			resource.extensionOf(null);
			resource.upgradeOf(null);
			resource.dependencies(newDependencies);
			
			return true;
		}
		
		return false;
	}

	private boolean updateOid(TransactionContext context, String oldOid, Builder updated) {
		if (oid == null || oid.equals(oldOid)) {
			return false;
		}
		
		if (!oid.isBlank()) {
			final boolean oidExist = ResourceRequests.prepareSearch()
					.setLimit(0)
					.filterByOid(oid)
					.build()
					.execute(context)
					.getTotal() > 0;
			
			if (oidExist) {
				throw new AlreadyExistsException("Resource", "oid", oid);
			}
		}
		
		updated.oid(oid);
		return true;
	}

	private boolean updateBranchPath(final TransactionContext context, 
			final ResourceDocument.Builder resource, 
			final String currentBranchPath,
			final String toolingId) {
		
		// if extensionOf is set, branch path changes are already handled in updateExtensionOf
		if (extensionOf == null && branchPath != null && !currentBranchPath.equals(branchPath)) {
			try {
				final Branch branch = RepositoryRequests
						.branching()
						.prepareGet(branchPath)
						.build(toolingId)
						.getRequest()
						.execute(context);
				
				if (branch.isDeleted()) {
					throw new BadRequestException("Branch with identifier '%s' is deleted.", branchPath);
				}
				
			} catch (NotFoundException e) {
				throw e.toBadRequestException();
			}
			

			// TODO: check if update branch path coincides with a version working path 
			// and update extensionOf accordingly?
			resource.extensionOf(null);
			resource.branchPath(branchPath);
			return true;
		}
		
		return false;
	}
}
