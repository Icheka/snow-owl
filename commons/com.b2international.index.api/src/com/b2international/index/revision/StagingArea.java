/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * A place that stores information about what will go into your next commit.
 * 
 * @since 6.5
 * @see RevisionIndex#prepareCommit()
 */
public final class StagingArea {

	private final RevisionIndex index;

	private final Map<Long, Revision> newRevisions = newHashMap();
	private final Multimap<Class<? extends Revision>, Long> removedRevisions = HashMultimap.create();
	
	StagingArea(RevisionIndex index) {
		this.index = index;
	}
	
	/**
	 * Commits the changes so far staged to the staging area. 
	 * @param commitId
	 * @param branchPath
	 * @param timestamp
	 * @param userId
	 * @param commitComment
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public Commit commit(String commitId, String branchPath, long timestamp, String userId, String commitComment) {
		return index.write(branchPath, timestamp, writer -> {
			Commit commit = Commit.builder()
				.id(commitId)
				.userId(userId)
				.branch(branchPath)
				.comment(commitComment)
				.timestamp(timestamp)
				.build();
			
			// apply removals first
			for (Class<? extends Revision> type : removedRevisions.keySet()) {
				writer.remove(type, removedRevisions.get(type));
			}

			// then new revisions
			for (Entry<Long, Revision> doc : newRevisions.entrySet()) {
				if (!removedRevisions.containsValue(doc.getKey())) {
					writer.put(doc.getKey(), doc.getValue());
				}
			}
			
			writer.writer().put(commit.getId(), commit);
			writer.commit();
			return commit;
		});
	}

	public StagingArea stageNew(long storageKey, Revision newRevision) {
		newRevisions.put(storageKey, newRevision);
		return this;
	}

	public StagingArea stageRemove(Class<? extends Revision> type, long storageKey) {
		removedRevisions.put(type, storageKey);
		return this;
	}
	
}
