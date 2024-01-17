/*
 * Copyright 2011-2023 B2i Healthcare, https://b2ihealthcare.com
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Maps.newHashMap;

import java.io.IOException;
import java.util.*;

import com.b2international.index.BulkDelete;
import com.b2international.index.BulkUpdate;
import com.b2international.index.Update;
import com.b2international.index.Writer;
import com.b2international.index.es.EsDocumentSearcher;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @since 4.7
 */
public class DefaultRevisionWriter implements RevisionWriter {

	private final RevisionBranchRef branch;
	
	private final Writer index;
	private final RevisionSearcher searcher;
	
	private final BaseRevisionBranching branching;
	private final RevisionBranchPoint created;
	private final RevisionBranchPoint revised;
	
	private final Map<Class<?>, Set<String>> revisionUpdates = newHashMap();

	public DefaultRevisionWriter(
			final BaseRevisionBranching branching,
			final RevisionBranchRef branch,
			long commitTimestamp,
			Writer index, 
			RevisionSearcher searcher) {
		this.branching = branching;
		this.branch = branch;
		this.index = index;
		this.searcher = searcher;
		this.created = new RevisionBranchPoint(branch.branchId(), commitTimestamp);
		this.revised = new RevisionBranchPoint(branch.branchId(), Long.MAX_VALUE);
	}

	@Override
	public void put(Object object) {
		if (object instanceof Revision) {
			Revision rev = (Revision) object;
			final Class<? extends Revision> type = rev.getClass();
			if (!revisionUpdates.containsKey(type)) {
				revisionUpdates.put(type, Sets.<String>newHashSet());
			}
			final Collection<String> revisionsToUpdate = revisionUpdates.get(type);
			// prevent duplicated revisions
			final String key = rev.getId();
			checkArgument(!revisionsToUpdate.contains(key), "duplicate revision %s", key);
			revisionsToUpdate.add(key);
			
			// set created time to the current commit timestamp
			rev.setCreated(created);
		}
		// register object for commit
		index.put(object);
	}
	
	@Override
	public void put(DocumentMapping mapping, String docId, ObjectNode source) {
		index.put(mapping, docId, source);
	}

	@Override
	public <T> void putAll(Collection<T> objects) {
		objects.forEach(this::put);
	}
	
	@Override
	public <T> void bulkUpdate(BulkUpdate<T> update) {
		index.bulkUpdate(update);
	}
	
	@Override
	public <T> void bulkDelete(BulkDelete<T> delete) {
		index.bulkDelete(delete);
	}
	
	@Override
	public <T> T updateImmediately(Update<T> update) {
		return index.updateImmediately(update);
	}

	@Override
	public void remove(Class<?> type, String key) {
		remove(type, Collections.singleton(key));
	}
	
	@Override
	public void remove(Class<?> type, Set<String> keysToRemove) {
		removeAll(ImmutableMap.of(type, keysToRemove));
	}
	
	@Override
	public void removeAll(Map<Class<?>, Set<String>> keysByType) {
		final String oldRevised = revised.toIpAddress();
		final String newRevised = created.toIpAddress();
		for (Class<?> type : keysByType.keySet()) {
			setRevised(type, keysByType.get(type), oldRevised, newRevised, branch);
		}
	}

	@Override
	public void setRevised(Class<?> type, Set<String> keysToUpdate, RevisionBranchRef branchToUpdate) {
		final String oldRevised = new RevisionBranchPoint(branch.branchId(), Long.MAX_VALUE).toIpAddress();
		final String newRevised = created.toIpAddress();
		setRevised(type, keysToUpdate, oldRevised, newRevised, branchToUpdate);
	}
	
	private void setRevised(Class<?> type, Set<String> keysToUpdate, final String oldRevised, final String newRevised, RevisionBranchRef branchToUpdate) {
		if (Revision.class.isAssignableFrom(type)) {
			if (!keysToUpdate.isEmpty()) {
				final Map<String, Object> updateRevised = ImmutableMap.of("oldRevised", oldRevised, "newRevised", newRevised);
				for (List<String> keys : Lists.partition(List.copyOf(keysToUpdate), ((EsDocumentSearcher) index.searcher()).maxTermsCount())) {
					final Expression filter = Expressions.bool()
							.filter(Expressions.matchAny(Revision.Fields.ID, keys))
							.filter(branchToUpdate.toRevisionFilter())
							.build();
					final BulkUpdate<Revision> update = new BulkUpdate<Revision>((Class<? extends Revision>) type, filter, Revision.UPDATE_REVISED, updateRevised);
					index.bulkUpdate(update);
				}
			}
		} else {
			index.remove(type, keysToUpdate);
		}
	}

	@Override
	public void commit() throws IOException {
		// before commit, mark all previous revisions as replaced
		removeAll(revisionUpdates);
		index.commit();
		branching.sendChangeEvent(branch());
	}

	@Override
	public String branch() {
		return branch.path();
	}
	
	@Override
	public RevisionSearcher searcher() {
		return searcher;
	}
	
	@Override
	public boolean isEmpty() {
		return index.isEmpty();
	}
	
}
