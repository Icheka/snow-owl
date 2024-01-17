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
package com.b2international.snowowl.core.rest;

import java.util.List;

import com.b2international.snowowl.core.Dependency;

/**
 * @since 8.12.0
 */
public abstract class BaseTerminologyResourceRestCreate extends BaseResourceRestCreate {

	private String oid;
	private String branchPath;
	private List<Dependency> dependencies;
	
	public void setOid(String oid) {
		this.oid = oid;
	}
	
	public String getOid() {
		return oid;
	}
	
	public void setBranchPath(String branchPath) {
		this.branchPath = branchPath;
	}
	
	public String getBranchPath() {
		return branchPath;
	}
	
	public List<Dependency> getDependencies() {
		return dependencies;
	}
	
	public void setDependencies(List<Dependency> dependencies) {
		this.dependencies = dependencies;
	}
	
}
