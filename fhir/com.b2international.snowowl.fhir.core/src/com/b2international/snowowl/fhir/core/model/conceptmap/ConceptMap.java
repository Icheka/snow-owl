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
package com.b2international.snowowl.fhir.core.model.conceptmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;

import com.b2international.snowowl.fhir.core.model.ContactDetail;
import com.b2international.snowowl.fhir.core.model.Meta;
import com.b2international.snowowl.fhir.core.model.MetadataResource;
import com.b2international.snowowl.fhir.core.model.dt.*;
import com.b2international.snowowl.fhir.core.model.usagecontext.UsageContext;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * A concept map defines a mapping from a set of concepts defined in a code
 * system to one or more concepts defined in other code systems.
 * 
 * Key properties:
 * <ul>
 * <li>Each mapping for a concept from source to target includes an equivalence
 * property that specifies how similar the mapping is
 * <li>There is one element for each concept or field in the source that needs
 * to be mapped
 * <li>Each source concept may have multiple targets
 * </ul>
 * 
 * @see <a href="https://www.hl7.org/fhir/conceptmap.html">FHIR:ConceptMap</a>
 * @since 6.10
 */
@JsonDeserialize(builder = ConceptMap.Builder.class, using = JsonDeserializer.None.class)
public class ConceptMap extends MetadataResource {

	private static final long serialVersionUID = 1L;
	
	public static final String RESOURCE_TYPE_CONCEPT_MAP = "ConceptMap";

	/**
	 * @since 8.0
	 */
	public static final class Fields extends MetadataResource.Fields {
		
		public static final String GROUP = "group";
		
		public static final Set<String> MANDATORY = ImmutableSet.<String>builder()
				.addAll(MetadataResource.Fields.MANDATORY)
				.build();
		
		public static final Set<String> SUMMARY = ImmutableSet.<String>builder()
				.addAll(MetadataResource.Fields.SUMMARY)
				.build();
		
		public static final Set<String> SUMMARY_TEXT = ImmutableSet.<String>builder()
				.addAll(MetadataResource.Fields.MANDATORY)
				.build();
		
		public static final Set<String> SUMMARY_DATA = MANDATORY;
		
		public static final Set<String> ALL = ImmutableSet.<String>builder()
				.addAll(MANDATORY)
				.addAll(SUMMARY)
				.add(GROUP)
				.build();

		
	}
	
	// FHIR header "resourceType" : "ConceptMap",
	@Mandatory
	@JsonProperty
	private final String resourceType;

	@Summary
	@JsonProperty("identifier")
	@JsonInclude(value = Include.NON_EMPTY)
	private Collection<Identifier> identifiers;
	
	@JsonProperty
	private String copyright;
	
	@Summary
	@Valid
	private Uri sourceUri;

	@Summary
	@Valid
	private Uri sourceCanonical;

	@Summary
	@Valid
	private Uri targetUri;

	@Summary
	@Valid
	private Uri targetCanonical;

	@Valid
	private final Collection<Group> groups;

	ConceptMap(
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

			// ConceptMap properties
			final String resourceType, 
			final Collection<Identifier> identifiers, 
			final String copyright, 
			final Uri sourceUri, 
			final Uri sourceCanonical, 
			final Uri targetUri, 
			final Uri targetCanonical, 
			final Collection<Group> groups) {
		
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
		this.sourceUri = sourceUri;
		this.sourceCanonical = sourceCanonical;
		this.targetUri = targetUri;
		this.targetCanonical = targetCanonical;
		this.groups = groups;
	}

	@AssertTrue(message = "Both URI and Canonical URI cannot be set for the 'source' and 'target' fields")
	private boolean isValid() {

		if (sourceUri != null && sourceCanonical != null) {
			return false;
		}

		if (targetUri != null && targetCanonical != null) {
			return false;
		}
		return true;
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
	
	@JsonProperty
	public Uri getSourceUri() {
		return sourceUri;
	}

	@JsonProperty
	public Uri getSourceCanonical() {
		return sourceCanonical;
	}

	@JsonProperty
	public Uri getTargetUri() {
		return targetUri;
	}

	@JsonProperty
	public Uri getTargetCanonical() {
		return targetCanonical;
	}
	
	@JsonProperty("group")
	public Collection<Group> getGroups() {
		return groups;
	}

	public static Builder builder() {
		return new Builder(); 
	}
	
	public static Builder builder(String conceptMapId) {
		return new Builder(conceptMapId);
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends MetadataResource.Builder<Builder, ConceptMap> {

		private String resourceType = RESOURCE_TYPE_CONCEPT_MAP;
		
		private Collection<Identifier> identifiers;
		private String copyright;
		private Uri sourceUri;
		private Uri sourceCanonical;
		private Uri targetUri;
		private Uri targetCanonical;
		private Collection<Group> groups;

		@JsonCreator
		private Builder() {
		}

		private Builder(String conceptMapId) {
			super(conceptMapId);
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

		public Builder sourceUri(Uri sourceUri) {
			this.sourceUri = sourceUri;
			return getSelf();
		}

		public Builder sourceUri(String sourceUriString) {
			this.sourceUri = new Uri(sourceUriString);
			return getSelf();
		}

		public Builder sourceCanonical(Uri sourceCanonical) {
			this.sourceCanonical = sourceCanonical;
			return getSelf();
		}

		public Builder sourceCanonical(String sourceCanonical) {
			this.sourceCanonical = new Uri(sourceCanonical);
			return getSelf();
		}

		public Builder targetUri(Uri targetUri) {
			this.targetUri = targetUri;
			return getSelf();
		}

		public Builder targetUri(String targetUriString) {
			this.targetUri = new Uri(targetUriString);
			return getSelf();
		}

		public Builder targetCanonical(Uri targetCanonical) {
			this.targetCanonical = targetCanonical;
			return getSelf();
		}

		public Builder targetCanonical(String targetCanonical) {
			this.targetCanonical = new Uri(targetCanonical);
			return getSelf();
		}
		
		@JsonProperty("group")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder groups(Collection<Group> groups) {
			this.groups = groups;
			return getSelf();
		}

		public Builder addGroup(final Group group) {
			
			if (groups == null) {
				groups = Lists.newArrayList();
			}
			this.groups.add(group);
			return getSelf();
		}

		@Override
		protected Builder getSelf() {
			return this;
		}

		@Override
		protected ConceptMap doBuild() {
			return new ConceptMap(
				// MetdataResource properties
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
				
				// ConceptMap properties
				resourceType, 
				identifiers, 
				copyright, 
				sourceUri, 
				sourceCanonical, 
				targetUri, 
				targetCanonical, 
				groups);
		}
	}
}
