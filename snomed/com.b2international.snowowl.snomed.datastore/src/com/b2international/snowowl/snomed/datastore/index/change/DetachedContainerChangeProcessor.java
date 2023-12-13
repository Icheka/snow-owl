/*
 * Copyright 2018-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.datastore.index.change;

import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Set;

import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.StagingArea;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.repository.ChangeSetProcessorBase;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.index.entry.*;

/**
 * Processes deleted container components and makes sure that components contained by the container will be deleted/inactivated etc. as well. 
 * 
 * @since 7.0
 */
public final class DetachedContainerChangeProcessor extends ChangeSetProcessorBase {

	public DetachedContainerChangeProcessor() {
		super("referring members");
	}

	@Override
	public void process(StagingArea staging, RevisionSearcher searcher) throws IOException {
		final Set<String> deletedCoreComponentIds = newHashSet();
		final Set<String> deletedConceptIds = newHashSet();
		
		staging.getRemovedObjects().forEach(detachedObject -> {
			if (detachedObject instanceof SnomedComponentDocument) {
				String id = ((SnomedComponentDocument) detachedObject).getId();
				deletedCoreComponentIds.add(id);
				if (detachedObject instanceof SnomedConceptDocument) {
					deletedConceptIds.add(id);
				}
			}
		});
		
		if (deletedCoreComponentIds.isEmpty() && deletedConceptIds.isEmpty()) {
			return;
		}
		
		final int pageSize = ((ServiceProvider) staging.getContext()).getPageSize();
		
		// deleting concepts should delete all of its descriptions, relationships, and inbound relationships
		Query.select(SnomedDescriptionIndexEntry.class)
			.where(SnomedDescriptionIndexEntry.Expressions.concepts(deletedConceptIds))
			.limit(pageSize)
			.build()
			.stream(searcher)
			.flatMap(Hits::stream)
			.forEachOrdered(description -> {
				deletedCoreComponentIds.add(description.getId());
				stageRemove(description);
			});
		
		Query.select(SnomedRelationshipIndexEntry.class)
			.where(Expressions.bool()
				.should(SnomedRelationshipIndexEntry.Expressions.sourceIds(deletedConceptIds))
				.should(SnomedRelationshipIndexEntry.Expressions.destinationIds(deletedConceptIds))
				.build())
			.limit(pageSize)
			.build()
			.stream(searcher)
			.flatMap(Hits::stream)
			.forEachOrdered(relationship -> {
				deletedCoreComponentIds.add(relationship.getId());
				stageRemove(relationship);
			});
		
		// deleting core components should delete all referring members as well
		ExpressionBuilder referringMembersQuery = Expressions.bool()
				.should(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(deletedCoreComponentIds))
				.should(SnomedRefSetMemberIndexEntry.Expressions.refsetIds(deletedConceptIds));
		
		SnomedRf2Headers.MEMBER_FIELDS_WITH_COMPONENT_ID.forEach(memberField -> {
			referringMembersQuery.should(Expressions.matchAny(memberField, deletedCoreComponentIds));
		});
		
		Query.select(SnomedRefSetMemberIndexEntry.class)
			.where(referringMembersQuery.build())
			.limit(pageSize)
			.build()
			.stream(searcher)
			.flatMap(Hits::stream)
			.forEachOrdered(member -> stageRemove(member));
	}
}
