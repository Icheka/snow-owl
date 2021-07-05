/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.FhirConstants;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.exceptions.FhirException;
import com.b2international.snowowl.fhir.core.model.dt.*;
import com.b2international.snowowl.fhir.core.model.usagecontext.UsageContext;
import com.b2international.snowowl.fhir.core.search.Filterable;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.b2international.snowowl.fhir.core.search.Searchable;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * FHIR resource with common meta-data properties.
 * 
 * @since 6.3
 */
@Filterable(filter = "_summary", values = {"TRUE", "TEXT", "DATA", "COUNT", "FALSE"})
@Filterable(filter = "_elements", supportsMultipleValues = true)
//@JsonFilter(FhirBeanPropertyFilter.FILTER_NAME)
public abstract class MetadataResource extends DomainResource {
	
	/**
	 * @since 8.0
	 */
	public static abstract class Fields extends DomainResource.Fields {
		
		public static final String URL = "url";
		public static final String IDENTIFIER = "identifier";
		public static final String VERSION = "version";
		public static final String NAME = "name";
		public static final String TITLE = "title";
		public static final String STATUS = "status";
		public static final String DATE = "date";
		public static final String PUBLISHER = "publisher";
		// XXX do we need contacts???
		public static final String DESCRIPTION = "description";
		// XXX do we need usageContexts???
		// XXX do we need jurisdictions???
		public static final String PURPOSE = "purpose";
		public static final String COPYRIGHT = "copyright";
		
		public static final Set<String> MANDATORY = ImmutableSet.<String>builder()
				.addAll(FhirResource.Fields.MANDATORY)
				.add(STATUS)
				.build();
		
		public static final Set<String> SUMMARY = ImmutableSet.<String>builder()
				.addAll(FhirResource.Fields.SUMMARY)
				.add(URL, IDENTIFIER, VERSION, NAME, TITLE, DATE, PUBLISHER)
				.build();
		
	}
	
	@Summary
	@JsonProperty
	private Uri url;
	
	@Summary
	@JsonProperty
	private Identifier identifier; //OID
	
	@Summary
	@JsonProperty
	private String version;
	
	@Summary
	@JsonProperty
	@Searchable(type = "String", modifiers = {"exact"}, supportsMultipleValues = true)
	private String name;
	
	@Summary
	@JsonProperty
	private String title;
	
	@Mandatory
	@Valid
	@NotNull
	@JsonProperty
	private Code status;
	
	//Revision date
	@Summary
	@JsonProperty
	private Date date;
	
	@Summary
	@JsonProperty
	private String publisher;
	
	@Summary
	@JsonProperty("contact")
	@JsonInclude(value = Include.NON_EMPTY)
	private Collection<ContactDetail> contacts;

	@JsonProperty
	private String description;
	
	@Summary
	@JsonProperty("useContext")
	@SuppressWarnings("rawtypes")
	@JsonInclude(value = Include.NON_EMPTY)
	private Collection<UsageContext> usageContexts;
	
	@Valid
	@Summary
	@JsonProperty("jurisdiction")
	@JsonInclude(value = Include.NON_EMPTY)
	private Collection<CodeableConcept> jurisdictions;
	
	@JsonProperty
	private String purpose;
	
	@JsonProperty
	private String copyright;

	/**
	 * @param id
	 * @param language
	 * @param text
	 */
	@SuppressWarnings("rawtypes")
	public MetadataResource(Id id, final Meta meta, final Uri impliciteRules, Code language, 
			Narrative text, Uri url, Identifier identifier, String version, 
			String name, String title, Code status, final Date date,  final String publisher, final Collection<ContactDetail> contacts, final String description, 
			final Collection<UsageContext> usageContexts, final Collection<CodeableConcept> jurisdictions, final String purpose, final String copyright) {
		
		super(id, meta, impliciteRules, language, text);
		
		this.url = url;
		this.identifier = identifier;
		this.version = version;
		this.name = name;
		this.title = title;
		this.status = status;
		this.date = date;
		this.contacts = contacts;
		this.publisher = publisher;
		this.description = description;
		this.usageContexts = usageContexts;
		this.jurisdictions = jurisdictions;
		this.purpose = purpose;
		this.copyright = copyright;
	}
	
	public String getVersion() {
		return version;
	}
	
	public Uri getUrl() {
		return url;
	}
	
	public String getName() {
		return name;
	}
	
	public Date getDate() {
		return date;
	}
	
	public static abstract class Builder<B extends Builder<B, T>, T extends FhirResource> extends DomainResource.Builder<B, T> {

		protected Uri url; //ORG_LINK or hardcoded provider value
		
		protected Identifier identifier; //OID
		
		protected String version; //not necessarily available - and what to do when we have more than 1??
		
		protected String name;
		
		protected String title;

		protected Code status;
		
		protected Date date;
		
		protected String publisher;

		protected Collection<ContactDetail> contacts = Sets.newHashSet();
		
		protected String description;
		
		@SuppressWarnings("rawtypes")
		protected Collection<UsageContext> usageContexts = Lists.newArrayList(); 

		protected Collection<CodeableConcept> jurisdictions = Sets.newHashSet();
		
		protected String purpose;
		
		protected String copyright;
		
		/**
		 * Use this constructor when a new resource is sent to the server to be created.
		 */
		public Builder() {
		}
		
		public Builder(String resourceId) {
			super(resourceId);
		}
		
		public B url(final Uri url) {
			this.url = url;
			return getSelf();
		}
		
		public B url(final String urlString) {
			this.url = new Uri(urlString);
			return getSelf();
		}
		
		public B identifier(final Identifier identifer) {
			this.identifier = identifer;
			return getSelf();
		}

		public B version(final String version) {
			this.version = version;
			return getSelf();
		}

		public B name(final String name) {
			this.name = name;
			return getSelf();
		}
		
		public B title(final String title) {
			this.title = title;
			return getSelf();
		}

		public B status(PublicationStatus status) {
			this.status = status.getCode();
			return getSelf();
		}
		
		public B date(Date date) {
			this.date = date;
			return getSelf();
		}
		
		public B date(String dateString) {
			DateFormat df = new SimpleDateFormat(FhirConstants.DATE_TIME_FORMAT);
			try {
				this.date = df.parse(dateString);
			} catch (ParseException e) {
				throw FhirException.createFhirError(dateString + " cannot be parsed, use the format " + FhirConstants.DATE_TIME_FORMAT, OperationOutcomeCode.MSG_PARAM_INVALID);
			}
			return getSelf();
		}
		
		public B publisher(String publisher) {
			this.publisher = publisher;
			return getSelf();
		}

		public B addContact(ContactDetail contact) {
			contacts.add(contact);
			return getSelf();
		}
		
		public B description(final String description) {
			this.description = description;
			return getSelf();
		}
		
		@SuppressWarnings("rawtypes")
		public B addUseContext(final UsageContext usageContext) {
			usageContexts.add(usageContext);
			return getSelf();
		}
		
		public B addJurisdiction(final CodeableConcept jurisdiction) {
			jurisdictions.add(jurisdiction);
			return getSelf();
		}
		
		public B purpose(final String purpose) {
			this.purpose = purpose;
			return getSelf();
		}
		
		public B copyright(final String copyright) {
			this.copyright = copyright;
			return getSelf();
		}
	}

}
