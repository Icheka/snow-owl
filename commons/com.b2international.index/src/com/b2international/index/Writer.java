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
package com.b2international.index;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.b2international.index.mapping.DocumentMapping;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @since 4.7
 */
public interface Writer {

	void put(Object object);
	
	void put(DocumentMapping mapping, String docId, ObjectNode source);
	
	<T> void putAll(Collection<T> objects);

	<T> void bulkUpdate(BulkUpdate<T> update);
	
	<T> void bulkDelete(BulkDelete<T> delete);
	
	// XXX: Unlike other write operations, updates take effect at once and do not get queued
	<T> T updateImmediately(Update<T> update);
	
	void remove(Class<?> type, String keyToRemove);
	
	void remove(Class<?> type, Set<String> keysToRemove);
	
	void removeAll(Map<Class<?>, Set<String>> keysToRemoveByType);

	void commit() throws IOException;

	Searcher searcher();

	boolean isEmpty();
}
