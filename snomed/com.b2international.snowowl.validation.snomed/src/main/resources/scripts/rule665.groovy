package scripts

import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.ADDITIONAL_RELATIONSHIP
import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.CHARACTERISTIC_TYPE
import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.INFERRED_RELATIONSHIP
import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.STATED_RELATIONSHIP

import java.util.stream.Stream

import com.b2international.index.Hits
import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.snomed.core.domain.SnomedConcept
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests

RevisionSearcher searcher = ctx.service(RevisionSearcher.class)

String deprecatedCharTypesEclFormat = "<%s MINUS (%s OR %s OR %S)"

Set<String> deprecatedCharacheristicTypes = SnomedRequests.prepareSearchConcept()
	.all()
	.filterByEcl(String.format(deprecatedCharTypesEclFormat, CHARACTERISTIC_TYPE, INFERRED_RELATIONSHIP, STATED_RELATIONSHIP, ADDITIONAL_RELATIONSHIP))
	.build()
	.execute(ctx)
	.collect({ SnomedConcept c -> c.getId() })
	
ExpressionBuilder fitlerExpressionBuilder = Expressions.bool()
		.filter(SnomedRelationshipIndexEntry.Expressions.active())
		.filter(SnomedRelationshipIndexEntry.Expressions.characteristicTypeIds(deprecatedCharacheristicTypes))
		
if (params.isUnpublishedOnly) {
	fitlerExpressionBuilder.filter(SnomedDocument.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
}
		
Stream<Hits<String>> queryResult =  searcher.stream(Query.select(String.class)
	.from(SnomedRelationshipIndexEntry.class)
	.fields(SnomedRelationshipIndexEntry.Fields.ID)
	.where(fitlerExpressionBuilder.build())
	.limit(10_000)
	.build())

List<ComponentIdentifier> issues =  new ArrayList<>();

queryResult.forEachOrdered({hits -> 
	for (String relationshipId : hits) {
		issues.add(ComponentIdentifier.of(SnomedRelationship.TYPE, relationshipId))
	}
})

return issues