/*
 * Copyright 2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.info;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.index.es.client.EsClusterStatus;
import com.b2international.index.es.client.EsIndexStatus;
import com.b2international.snowowl.core.Repositories;
import com.b2international.snowowl.core.RepositoryInfo;
import com.b2international.snowowl.core.ServerInfo;
import com.b2international.snowowl.core.repository.JsonSupport;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 8.9.0
 */
public class ServerInfoSerializationTest {

	private static ServerInfo info;
	private static ObjectMapper mapper;
	private static String serializedResponse;

	@BeforeClass
	public static void before() {

		mapper = JsonSupport.getDefaultObjectMapper();

		info = new ServerInfo(
					"1.0.0",
					"description",
					new Repositories(
						List.of(
							RepositoryInfo.of(
								"id",
								RepositoryInfo.Health.GREEN,
								"diagnosis",
								List.of(
									new EsIndexStatus("index", ClusterHealthStatus.GREEN, "diagnosis"),
									new EsIndexStatus("index2", ClusterHealthStatus.RED, null)
								)
							)
						)
					),
					new EsClusterStatus(
						true,
						"clusterDiagnosis",
						List.of(new EsIndexStatus("index", ClusterHealthStatus.GREEN, "diagnosis"))
					)
				);

		serializedResponse = "{\"version\":\"1.0.0\",\"description\":\"description\",\"repositories\":{\"items\":[{\"id\":\"id\",\"health\":\"GREEN\",\"diagnosis\":\"diagnosis\",\"indices\":[{\"index\":\"index\",\"status\":\"GREEN\",\"diagnosis\":\"diagnosis\"},{\"index\":\"index2\",\"status\":\"RED\"}]}]},\"cluster\":{\"available\":true,\"diagnosis\":\"clusterDiagnosis\",\"indices\":[{\"index\":\"index\",\"status\":\"GREEN\",\"diagnosis\":\"diagnosis\"}]}}";

	}

	@Test
	public void testSerialization() throws Exception {
		final String result = mapper.writeValueAsString(info);
		assertEquals(serializedResponse, result);
	}

	@Test
	public void testDeserialization() throws Exception {
		final ServerInfo newInfo = mapper.readValue(serializedResponse, ServerInfo.class);
		assertEquals(info, newInfo);
	}

}
