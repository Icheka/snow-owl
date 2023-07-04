/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.request;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.QueryExpression;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * @since 8.11.0
 */
public record SnomedRelationshipStats(
	Table<String, String, Integer> positiveSources, 
	Table<String, String, Integer> totalSources
) {

	@FunctionalInterface
	public interface RelationshipSearchBySource {

		RelationshipSearchBySource DEFAULT = (context, sourceIds) -> SnomedRequests.prepareSearchRelationship()
			.filterByActive(true)
			.filterByCharacteristicType(Concepts.INFERRED_RELATIONSHIP)
			.filterBySources(sourceIds)
			.setLimit(SnomedQueryOptimizer.PAGE_SIZE)
			.setFields(
				SnomedRelationshipIndexEntry.Fields.ID, 
				SnomedRelationshipIndexEntry.Fields.SOURCE_ID, 
				SnomedRelationshipIndexEntry.Fields.TYPE_ID, 
				SnomedRelationshipIndexEntry.Fields.DESTINATION_ID)
			.stream(context)
			.flatMap(SnomedRelationships::stream)
			.filter(r -> !Concepts.IS_A.equals(r.getTypeId())); // Exclude IS_A relationships

		Stream<SnomedRelationship> findRelationshipsBySource(BranchContext context, Set<String> sourceIds);
	}

	@FunctionalInterface
	public interface RelationshipSearchByTypeAndDestination {

		RelationshipSearchByTypeAndDestination DEFAULT = (context, typeIds, destinationIds) -> SnomedRequests.prepareSearchRelationship()
			.filterByActive(true)
			.filterByCharacteristicType(Concepts.INFERRED_RELATIONSHIP)
			.filterByTypes(typeIds)
			.filterByDestinations(destinationIds)
			.setLimit(SnomedQueryOptimizer.PAGE_SIZE)
			.setFields(
				SnomedRelationshipIndexEntry.Fields.ID, 
				SnomedRelationshipIndexEntry.Fields.TYPE_ID, 
				SnomedRelationshipIndexEntry.Fields.DESTINATION_ID)
			.stream(context)
			.flatMap(SnomedRelationships::stream);

		Stream<SnomedRelationship> findRelationshipsByTypeAndDestination(BranchContext context, Set<String> typeIds, Set<String> destinationIds);
	}

	public static SnomedRelationshipStats create(
		final BranchContext context, 
		final Set<String> conceptIds, 
		final RelationshipSearchBySource searchBySource, 
		final RelationshipSearchByTypeAndDestination searchByTypeAndDestination
	) {
		// How many member concepts are there for a particular type-destination pair?
		final Table<String, String, Integer> positiveSources = HashBasedTable.create();
		// How many source concepts are there in total for a particular type-destination pair?
		final Table<String, String, Integer> totalSources = HashBasedTable.create();
		
		if (conceptIds.isEmpty()) {
			// No input concepts
			return new SnomedRelationshipStats(positiveSources, totalSources);	
		}
		
		searchBySource.findRelationshipsBySource(context, conceptIds)
			.forEachOrdered(r -> incrementTableCount(positiveSources, r));

		if (positiveSources.isEmpty()) {
			// No relevant relationships collected
			return new SnomedRelationshipStats(positiveSources, totalSources);
		}
		
		final Set<String> typeIds = positiveSources.rowKeySet();
		final Set<String> destinationIds = positiveSources.columnKeySet();

		searchByTypeAndDestination.findRelationshipsByTypeAndDestination(context, typeIds, destinationIds)
			// XXX: This request may return any combination of the given type-destination pairs, which need to be filtered here
			.filter(r -> positiveSources.contains(r.getTypeId(), r.getDestinationId()))
			.forEachOrdered(r -> incrementTableCount(totalSources, r));

		return new SnomedRelationshipStats(positiveSources, totalSources);
	}

	private static void incrementTableCount(final Table<String, String, Integer> countByTypeAndDestination, final SnomedRelationship relationship) {
		final Map<String, Integer> countByDestination = countByTypeAndDestination.row(relationship.getTypeId());
		countByDestination.merge(relationship.getDestinationId(), 1, (oldCount, newCount) -> oldCount + newCount);
	}

	public void filterByPrecision(final float precisionThreshold) {
		positiveSources.cellSet().removeIf(cell -> {
			final int truePositives = cell.getValue();
			final int total = totalSources.get(cell.getRowKey(), cell.getColumnKey());
			
			if (precisionThreshold >= 1.0f) {
				return truePositives < total;	
			} else {
				final float precision = ((float) truePositives) / total;
				return precision < precisionThreshold;
			}
		});
	}

	public void filterByMinTruePositives(final int minTruePositives) {
		positiveSources.values().removeIf(truePositives -> {
			return truePositives < minTruePositives;
		});
	}

	public void filterByMaxFalsePositives(final int maxFalsePositives) {
		positiveSources.cellSet().removeIf(cell -> {
			final int truePositives = cell.getValue();
			final int total = totalSources.get(cell.getRowKey(), cell.getColumnKey());
			final int falsePositives = total - truePositives;
			return falsePositives > maxFalsePositives;
		});
	}

	public List<QueryExpression> optimizeRefinements(final BranchContext context) {
		if (positiveSources.isEmpty()) {
			return List.of();
		}

		return positiveSources.cellSet()
			.stream()
			.map(cell -> new QueryExpression(IDs.base62UUID(), String.format("* : %s = %s", cell.getRowKey(), cell.getColumnKey()), false))
			.collect(Collectors.toList());
	}
}
