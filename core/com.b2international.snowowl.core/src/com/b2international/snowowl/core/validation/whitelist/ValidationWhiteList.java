/*
 * Copyright 2018-2021 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.validation.whitelist;

import java.io.Serializable;
import java.util.List;

import com.b2international.commons.collections.Collections3;
import com.b2international.index.Analyzers;
import com.b2international.index.Doc;
import com.b2international.index.ID;
import com.b2international.index.mapping.Field;
import com.b2international.index.mapping.FieldAlias;
import com.b2international.index.mapping.FieldAlias.FieldAliasType;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * @since 6.1
 */
@Doc
public final class ValidationWhiteList implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * @since 6.1
	 */
	public static final class Fields {
		public static final String ID = "id";
		public static final String RULE_ID = "ruleId";
		public static final String COMPONENT_ID = "componentId";
		public static final String TERMINOLOGY_COMPONENT_ID = "componentType";
		public static final String AFFECTED_COMPONENT_LABELS = "affectedComponentLabels";
		public static final String AFFECTED_COMPONENT_LABELS_TEXT = AFFECTED_COMPONENT_LABELS + ".text";
		public static final String AFFECTED_COMPONENT_LABELS_PREFIX = AFFECTED_COMPONENT_LABELS + ".prefix";
		public static final String REPORTER = "reporter";
		public static final String CREATED_AT = "createdAt";
	}
	
	@ID
	private final String id;
	private final String ruleId;
	private final String reporter;
	private final Long createdAt;
	private final String componentId;
	private final String componentType;
	
	@Field(aliases = {
		@FieldAlias(name = "text", type = FieldAliasType.TEXT, analyzer = Analyzers.TOKENIZED, searchAnalyzer = Analyzers.TOKENIZED_SYNONYMS),
		@FieldAlias(name = "prefix", type = FieldAliasType.TEXT, analyzer = Analyzers.PREFIX, searchAnalyzer = Analyzers.TOKENIZED)
	})
	private final List<String> affectedComponentLabels;
	
	private transient ComponentIdentifier componentIdentifier;
	
	public ValidationWhiteList(
			final String id,
			final String ruleId,
			final String reporter,
			final Long createdAt,
			final ComponentIdentifier componentIdentifier,
			final List<String> affectedComponentLabels) {
		this(id, ruleId, reporter, createdAt, componentIdentifier.getComponentType(), componentIdentifier.getComponentId(), affectedComponentLabels);
	}

	@JsonCreator
	public ValidationWhiteList(
			@JsonProperty("id") final String id,
			@JsonProperty("ruleId") final String ruleId,
			@JsonProperty("reporter") final String reporter,
			@JsonProperty("createdAt") final Long createdAt,
			@JsonProperty("terminologyComponentId") final String componentType,
			@JsonProperty("componentId") final String componentId,
			@JsonProperty("affectedComponentLabels") final List<String> affectedComponentLabels) {
		this.id = id;
		this.ruleId = ruleId;
		this.reporter = reporter;
		this.createdAt = createdAt;
		this.componentType = componentType;
		this.componentId = componentId;
		this.affectedComponentLabels = Collections3.toImmutableList(affectedComponentLabels);
	}
	
	public String getId() {
		return id;
	}

	public String getRuleId() {
		return ruleId;
	}
	
	@JsonIgnore
	public ComponentIdentifier getComponentIdentifier() {
		if (componentIdentifier == null) {
			componentIdentifier = ComponentIdentifier.of(componentType, componentId);
		}
		return componentIdentifier;
	}
	
	@JsonProperty
	String getComponentId() {
		return componentId;
	}
	
	@JsonProperty
	String getComponentType() {
		return componentType;
	}
	
	public List<String> getAffectedComponentLabels() {
		return affectedComponentLabels;
	}
	
	public String getReporter() {
		return reporter;
	}
	
	public Long getCreatedAt() {
		return createdAt;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
			.add("id", id)
			.add("ruleId", ruleId)
			.add("reporter", reporter)
			.add("createdAt", createdAt)
			.add("componentIdentifier", getComponentIdentifier())
			.toString();
	}
	
}