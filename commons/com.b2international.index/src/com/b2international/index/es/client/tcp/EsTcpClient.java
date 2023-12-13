/*
 * Copyright 2018-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.index.es.client.tcp;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkProcessor.Builder;
import org.elasticsearch.action.bulk.BulkProcessor.Listener;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.*;
import org.elasticsearch.script.Script;

import com.b2international.index.IndexException;
import com.b2international.index.es.client.ClusterClient;
import com.b2international.index.es.client.EsClientBase;
import com.b2international.index.es.client.IndicesClient;

/**
 * @since 6.11
 * @deprecated
 */
public final class EsTcpClient extends EsClientBase {

	private Client client;
	private IndicesClient indicesClient;
	private ClusterClient clusterClient;

	public EsTcpClient(Client client) {
		super(client instanceof TransportClient ? ((TransportClient) client).transportAddresses().stream().findFirst().get().address().toString() : "localhost:9300");
		this.client = client;
		this.indicesClient = new IndicesTcpClient(client.admin().indices());
		this.clusterClient = new ClusterTcpClient(client.admin().cluster());
	}
	
	@Override
	public String version() throws IOException {
		// fake version to mimic 7.x behavior, TCP support has been removed in 8.x of ES, no need to ask the connected ES what version it has
		// replace this with actual logic if we need an actual 7.x ES version for anything
		return "7.x";
	}
	
	@Override
	protected boolean ping() throws IOException {
		return true; // always returns true
	}
	
	@Override
	public void close() throws Exception {
		client.close();
	}

	@Override
	public IndicesClient indices() {
		return indicesClient;
	}

	@Override
	public ClusterClient cluster() {
		return clusterClient;
	}

	@Override
	public GetResponse get(GetRequest req) throws IOException {
		return execute(client.get(req));
	}

	@Override
	public SearchResponse search(SearchRequest req) throws IOException {
		return execute(client.search(req));
	}
	
	@Override
	public UpdateResponse update(UpdateRequest req) throws IOException {
		return execute(client.update(req));
	}

	@Override
	public Builder bulk(Listener listener) {
		return BulkProcessor.builder(client::bulk, listener);
	}

	@Override
	public BulkByScrollResponse updateByQuery(String index, int batchSize, Script script, QueryBuilder query) throws IOException {
		UpdateByQueryRequestBuilder ubqrb = new UpdateByQueryRequestBuilder(client, UpdateByQueryAction.INSTANCE);
		
		ubqrb.source()
			.setIndices(index)
			.setSize(batchSize)
			.setQuery(query);
		
		return ubqrb
			.script(script)
			.setSlices(UpdateByQueryRequest.AUTO_SLICES)
			.get();
	}
	
	@Override
	public BulkByScrollResponse deleteByQuery(String index, int batchSize, QueryBuilder query) throws IOException {
		DeleteByQueryRequestBuilder dbqrb = new DeleteByQueryRequestBuilder(client, DeleteByQueryAction.INSTANCE);
		
		dbqrb.source()
			.setIndices(index)
			.setSize(batchSize)
			.setQuery(query);
	
		return dbqrb
			.setSlices(DeleteByQueryRequest.AUTO_SLICES)
			.get();
	}
	
	@Override
	public BulkByScrollResponse reindex(String sourceIndex, String destinationIndex, RemoteInfo remoteInfo, boolean refresh, int batchSize) throws IOException {
		
		ReindexRequestBuilder rirb = new ReindexRequestBuilder(client, ReindexAction.INSTANCE);
		
		rirb.source().setSize(batchSize);
		
		return rirb
			.source(sourceIndex)
			.destination(destinationIndex)
			.setRemoteInfo(remoteInfo)
			.refresh(refresh)
			.abortOnVersionConflict(false)
			.get();
		
	}
	
	static final <T> T execute(ActionFuture<T> future) throws IOException {
		try {
			return future.get();
		} catch (ExecutionException e) {
			if (e.getCause() instanceof IOException) {
				throw (IOException) e.getCause();
			} else {
				throw new IOException(e.getCause());
			}
		} catch (InterruptedException e) {
			throw new IndexException("Interrupted execution of Elasticsearch request", e);
		}
	}

}
