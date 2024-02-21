/*
 * Copyright 2017-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.branch.compare;

import java.util.Set;

import jakarta.validation.constraints.Min;

import jakarta.validation.constraints.NotEmpty;

import com.b2international.index.revision.ObjectId;
import com.b2international.index.revision.RevisionCompare;
import com.b2international.index.revision.RevisionCompareDetail;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.authorization.AccessControl;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

/**
 * @since 5.9
 */
final class BranchCompareRequest implements Request<RepositoryContext, BranchCompareResult>, AccessControl {

	private static final long serialVersionUID = 1L;

	@JsonProperty
	private String base;
	
	@NotEmpty
	@JsonProperty
	private String compare;
	
	@Min(0)
	@JsonProperty
	private int limit;
	
	@JsonProperty
	private boolean excludeComponentChanges;
	
	BranchCompareRequest() {
	}
	
	void setBaseBranch(String baseBranch) {
		this.base = baseBranch;
	}
	
	void setCompareBranch(String compareBranch) {
		this.compare = compareBranch;
	}
	
	void setLimit(int limit) {
		this.limit = limit;
	}
	
	void setExcludeComponentChanges(boolean excludeComponentChanges) {
		this.excludeComponentChanges = excludeComponentChanges;
	}
	
	@Override
	public BranchCompareResult execute(RepositoryContext context) {
		final RevisionIndex index = context.service(RevisionIndex.class);
		final Branch branchToCompare = RepositoryRequests.branching().prepareGet(compare).build().execute(context);
		final long compareHeadTimestamp = branchToCompare.headTimestamp();
		
		final RevisionCompare compareResult;
		final String baseBranchPath;
		if (base != null) {
			compareResult = index.compare(base, compare, limit, excludeComponentChanges);
			baseBranchPath = base;
		} else {
			compareResult = index.compare(compare, limit, excludeComponentChanges);
			baseBranchPath = branchToCompare.parentPath();
		}
		
		final BranchCompareResult.Builder result = BranchCompareResult.builder(baseBranchPath, compare, compareHeadTimestamp);
		
		final Set<ComponentIdentifier> changedContainers = Sets.newHashSet(); 
		for (RevisionCompareDetail detail : compareResult.getDetails()) {
			final ObjectId affectedId;
			if (detail.isComponentChange()) {
				affectedId = detail.getComponent();
				if (!detail.getObject().isRoot() && !excludeComponentChanges) {
					changedContainers.add(ComponentIdentifier.of(detail.getObject().type(), detail.getObject().id()));
				}
			} else {
				affectedId = detail.getObject();
			}
			final ComponentIdentifier identifier = ComponentIdentifier.of(affectedId.type(), affectedId.id());
			
			switch (detail.getOp()) {
			case ADD:
				result.putNewComponent(identifier);
				break;
			case CHANGE:
				result.putChangedComponent(identifier);			
				break;
			case REMOVE:
				result.putDeletedComponent(identifier);
				break;
			}
		}
		
		return result
				.totalNew(compareResult.getTotalAdded())
				.totalChanged(compareResult.getTotalChanged())
				.totalDeleted(compareResult.getTotalRemoved())
				.build(changedContainers);
	}
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_BROWSE;
	}
	
}
