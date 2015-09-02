/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index.refset;

import java.io.Serializable;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.index.IndexAdapterBase;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;

public class SnomedRefSetMemberIndexQueryAdapter extends IndexAdapterBase<SnomedRefSetMemberIndexEntry> implements Serializable {

	private static final long serialVersionUID = 3095110912042606627L;

	private String refSetId;
	private boolean excludeInactive;
	
	protected SnomedRefSetMemberIndexQueryAdapter() {
		super("");
	}
		
	public SnomedRefSetMemberIndexQueryAdapter(final String refSetId, final String searchString) {
		this(refSetId, searchString, true);
	}
	
	public SnomedRefSetMemberIndexQueryAdapter(final String refSetId, final String searchString, final boolean excludeInactive) {
		super(searchString);
		this.refSetId = refSetId;
		this.excludeInactive = excludeInactive;
	}

	@Override
	public SnomedRefSetMemberIndexEntry buildSearchResult(final Document doc, final IBranchPath branchPath, final float score) {
		return SnomedRefSetMemberIndexEntry.create(doc, branchPath);
	}
	
	@Override
	protected Query buildQuery() throws ParseException {
		
		final BooleanQuery main = new BooleanQuery();
		
		final BooleanQuery refSetIdQuery = new BooleanQuery();
		refSetIdQuery.add(SnomedMappings.newQuery().memberRefSetId(refSetId).matchAll(), Occur.MUST);
		main.add(refSetIdQuery, Occur.MUST);
		
		if (!StringUtils.isEmpty(searchString)) {
			final BooleanQuery fieldQuery = new BooleanQuery();
			addTermPrefixClause(fieldQuery, Mappings.label().fieldName(), searchString.toLowerCase());
			//added by endre, see issue #242
			fieldQuery.add(SnomedMappings.newQuery().memberReferencedComponentId(searchString.toLowerCase()).matchAll(), Occur.SHOULD);
			main.add(fieldQuery, Occur.MUST);
		}
		
		if (excludeInactive) {
			main.add(SnomedMappings.newQuery().active().matchAll(), Occur.MUST);
		}
		
		return main;
	}
}