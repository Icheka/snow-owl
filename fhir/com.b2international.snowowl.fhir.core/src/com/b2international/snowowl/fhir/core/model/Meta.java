/*******************************************************************************
 * Copyright (c) 2018-2021 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.fhir.core.model;

import java.util.ArrayList;
import java.util.List;

import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.dt.Instant;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.Searchable;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FHIR Resource Metadata
 * 
 * @see <a href="https://www.hl7.org/fhir/resource.html#Meta">FHIR:Resource:Meta</a>
 * @since 6.6
 */
public class Meta extends Element {
	
	@Summary
	private final Id versionId;
	
	@Summary
	@Searchable(type = "Date")
	private final Instant lastUpdated;
	
	@Summary
	private final List<Uri> profiles;
	
	@Summary
	private final List<Coding> securities;
	
	@Summary
	private final List<Coding> tags;

	protected Meta(String id, List<Extension> extensions,
			final Id versionId, final Instant lastUpdated, final List<Uri> profiles, final List<Coding> securities, final List<Coding> tags) {
		
		super(id, extensions);
		this.versionId = versionId;
		this.lastUpdated = lastUpdated;
		this.profiles = profiles;
		this.securities = securities;
		this.tags = tags;
	}

	public Id getVersionId() {
		return versionId;
	}
	
	public Instant getLastUpdated() {
		return lastUpdated;
	}
	
	@JsonProperty("tag")
	public List<Coding> getTags() {
		return tags;
	}
	
	@JsonProperty("profile")
	public List<Uri> getProfiles() {
		return profiles;
	}
	
	@JsonProperty("security")
	public List<Coding> getSecurities() {
		return securities;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends Element.Builder<Builder, Meta> {
		
		private Id versionId;
		private Instant lastUpdated;
		private List<Uri> profiles;
		private List<Coding> securities;
		private List<Coding> tags;
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		public Builder versionId(Id versionId) {
			this.versionId = versionId;
			return getSelf();
		}
		
		public Builder versionId(String versionId) {
			this.versionId = new Id(versionId);
			return getSelf();
		}
		
		public Builder lastUpdated(Instant lastUpdated) {
			this.lastUpdated = lastUpdated;
			return getSelf();
		}
		
		public Builder addProfile(Uri profileUri) {
			if (profiles == null) {
				profiles = new ArrayList<>();
			}
			profiles.add(profileUri);
			return getSelf();
		}

		public Builder addProfile(String profile) {
			if (profiles == null) {
				profiles = new ArrayList<>();
			}
			profiles.add(new Uri(profile));
			return getSelf();
		}
		
		public Builder addSecurity(Coding security) {
			if (securities == null) {
				securities = new ArrayList<>();
			}
			securities.add(security);
			return getSelf();
		}

		public Builder addTag(Coding tag) {
			if (tag != null) {
				if (tags == null) {
					tags = new ArrayList<>();
				}
				tags.add(tag);
			}
			return getSelf();
		}
		
		@Override
		protected Meta doBuild() {
			return new Meta(id, extensions, versionId, lastUpdated, profiles, securities, tags);
		}
	}
}
