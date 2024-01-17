/*
 * Copyright 2011-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.datastore.index.entry;

import static com.b2international.index.query.Expressions.exactMatch;
import static com.b2international.index.query.Expressions.match;
import static com.b2international.index.query.Expressions.matchAny;
import static com.b2international.index.query.Expressions.matchAnyLong;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Sets.newHashSetWithExpectedSize;

import java.util.*;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSortedSet;
import com.b2international.commons.StringUtils;
import com.b2international.commons.collections.Collections3;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.index.Doc;
import com.b2international.index.Script;
import com.b2international.index.query.Expression;
import com.b2international.index.query.SortBy;
import com.b2international.index.revision.Revision;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.similarity.Similarity;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;

/**
 * A transfer object representing a SNOMED CT concept.
 */
@Doc(
	type=SnomedConcept.TYPE,
	revisionHash = { 
		SnomedDocument.Fields.ACTIVE, 
		SnomedDocument.Fields.EFFECTIVE_TIME, 
		SnomedDocument.Fields.MODULE_ID,
		SnomedDocument.Fields.RELEASED, // XXX required for SnomedComponentRevisionConflictProcessor CHANGED vs. DELETED detection 
		SnomedConceptDocument.Fields.DEFINITION_STATUS_ID,
		SnomedConceptDocument.Fields.EXHAUSTIVE,
		SnomedConceptDocument.Fields.MAP_TARGET_COMPONENT_TYPE
	}
)
@JsonDeserialize(builder=SnomedConceptDocument.Builder.class)
@Script(
	name="doiFactor", 
	script=
	"double interest = params.useDoi ? (doc.doi.value - params.minDoi) / (params.maxDoi - params.minDoi) : 0;"
	+ "String id = doc.id.value;" 
	+ "return params.termScores.containsKey(id) ? params.termScores.get(id) + interest : 0.0d;")
@Script(name="doi", script="return doc.doi.value")
@Script(
	name="termSort", 
	script=
	// select first preferred SYNONYM
	  "for (locale in params.locales) {"
	+ "	for (description in params._source.preferredDescriptions) {"
	+ "		if (!params.synonymIds.contains(description.typeId)) {"
	+ "			continue;"
	+ "		}"
	+ "		if (description.languageRefSetIds.contains(locale)) {"
	+ "			return description.term;"
	+ "		}"
	+ "	}"
	+ "}"
	// if there is no first preferred synonym then select first preferred FSN
	+ "for (locale in params.locales) {"
	+ "	for (description in params._source.preferredDescriptions) {"
	+ "		if (!\"900000000000003001\".equals(description.typeId)) {"
	+ "			continue;"
	+ "		}"
	+ "		if (description.languageRefSetIds.contains(locale)) {"
	+ "			return description.term;"
	+ "		}"
	+ "	}"
	+ "}"
	// if there is no first preferred FSN, then select the first FSN in the list (the index will contain only active description in creation order)
	+ "for (description in params._source.preferredDescriptions) {"
	+ "	if (\"900000000000003001\".equals(description.typeId)) {"
	+ "		return description.term;"
	+ "	}"
	+ "}"
	// Otherwise select the ID for sorting
	+ "return doc.id.value")
public final class SnomedConceptDocument extends SnomedComponentDocument {

	public static final float DEFAULT_DOI = 1.0f;

	public static SortBy sortByTerm(List<String> languageRefSetPreferenceList, Set<String> synonymIds, SortBy.Order order) {
		return SortBy.script("termSort", ImmutableMap.of("locales", languageRefSetPreferenceList, "synonymIds", synonymIds), order);
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public final static class Expressions extends SnomedComponentDocument.Expressions {
		
		private Expressions() {
		}

		private static Iterable<Long> toLongValues(Collection<String> values) {
			final Set<Long> result = newHashSetWithExpectedSize(values.size());
			for (String value : values) {
				try {
					result.add(Long.valueOf(value));
				} catch (NumberFormatException e) {
					throw new BadRequestException("'%s' value is not a valid SNOMED CT identifier", value);
				}
			}
			return result;
		}
		
		public static Expression parents(Collection<String> parentIds) {
			return matchAnyLong(Fields.PARENTS, toLongValues(parentIds));
		}

		public static Expression ancestors(Collection<String> ancestorIds) {
			return matchAnyLong(Fields.ANCESTORS, toLongValues(ancestorIds));
		}

		public static Expression statedParents(Collection<String> statedParentIds) {
			return matchAnyLong(Fields.STATED_PARENTS, toLongValues(statedParentIds));
		}
		
		public static Expression statedAncestors(Collection<String> statedAncestorIds) {
			return matchAnyLong(Fields.STATED_ANCESTORS, toLongValues(statedAncestorIds));
		}
		
		public static Expression definitionStatusId(String definitionStatusId) {
			return exactMatch(Fields.DEFINITION_STATUS_ID, definitionStatusId);
		}
		
		public static Expression definitionStatusIds(Iterable<String> definitionStatusIds) {
			return matchAny(Fields.DEFINITION_STATUS_ID, definitionStatusIds);
		}
		
		public static Expression exhaustive() {
			return match(Fields.EXHAUSTIVE, true);
		}
		
		public static Expression refSetType(SnomedRefSetType type) {
			return refSetTypes(Collections.singleton(type));
		}
		
		public static Expression refSetTypes(Collection<SnomedRefSetType> types) {
			return matchAny(Fields.REFSET_TYPE, FluentIterable.from(types).transform(type -> type.name()).toSet());
		}
		
		public static Expression referencedComponentType(int referencedComponentType) {
			return match(Fields.REFERENCED_COMPONENT_TYPE, referencedComponentType);
		}
		
		public static Expression referencedComponentTypes(Collection<String> referencedComponentTypes) {
			return matchAny(Fields.REFERENCED_COMPONENT_TYPE, referencedComponentTypes);
		}
		
		public static Expression mapTargetComponentType(int mapTargetComponentType) {
			return match(Fields.MAP_TARGET_COMPONENT_TYPE, mapTargetComponentType);
		}
		
		public static Expression mapTargetComponentTypes(Collection<String> mapTargetComponentTypes) {
			return matchAny(Fields.MAP_TARGET_COMPONENT_TYPE, mapTargetComponentTypes);
		}
		
		public static Expression mapSourceComponentType(int mapSourceComponentType) {
			return match(Fields.MAP_SOURCE_COMPONENT_TYPE, mapSourceComponentType);
		}
		
		public static Expression mapSourceComponentTypes(Collection<String> mapSourceComponentTypes) {
			return matchAny(Fields.MAP_SOURCE_COMPONENT_TYPE, mapSourceComponentTypes);
		}

		public static Expression semanticTag(String semanticTag) {
			return exactMatch(Fields.SEMANTIC_TAGS, semanticTag);
		}
		
		public static Expression semanticTags(Iterable<String> semanticTags) {
			return matchAny(Fields.SEMANTIC_TAGS, semanticTags);
		}
		
	}

	public static class Fields extends SnomedComponentDocument.Fields {
		
		public static final String REFSET_STORAGEKEY = "refSetStorageKey";
		public static final String DEFINITION_STATUS_ID = "definitionStatusId";
		public static final String EXHAUSTIVE = "exhaustive";
		public static final String ANCESTORS = "ancestors";
		public static final String STATED_ANCESTORS = "statedAncestors";
		public static final String PARENTS = "parents";
		public static final String STATED_PARENTS = "statedParents";
		public static final String REFSET_TYPE = "refSetType";
		public static final String REFERENCED_COMPONENT_TYPE = "referencedComponentType";
		public static final String MAP_TARGET_COMPONENT_TYPE = "mapTargetComponentType";
		public static final String MAP_SOURCE_COMPONENT_TYPE = "mapSourceComponentType";
		public static final String DOI = "doi";
		public static final String PREFERRED_DESCRIPTIONS = "preferredDescriptions";
		public static final String SEMANTIC_TAGS = "semanticTags";
		public static final String TERM_SORT = "termSort";
		public static final String SIMILARITY_FIELD = "similarity.predicted_value";
	}
	
	public static Builder builder(final SnomedConceptDocument input) {
		final String id = input.getId();
		return builder()
				.id(id)
				.moduleId(input.getModuleId())
				.active(input.isActive())
				.released(input.isReleased())
				.effectiveTime(input.getEffectiveTime())
				.iconId(input.getIconId())
				.definitionStatusId(input.getDefinitionStatusId())
				.exhaustive(input.isExhaustive())
				.parents(input.getParents())
				.ancestors(input.getAncestors())
				.statedParents(input.getStatedParents())
				.statedAncestors(input.getStatedAncestors())
				.memberOf(input.getMemberOf())
				.activeMemberOf(input.getActiveMemberOf())
				.referencedComponentType(input.getReferencedComponentType())
				.mapTargetComponentType(input.getMapTargetComponentType())
				.mapSourceComponentType(input.getMapSourceComponentType())
				.preferredDescriptions(input.getPreferredDescriptions())
				.refSetType(input.getRefSetType())
				.doi(input.getDoi())
				.memberOf(input.getMemberOf())
				.activeMemberOf(input.getActiveMemberOf())
				.semanticTags(input.getSemanticTags())
				.similarity(input.getSimilarity());
	}
	
	public static Builder builder(SnomedConcept input) {
		String id = input.getId();
		final Builder builder = builder()
				.id(id)
				.moduleId(input.getModuleId())
				.active(input.isActive())
				.released(input.isReleased())
				.effectiveTime(EffectiveTimes.getEffectiveTime(input.getEffectiveTime()))
				.iconId(input.getIconId())
				.definitionStatusId(input.getDefinitionStatusId())
				.exhaustive(input.getSubclassDefinitionStatus().isExhaustive())
				.parents(PrimitiveSets.newLongSortedSet(input.getParentIds()))
				.ancestors(PrimitiveSets.newLongSortedSet(input.getAncestorIds()))
				.statedParents(PrimitiveSets.newLongSortedSet(input.getStatedParentIds()))
				.statedAncestors(PrimitiveSets.newLongSortedSet(input.getStatedAncestorIds()));
		
		if (input.getReferenceSet() != null) {
			builder.refSet(input.getReferenceSet());
		}
		
//		if (input.getScore() != null) {
//			builder.score(input.getScore());
//		}
		
		return builder;
	}

	@JsonPOJOBuilder(withPrefix="")
	public static class Builder extends SnomedComponentDocument.Builder<Builder, SnomedConceptDocument> {

		private String definitionStatusId;
		private Boolean exhaustive;
		private LongSortedSet parents;
		private LongSortedSet ancestors;
		private LongSortedSet statedParents;
		private LongSortedSet statedAncestors;
		private SnomedRefSetType refSetType;
		private String referencedComponentType = TerminologyRegistry.UNKNOWN_COMPONENT_TYPE;
		private String mapTargetComponentType = TerminologyRegistry.UNKNOWN_COMPONENT_TYPE;
		private String mapSourceComponentType = TerminologyRegistry.UNKNOWN_COMPONENT_TYPE;
		private List<SnomedDescriptionFragment> preferredDescriptions = Collections.emptyList();
		private SortedSet<String> semanticTags = Collections.emptySortedSet();
		private float doi = DEFAULT_DOI;
		private Similarity similarity;

		@JsonCreator
		private Builder() {
			// Disallow instantiation outside static method
		}
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		public Builder definitionStatusId(final String definitionStatusId) {
			this.definitionStatusId = definitionStatusId;
			return getSelf();
		}

		public Builder exhaustive(final Boolean exhaustive) {
			this.exhaustive = exhaustive;
			return getSelf();
		}
		
		@JsonIgnore
		public Builder parents(final long...parents) {
			return parents(PrimitiveSets.newLongSortedSet(parents));
		}

		@JsonProperty("parents")
		public Builder parents(final LongSortedSet parents) {
			this.parents = parents;
			return getSelf();
		}
		
		@JsonIgnore
		public Builder statedParents(final long...statedParents) {
			return statedParents(PrimitiveSets.newLongSortedSet(statedParents));
		}
		
		@JsonProperty("statedParents")
		public Builder statedParents(final LongSortedSet statedParents) {
			this.statedParents = statedParents;
			return getSelf();
		}
		
		@JsonIgnore
		public Builder ancestors(final long... ancestors) {
			return ancestors(PrimitiveSets.newLongSortedSet(ancestors));
		}
		
		@JsonProperty("ancestors")
		public Builder ancestors(final LongSortedSet ancestors) {
			this.ancestors = ancestors;
			return getSelf();
		}
		
		@JsonIgnore
		public Builder statedAncestors(final long... statedAncestors) {
			return statedAncestors(PrimitiveSets.newLongSortedSet(statedAncestors));
		}
		
		@JsonProperty("statedAncestors")
		public Builder statedAncestors(final LongSortedSet statedAncestors) {
			this.statedAncestors = statedAncestors;
			return getSelf();
		}
		
		@JsonIgnore
		public Builder clearRefSet() {
			referencedComponentType = TerminologyRegistry.UNKNOWN_COMPONENT_TYPE;
			mapTargetComponentType = TerminologyRegistry.UNKNOWN_COMPONENT_TYPE;
			mapSourceComponentType = TerminologyRegistry.UNKNOWN_COMPONENT_TYPE;
			refSetType = null;
			return getSelf();
		}
		
		@JsonIgnore
		public Builder refSet(final SnomedReferenceSet refSet) {
			if (!StringUtils.isEmpty(refSet.getMapTargetComponentType())) {
				mapTargetComponentType(refSet.getMapTargetComponentType());
			}
			
			if (!StringUtils.isEmpty(refSet.getMapSourceComponentType())) {
				mapSourceComponentType(refSet.getMapSourceComponentType());
			}
			
			if (!StringUtils.isEmpty(refSet.getReferencedComponentType())) {
				referencedComponentType(refSet.getReferencedComponentType());
			}
			
			return refSetType(refSet.getType());
		}
		
		public Builder mapTargetComponentType(String mapTargetComponentType) {
			this.mapTargetComponentType = mapTargetComponentType;
			return getSelf();
		}
		
		public Builder mapSourceComponentType(String mapSourceComponentType) {
			this.mapSourceComponentType = mapSourceComponentType;
			return getSelf();
		}
		
		public Builder referencedComponentType(String referencedComponentType) {
			this.referencedComponentType = referencedComponentType;
			return getSelf();
		}

		public Builder refSetType(SnomedRefSetType refSetType) {
			this.refSetType = refSetType;
			return getSelf();
		}

		public Builder doi(float doi) {
			this.doi = doi;
			return getSelf();
		}
		
		public Builder preferredDescriptions(List<SnomedDescriptionFragment> preferredDescriptions) {
			this.preferredDescriptions = Collections3.toImmutableList(preferredDescriptions);
			for (SnomedDescriptionFragment preferredDescription : this.preferredDescriptions) {
				checkArgument(!preferredDescription.getLanguageRefSetIds().isEmpty(), "At least one language reference set ID is required to create a preferred description fragment for description %s.", preferredDescription.getId());
			}
			return getSelf();
		}
		
		public Builder semanticTags(SortedSet<String> semanticTags) {
			this.semanticTags = semanticTags;
			return getSelf();
		}
		
		@JsonSetter
		/*package*/ Builder similarity(Similarity similarity) {
			this.similarity = similarity;
			return getSelf();
		}
		
		public SnomedConceptDocument build() {
			final SnomedConceptDocument entry = new SnomedConceptDocument(id,
					iconId, 
					moduleId, 
					released, 
					active, 
					effectiveTime, 
					definitionStatusId, 
					exhaustive,
					refSetType, 
					referencedComponentType,
					mapTargetComponentType,
					mapSourceComponentType,
					memberOf,
					activeMemberOf,
					preferredDescriptions);
			
			entry.doi = doi;
			entry.setScore(score);
			
			if (parents != null) {
				entry.parents = parents;
			}
			
			if (statedParents != null) {
				entry.statedParents = statedParents;
			}
			
			if (ancestors != null) {
				entry.ancestors = ancestors;
			}
			
			if (statedAncestors != null) {
				entry.statedAncestors = statedAncestors;
			}
			
			if (semanticTags != null) {
				entry.semanticTags = semanticTags;
			}
			
			if (similarity != null) {
				entry.similarity = similarity;
			}
			
			return entry;
		}

	}

	private final String definitionStatusId;
	private final Boolean exhaustive;
	private final SnomedRefSetType refSetType;
	private final String referencedComponentType;
	private final String mapTargetComponentType;
	private final String mapSourceComponentType;
	private final List<SnomedDescriptionFragment> preferredDescriptions;
	
	private LongSortedSet parents;
	private LongSortedSet ancestors;
	private LongSortedSet statedParents;
	private LongSortedSet statedAncestors;
	private SortedSet<String> semanticTags;
	
	private float doi;
	
	// can be computed via external applications and appended into the document before indexing
	private Similarity similarity;

	private SnomedConceptDocument(final String id,
			final String iconId,
			final String moduleId,
			final Boolean released,
			final Boolean active,
			final Long effectiveTime,
			final String definitionStatusId,
			final Boolean exhaustive, 
			final SnomedRefSetType refSetType, 
			final String referencedComponentType,
			final String mapTargetComponentType,
			final String mapSourceComponentType,
			final List<String> referringRefSets,
			final List<String> referringMappingRefSets,
			final List<SnomedDescriptionFragment> preferredDescriptions) {

		super(id, iconId, moduleId, released, active, effectiveTime, referringRefSets, referringMappingRefSets);
		this.definitionStatusId = definitionStatusId;
		this.exhaustive = exhaustive;
		this.refSetType = refSetType;
		this.referencedComponentType = referencedComponentType;
		this.mapTargetComponentType = mapTargetComponentType;
		this.mapSourceComponentType = mapSourceComponentType;
		this.preferredDescriptions = preferredDescriptions;
	}
	
	@Override
	protected Revision.Builder<?, ? extends Revision> toBuilder() {
		return builder(this);
	}
	
	@JsonGetter
	/*package*/ Similarity getSimilarity() {
		return similarity;
	}
	
	public float getDoi() {
		return doi;
	}
	
	/**
	 * @return the concept's definition status id
	 */
	public String getDefinitionStatusId() {
		return definitionStatusId;
	}

	/**
	 * @return {@code true} if the concept subclass definition status is exhaustive, {@code false} otherwise
	 */
	public Boolean isExhaustive() {
		return exhaustive;
	}
	
	public LongSortedSet getParents() {
		return parents;
	}
	
	public LongSortedSet getStatedParents() {
		return statedParents;
	}
	
	public LongSortedSet getAncestors() {
		return ancestors;
	}
	
	public LongSortedSet getStatedAncestors() {
		return statedAncestors;
	}
	
	public SnomedRefSetType getRefSetType() {
		return refSetType;
	}
	
	public String getReferencedComponentType() {
		return referencedComponentType;
	}
	
	public String getMapTargetComponentType() {
		return mapTargetComponentType;
	}
	
	public String getMapSourceComponentType() {
		return mapSourceComponentType;
	}
	
	@JsonIgnore
	public boolean isRefSet() {
		return getRefSetType() != null;
	}
	
	public List<SnomedDescriptionFragment> getPreferredDescriptions() {
		return preferredDescriptions;
	}
	
	public SortedSet<String> getSemanticTags() {
		return semanticTags;
	}
	
	@Override
	protected ToStringHelper doToString() {
		return super.doToString()
				.add("definitionStatusId", definitionStatusId)
				.add("exhaustive", exhaustive)
				.add("refSetType", refSetType)
				.add("referencedComponentType", referencedComponentType)
				.add("mapTargetComponentType", mapTargetComponentType)
				.add("mapSourceComponentType", mapSourceComponentType)
				.add("parents", parents)
				.add("ancestors", ancestors)
				.add("statedParents", statedParents)
				.add("statedAncestors", statedAncestors)
				.add("doi", doi);
	}
	
	/**
	 * Computes whether a reference set is structural or not.
	 * @param refSetId
	 * @param type
	 * @return
	 */
	public static boolean isStructural(final String refSetId, final SnomedRefSetType type) {
		switch (type) {
			case LANGUAGE: //$FALL-THROUGH$
			case CONCRETE_DATA_TYPE: //$FALL-THROUGH$
			case ASSOCIATION: //$FALL-THROUGH$
			case MODULE_DEPENDENCY: //$FALL-THROUGH$
				return true;
			case ATTRIBUTE_VALUE:
				return 
						Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR.equals(refSetId) 
						|| Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR.equals(refSetId) 
						|| Concepts.REFSET_RELATIONSHIP_REFINABILITY.equals(refSetId);
			default: return false;
		}
	}

}

