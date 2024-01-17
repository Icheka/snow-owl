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
package com.b2international.index.admin;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.RemoteInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.index.IndexClientFactory;
import com.b2international.index.es.admin.IndexMapping;
import com.b2international.index.es.client.EsClient;
import com.b2international.index.es.reindex.ReindexResult;
import com.b2international.index.es8.Es8Client;
import com.b2international.index.mapping.Mappings;

/**
 * Administration interface managing an elasticsearch index.
 * 
 * @since 4.7
 */
public interface IndexAdmin {

	/**
	 * Returns the {@link Logger} assigned to this index.
	 * 
	 * @return
	 */
	Logger log();

	/**
	 * Returns <code>true</code> if the index already exists, otherwise returns <code>false</code>.
	 */
	boolean exists();

	/**
	 * Creates the index if and only if does not exist yet, otherwise this method is no-op. Configure the defined {@link #documentMappings()} as well.
	 */
	void create();

	/**
	 * Deletes the entire index with its data if and only if does exist, otherwise this method is no-op.
	 */
	void delete();

	/**
	 * Clears all associated data from the index for the specified types.
	 * 
	 * @param types
	 *            - the types to remove completely from the index
	 */
	void clear(Collection<Class<?>> types);

	/**
	 * Returns the settings of this index.
	 * 
	 * @return
	 */
	Map<String, Object> settings();
	
	/**
	 * Updates the dynamic settings of the underlying indices. NOTE: this currently supports only a few parameters, like the max_result_window.
	 * 
	 * @param newSettings
	 */
	void updateSettings(Map<String, Object> newSettings);

	/**
	 * Updates the mappings available for the underlying indices to work with.
	 * 
	 * NOTE: keep in mind that these won't affect existing indices and operations on this class might not run on all previously created indices. 
	 */
	void updateMappings(Mappings mappings);

	/**
	 * Returns the index name prefix which will be used to identify all indexes that managed by this {@link IndexAdmin}.
	 * 
	 * @return
	 */
	String name();
	
	/**
	 * @return the {@link IndexMapping} instance that contains information about created and managed Elasticsearch indices.
	 */
	IndexMapping getIndexMapping();

	/**
	 * Optimizes the underlying index until it has less than or equal segments than the supplied maxSegments number.
	 * 
	 * @param maxSegments
	 *            - max number of segments to force on the index
	 */
	void optimize(int maxSegments);
	
	/**
	 * @return the Elasticsearch client used by this {@link IndexAdmin}.
	 */
	EsClient client();
	
	/**
	 * NOTE: depending on configuration, this client might not be available.
	 * 
	 * @return the Elasticsearch high-level client that supports all Elasticsearch 8 features
	 * @throws UnsupportedOperationException - if es8Client is not available
	 */
	Es8Client es8Client() throws UnsupportedOperationException;
	
	/**
	 * Issue a refresh on the specified indices
	 * @return {@link RefreshResponse}
	 * @param indices
	 */
	RefreshResponse refresh(String...indices);

	/**
	 * Issue a reindex operation of sourceIndex to destinationIndex.
	 * 
	 * An optional remoteInfo object can be used to:
	 * 	- add a selective document query in a serialized form
	 *  - specify a remote Elasticsearch instance to move the contents of sourceIndex to the remote destinationIndex 
	 *
	 * @return {@link BulkByScrollResponse}
	 * @param sourceIndex - the source index
	 * @param destinationIndex - the destination index
	 * @param remoteInfo - configuration for the remote Elasticsearch instance (scheme, host, port, credentials and query)
	 * @param refresh - whether to refresh the destination index at the end of the process or not
	 * @param batchSize - default batch size is {@link IndexClientFactory#DEFAULT_REINDEX_BATCH_SIZE}
	 */
	ReindexResult reindex(String sourceIndex, String destinationIndex, RemoteInfo remoteInfo, boolean refresh, int batchSize) throws IOException;
	
	/**
	 * @return the indices maintained by this {@link IndexAdmin}
	 */
	default String[] indices() {
		return getIndexMapping().indices();
	}
	
	static Logger createIndexLogger(String name) {
		return LoggerFactory.getLogger(String.join(".", "index", name));
	}

}
