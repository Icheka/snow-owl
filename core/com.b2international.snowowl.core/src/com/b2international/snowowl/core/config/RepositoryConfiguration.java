/*
 * Copyright 2011-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import jakarta.validation.constraints.NotEmpty;

import com.b2international.snowowl.core.domain.PagingSettingsProvider;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.net.HostAndPort;

/**
 * Repository configuration is the central place where database connection
 * parameters, repository settings can be configured and retrieved.
 * 
 * @since 3.4
 */
public class RepositoryConfiguration implements PagingSettingsProvider {
	
	@NotEmpty
	private String host = "0.0.0.0";

	@Min(0)
	@Max(65535)
	private int port = 2036;

	@NotNull
	private String certificateChainPath = "";
	
	@NotNull
	private String privateKeyPath = "";

	@NotNull
	private IndexConfiguration indexConfiguration = new IndexConfiguration();
	
	@Min(1)
	private int maxThreads = 200;
	
	@Min(10)
	@Max(1000)
	private int mergeMaxResults = 100;

	@Pattern(regexp = "^[a-zA-Z0-9_-]{0,32}$")
	private String deploymentId = "";
	
	/**
	 * @return the host
	 */
	@JsonProperty
	public String getHost() {
		return host;
	}

	/**
	 * @return the port
	 */
	@JsonProperty
	public int getPort() {
		return port;
	}

	/**
	 * @param host
	 *            the host to set
	 */
	@JsonProperty
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	@JsonProperty
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Returns the currently set {@link HostAndPort}.
	 * 
	 * @return
	 */
	public HostAndPort getHostAndPort() {
		return HostAndPort.fromParts(getHost(), getPort());
	}
	
	@JsonProperty
	public String getCertificateChainPath() {
		return certificateChainPath;
	}
	
	@JsonProperty
	public void setCertificateChainPath(String certificateChainPath) {
		this.certificateChainPath = certificateChainPath;
	}
	
	@JsonProperty
	public String getPrivateKeyPath() {
		return privateKeyPath;
	}
	
	@JsonProperty
	public void setPrivateKeyPath(String privateKeyPath) {
		this.privateKeyPath = privateKeyPath;
	}
	
	/**
	 * @return number of maximum threads to allow in the underlying event bus instance
	 */
	@JsonProperty
	public int getMaxThreads() {
		return maxThreads;
	}
	
	/**
	 * @param maxThreads - the maximum number of threads to allow in the underlying event bus instance
	 */
	@JsonProperty
	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}
	
	@JsonProperty("index")
	public IndexConfiguration getIndexConfiguration() {
		return indexConfiguration;
	}
	
	@JsonProperty("index")
	public void setIndexConfiguration(IndexConfiguration indexConfiguration) {
		this.indexConfiguration = indexConfiguration;
	}

	/**
	 * @return the maximum number of completed merge job results to keep
	 */
	@JsonProperty
	public int getMergeMaxResults() {
		return mergeMaxResults;
	}
	
	@JsonProperty
	public void setMergeMaxResults(int mergeMaxResults) {
		this.mergeMaxResults = mergeMaxResults;
	}
	
	@JsonProperty
	public String getDeploymentId() {
		return deploymentId;
	}
	
	@JsonProperty
	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	@JsonIgnore
	@Override
	public int getPageSize() {
		return indexConfiguration.getPageSize();
	}

	@JsonIgnore
	@Override
	public int getTermPartitionSize() {
		return indexConfiguration.getTermPartitionSize();
	}

	@JsonIgnore
	@Override
	public int getCommitLimit() {
		return indexConfiguration.getCommitLimit();
	}
}
