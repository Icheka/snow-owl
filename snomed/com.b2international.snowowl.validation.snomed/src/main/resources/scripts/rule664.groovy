package scripts;

import java.util.function.Supplier

import com.b2international.index.Hits
import com.b2international.index.aggregations.AggregationBuilder
import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts
import com.b2international.snowowl.snomed.core.domain.SnomedDescription
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument
import com.google.common.base.Suppliers
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap

def RevisionSearcher searcher = ctx.service(RevisionSearcher.class)
def Set<ComponentIdentifier> issues = []

def Supplier<Set<String>> activeConceptIds = Suppliers.memoize({
	def Set<String> conceptIds = []
	
	searcher.stream(Query.select(String.class)
		.from(SnomedConceptDocument.class)
		.fields(SnomedConceptDocument.Fields.ID)
		.where(SnomedConceptDocument.Expressions.active())
		.limit(100_000)
		.build())
		.forEachOrdered({ Hits<String> conceptBatch ->
			conceptIds.addAll(conceptBatch.getHits())
		})
	
	return conceptIds
})

if (params.isUnpublishedOnly) {
	
	def Multimap<String, String> descriptionsByTerm = HashMultimap.create()

	// load all unpublished FSNs with their terms first	
	searcher.stream(Query.select(String[].class)
		.from(SnomedDescriptionIndexEntry.class)
		.fields(SnomedDescriptionIndexEntry.Fields.ID, SnomedDescriptionIndexEntry.Fields.TERM, SnomedDescriptionIndexEntry.Fields.CONCEPT_ID)
		.where(
			Expressions.bool()
				.filter(SnomedDescriptionIndexEntry.Expressions.active())
				.filter(SnomedDescriptionIndexEntry.Expressions.type(Concepts.FULLY_SPECIFIED_NAME))
				.filter(SnomedDocument.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
			.build()
		)
		.limit(10_000)
		.build())
		.forEachOrdered({ Hits<String[]> descriptionsToCheck -> 
			
			descriptionsToCheck.each { descriptionToCheck ->
				if (activeConceptIds.get().contains(descriptionToCheck[2])) {
					descriptionsByTerm.put(descriptionToCheck[1], descriptionToCheck[0])
				}
			}
			
		})
		
	// then check the duplication between all unpublished terms by checking larger than 1 buckets
	descriptionsByTerm.keySet().each { term ->
		def descriptions = descriptionsByTerm.get(term)
		// report all buckets with more than 1 item
		if (descriptions.size() > 1) {
			descriptions.each { description ->
				issues.add(ComponentIdentifier.of(SnomedDescription.TYPE, description))
			}
		}
	}
	
	// then scroll through all possible duplicates among published terms
	searcher.stream(Query.select(String[].class)
		.from(SnomedDescriptionIndexEntry.class)
		.fields(SnomedDescriptionIndexEntry.Fields.ID, SnomedDescriptionIndexEntry.Fields.CONCEPT_ID, SnomedDescriptionIndexEntry.Fields.TERM)
		.where(
			Expressions.bool()
				.filter(SnomedDescriptionIndexEntry.Expressions.active())
				.filter(SnomedDescriptionIndexEntry.Expressions.type(Concepts.FULLY_SPECIFIED_NAME))
				.filter(SnomedDescriptionIndexEntry.Expressions.matchTerm(descriptionsByTerm.keySet())) // send in all unpublished terms
				.mustNot(SnomedDocument.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)) // only published
			.build()
		)
		.limit(10_000)
		.build())
		.each { Hits<String[]> publishedTermsBatch ->
			// all returned terms are duplicate of an unpublished term
			publishedTermsBatch.each { publishedTerm ->
				if (activeConceptIds.get().contains(publishedTerm[1])) {
					issues.add(ComponentIdentifier.of(SnomedDescription.TYPE, publishedTerm[0]))
					String term = publishedTerm[2];
					descriptionsByTerm.get(term).forEach({ descriptionId -> 
						//Report the unpublished term
						issues.add(ComponentIdentifier.of(SnomedDescription.TYPE,  descriptionId))
					});
				}
			}
		}
	
} else {
	// published and unpublished FSNs both count, use aggregation to gather all possible terms
	// TODO eval performance diff of scroll through all sorted via term vs aggregation
	searcher
		.aggregate(AggregationBuilder.bucket("rule664", String.class, SnomedDescriptionIndexEntry.class)
				.query(
						Expressions.bool()
						.filter(SnomedDescriptionIndexEntry.Expressions.active())
						.filter(SnomedDescriptionIndexEntry.Expressions.type(Concepts.FULLY_SPECIFIED_NAME))
						.filter(SnomedDescriptionIndexEntry.Expressions.concepts(activeConceptIds.get()))
						.build()
						)
				.onFieldValue(SnomedDescriptionIndexEntry.Fields.TERM)
				.fields(SnomedDescriptionIndexEntry.Fields.ID)
				.minBucketSize(2))
		.getBuckets()
		.values()
		.each { bucket ->
			bucket.each { id ->
				issues.add(ComponentIdentifier.of(SnomedDescription.TYPE, id))
			}
		}
}


return issues.toList()
