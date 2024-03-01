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
package com.b2international.snowowl.fhir.core.model.codesystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemContentMode;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemHierarchyMeaning;
import com.b2international.snowowl.fhir.core.model.ContactDetail;
import com.b2international.snowowl.fhir.core.model.Meta;
import com.b2international.snowowl.fhir.core.model.MetadataResource;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Narrative;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.usagecontext.UsageContext;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * This class represents a FHIR code system. The CodeSystem resource is used to
 * declare the existence of a code system, and its key properties:
 * 
 * <ul>
 * <li>Identifying URL and version
 * <li>Description, Copyright, publication date, and other metadata
 * <li>Some key properties of the code system itself - whether it's case
 * sensitive, version safe, and whether it defines a compositional grammar
 * <li>What filters can be used in value sets that use the code system in a
 * ValueSet.compose element
 * <li>What properties the concepts defined by the code system
 * </ul>
 * 
 * @see <a href="https://www.hl7.org/fhir/codesystem.html">FHIR:CodeSystem</a>
 * @since 6.3
 */
@JsonDeserialize(builder = CodeSystem.Builder.class, using = JsonDeserializer.None.class)
public class CodeSystem extends MetadataResource {

	private static final long serialVersionUID = 1L;

	/**
	 * @since 8.0
	 */
	public static final class Fields extends MetadataResource.Fields {

		// XXX do we need caseSensitive???
		public static final String CONTENT = "content";
		// TODO valueSet
		public static final String COUNT = "count";
		// XXX do we need hierarchyMeaning???
		// XXX do we need compositional???
		// XXX do we need versionNeeded???
		// XXX do we need supplements???

		public static final String COPYRIGHT = "copyright";
		
		// complex properties
		public static final String IDENTIFIER = "identifier";
		public static final String FILTER = "filter";
		public static final String PROPERTY = "property";
		public static final String CONCEPT = "concept";
		
		public static final Set<String> MANDATORY = ImmutableSet.<String>builder()
				.addAll(MetadataResource.Fields.MANDATORY)
				.add(CONTENT)
				.build();
		
		public static final Set<String> SUMMARY = ImmutableSet.<String>builder()
				.addAll(MetadataResource.Fields.SUMMARY)
				.add(COUNT, FILTER, PROPERTY, IDENTIFIER)
				.build();
		
		public static final Set<String> SUMMARY_TEXT = ImmutableSet.<String>builder()
				.addAll(MetadataResource.Fields.MANDATORY)
				.add(TEXT)
				.build();
		
		public static final Set<String> SUMMARY_DATA = MANDATORY;
		
		public static final Set<String> ALL = ImmutableSet.<String>builder()
				.addAll(MANDATORY)
				.addAll(SUMMARY)
				.add(TEXT)
				.add(CONCEPT)
				.build();
	}
	
	public static final String RESOURCE_TYPE_CODE_SYSTEM = "CodeSystem";
	
	@Mandatory
	@JsonProperty
	private String resourceType;
	
	@Summary
	@JsonProperty("identifier")
	@JsonInclude(value = Include.NON_EMPTY)
	private Collection<Identifier> identifiers;
	
	@JsonProperty
	private String copyright;

	@Summary
	@JsonProperty
	private Boolean caseSensitive;
	
	@Summary
	//@Valid - bbanfai: why is this invoked on a null?
	@JsonProperty
	private Uri valueSet;
	
	@Summary
	@JsonProperty
	private Code hierarchyMeaning;
	
	@Summary
	@JsonProperty
	private Boolean compositional;
	
	@Summary
	@JsonProperty
	private Boolean versionNeeded;
	
	@Mandatory
	@Valid
	@NotNull
	@JsonProperty
	private Code content;
	
	@Summary
	@Valid
	@JsonProperty
	private Uri supplements;

	//not primitive int to avoid serialization when the default value is 0
	@Summary
	@Min(value = 0, message = "Count must be equal to or larger than 0")
	@JsonProperty
	private Integer count;

	@Summary
	@JsonProperty(CodeSystem.Fields.FILTER)
	private Collection<Filter> filters;

	/*
	 * The properties supported by this code system
	 */
	@Summary
	@Valid
	@JsonProperty(CodeSystem.Fields.PROPERTY)
	private Collection<SupportedConceptProperty> properties;

	/*
	 * Concepts in the code system, up to the server if they are returned
	 */
	@Valid
	@JsonProperty(CodeSystem.Fields.CONCEPT)
	private Collection<Concept> concepts;
	
	CodeSystem(
			// MetadataResource properties
			final Id id, 
			final Meta meta, 
			final Uri implicitRules, 
			final Code language, 
			final Narrative text, 
			final Uri url, 
			final String version, 
			final String name, 
			final String title, 
			final Code status,
			final Boolean experimental, 
			final Date date, 
			final String publisher, 
			final Collection<ContactDetail> contacts, 
			final String description, 
			final Collection<UsageContext<?>> usageContexts, 
			final Collection<CodeableConcept> jurisdictions, 
			final String purpose,
			final String toolingId,

			// CodeSystem properties
			final String resourceType,
			final Collection<Identifier> identifiers,
			final String copyright,
			final Boolean caseSensitive, 
			final Uri valueSet, 
			final Code hierarchyMeaning, 
			final Boolean compositional, 
			final Boolean versionNeeded,
			final Code content, 
			final Uri supplements, 
			final Integer count, 
			final Collection<Filter> filters, 
			final Collection<SupportedConceptProperty> properties, 
			final Collection<Concept> concepts) {

		super(id, 
			meta, 
			implicitRules, 
			language, 
			text, 
			url, 
			version, 
			name, 
			title, 
			status, 
			experimental, 
			date, 
			publisher, 
			contacts, 
			description, 
			usageContexts, 
			jurisdictions, 
			purpose, 
			toolingId);

		this.resourceType = resourceType;
		this.identifiers = identifiers;
		this.copyright = copyright;
		this.caseSensitive = caseSensitive;
		this.valueSet = valueSet;
		this.hierarchyMeaning = hierarchyMeaning;
		this.compositional = compositional;
		this.versionNeeded = versionNeeded;
		this.content = content;
		this.supplements = supplements;
		this.count = count;
		this.filters = filters;
		this.properties = properties;
		this.concepts = concepts;
	}
	
	public String getResourceType() {
		return resourceType;
	}
	
	public Collection<Identifier> getIdentifiers() {
		return identifiers;
	}
	
	public String getCopyright() {
		return copyright;
	}
	
	public Boolean getCaseSensitive() {
		return caseSensitive;
	}
	
	public Uri getValueSet() {
		return valueSet;
	}
	
	public Code getHierarchyMeaning() {
		return hierarchyMeaning;
	}
	
	public Boolean getCompositional() {
		return compositional;
	}
	
	public Boolean getVersionNeeded() {
		return versionNeeded;
	}
	
	public Code getContent() {
		return content;
	}
	
	public Uri getSupplements() {
		return supplements;
	}
	
	public Integer getCount() {
		return count;
	}
	
	@JsonProperty(CodeSystem.Fields.CONCEPT)
	@JsonInclude(value = Include.NON_EMPTY)
	public Collection<Concept> getConcepts() {
		return concepts;
	}
	
	@JsonProperty(CodeSystem.Fields.FILTER)
	@JsonInclude(value = Include.NON_EMPTY)
	public Collection<Filter> getFilters() {
		return filters;
	}
	
	@JsonProperty(CodeSystem.Fields.PROPERTY)
	@JsonInclude(value = Include.NON_EMPTY)
	public Collection<SupportedConceptProperty> getProperties() {
		return properties;
	}
	
	@JsonIgnore
	public ResourceURI getResourceURI() {
		return ResourceURI.of(com.b2international.snowowl.core.codesystem.CodeSystem.RESOURCE_TYPE, getId().getIdValue());
	}
	
	public static Builder builder() {
		return new Builder();
	}

	public static Builder builder(String codeSystemId) {
		return new Builder(codeSystemId);
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static final class Builder extends MetadataResource.Builder<Builder, CodeSystem> {

		private String resourceType = RESOURCE_TYPE_CODE_SYSTEM;
		private Collection<Identifier> identifiers;
		private String copyright;
		private Boolean caseSensitive;
		private Uri valueSet;
		private Code hierarchyMeaning;
		private Boolean compositional;
		private Boolean versionNeeded;
		private Code content;
		private Uri supplements;
		private Integer count;
		private Collection<Filter> filters;
		private Collection<SupportedConceptProperty> properties;
		private Collection<Concept> concepts;
		
		/**
		 * Use this constructor when a new resource is sent to the server to be created.
		 */
		public Builder() {
		}

		public Builder(String codeSystemId) {
			super(codeSystemId);
		}

		@Override
		protected Builder getSelf() {
			return this;
		}
		
		public Builder resourceType(String resourceType) {
			this.resourceType = resourceType;
			return getSelf();
		}
		
		@JsonProperty("identifier")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder identifiers(final Collection<Identifier> identifers) {
			this.identifiers = identifers;
			return getSelf();
		}
		
		public Builder addIdentifier(Identifier identifier) {
			if (identifier == null) {
				return getSelf();
			}
			
			if (identifiers == null) {
				identifiers = new ArrayList<>();
			}
			
			identifiers.add(identifier);
			return getSelf();
		}
		
		public Builder copyright(final String copyright) {
			this.copyright = copyright;
			return getSelf();
		}
		
		public Builder caseSensitive(Boolean caseSensitive) {
			this.caseSensitive = caseSensitive;
			return getSelf();
		}
		
		public Builder valueSet(Uri valueSetUri) {
			this.valueSet = valueSetUri;
			return getSelf();
		}
		
		public Builder valueSet(String valueSet) {
			this.valueSet = new Uri(valueSet);
			return getSelf();
		}
		
		@JsonIgnore
		public Builder hierarchyMeaning(Code hierarchyMeaning) {
			this.hierarchyMeaning = hierarchyMeaning;
			return getSelf();
		}
		
		@JsonProperty
		public Builder hierarchyMeaning(CodeSystemHierarchyMeaning codeSystemHierarchyMeaning) {
			if (codeSystemHierarchyMeaning == null) {
				hierarchyMeaning = null;
			} else {
				this.hierarchyMeaning = codeSystemHierarchyMeaning.getCode();
			}
			return getSelf();
		}
		
		public Builder compositional(Boolean compositional) {
			this.compositional = compositional;
			return getSelf();
		}
		
		public Builder versionNeeded(Boolean versionNeeded) {
			this.versionNeeded = versionNeeded;
			return getSelf();
		}

		@JsonIgnore
		public Builder content(Code content) {
			this.content = content;
			return getSelf();
		}
		
		@JsonProperty
		public Builder content(CodeSystemContentMode contentMode) {
			this.content = contentMode.getCode();
			return getSelf();
		}

		public Builder supplements(Uri supplementsUri) {
			this.supplements = supplementsUri;
			return getSelf();
		}

		public Builder count(Integer count) {
			this.count = count;
			return getSelf();
		}
		
		@JsonProperty(CodeSystem.Fields.FILTER)
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder filters(Collection<Filter> filters) {
			this.filters = filters;
			return getSelf();
		}

		public Builder addFilter(Filter filter) {
			if (filters == null) {
				filters = new ArrayList<>();
			}
			this.filters.add(filter);
			return getSelf();
		}
		
		@JsonProperty(CodeSystem.Fields.PROPERTY)
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder properties(Collection<SupportedConceptProperty> properties) {
			this.properties = properties;
			return getSelf();
		}

		public Builder addProperty(SupportedConceptProperty property) {
			
			if (properties == null) {
				properties = new ArrayList<SupportedConceptProperty>();
			}
			
			this.properties.add(property);
			return getSelf();
		}
		
		@JsonProperty(CodeSystem.Fields.CONCEPT)
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder concepts(Collection<Concept> concepts) {
			this.concepts = concepts;
			return getSelf();
		}

		public Builder addConcept(Concept concept) {
			if (concepts == null) {
				concepts = Sets.newHashSet();
			}
			concepts.add(concept);
			return getSelf();
		}
		
		@Override
		protected CodeSystem doBuild() {
			return new CodeSystem(
				// MetadataResource properties
				id, 
				meta, 
				implicitRules, 
				language, 
				text, 
				url, 
				version, 
				name, 
				title, 
				status, 
				experimental, 
				date, 
				publisher, 
				contacts, 
				description, 
				usageContexts, 
				jurisdictions, 
				purpose, 
				toolingId,
				
				// CodeSystem properties
				resourceType, 
				identifiers, 
				copyright, 
				caseSensitive, 
				valueSet, 
				hierarchyMeaning, 
				compositional, 
				versionNeeded, 
				content, 
				supplements, 
				count, 
				filters, 
				properties, 
				concepts);
		}
	}
}
