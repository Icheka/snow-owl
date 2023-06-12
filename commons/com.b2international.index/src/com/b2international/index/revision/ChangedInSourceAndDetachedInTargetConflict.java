/*
 * Copyright 2018-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.revision;

import java.util.List;
import java.util.Objects;

import com.b2international.commons.collections.Collections3;
import com.b2international.index.revision.StagingArea.RevisionPropertyDiff;

/**
 * @since 7.0
 */
public final class ChangedInSourceAndDetachedInTargetConflict extends Conflict {

	private final List<RevisionPropertyDiff> changes;

	public ChangedInSourceAndDetachedInTargetConflict(ObjectId objectId, List<RevisionPropertyDiff> changes) {
		super(objectId, "");
		this.changes = Collections3.toImmutableList(changes);
	}
	
	public List<RevisionPropertyDiff> getChanges() {
		return changes;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getObjectId(), getMessage(), getChanges());
	}
	
	@Override
	protected boolean doEquals(Conflict obj) {
		ChangedInSourceAndDetachedInTargetConflict other = (ChangedInSourceAndDetachedInTargetConflict) obj;
		return Objects.equals(changes, other.changes);
	}

}
