/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core;

import java.io.Serializable;

/**
 * @since 8.0
 */
public abstract class Resource implements Serializable {

	private static final long serialVersionUID = 1L;

	// unique identifier for each resource, can be auto-generated or manually specified
	private String id;

	// URL, eg. http://snomed.info/sct
	private String url;

	// FHIR Property, human-readable name of the resource (formerly CodeSystem.name in 7.x)
	private String title;
	
	// FHIR Property, primary language of this resource, must be a valid two letter ISO-639 language code, if defined
	private String language;
	
	// FHIR Property, supports markdown
	private String description;

	// FHIR Property, publication status of the resource, usually draft|active|retired|unknown (maybe experimental, etc.)
	private String status;

	// FHIR Property, supports markdown
	private String copyright;

	// ('publisher' FHIR property, but owner is way better for our use cases)
	private String owner;

	// FHIR Property, primary e-mail address or contact URL (FHIR property)
	private String contact;

	// useContext from FHIR is just too complex for our initial use cases, using former usage field instead
	private String usage;
	
	// FHIR property, supports markdown
	private String purpose;

	/**
	 * @return the type of the resource
	 */
	public abstract String getResourceType();
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public ResourceURI getResourceURI() {
		return ResourceURI.of(getResourceType(), getId());
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the human-readable name of this resource, eg. "{@code SNOMED Clinical Terms}"
	 */
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * The primary language tag of the resource, eg. "en_US"
	 * @return
	 */
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	
}