/*
 * Copyright 2011-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.io.commit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.commit.CommitInfo;
import com.b2international.snowowl.core.commit.CommitInfos;
import com.b2international.snowowl.core.context.ResourceRepositoryRequestBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.test.commons.Services;
import com.google.common.collect.Iterables;

/**
 * @since 5.2
 */
public class SnomedCommitApiTest {
	
	private static final String USER_ID = "system";
	private static final String TOOLING_ID = SnomedTerminologyComponentConstants.TOOLING_ID;
	
	private IEventBus bus;
	
	@Before
	public void setup() {
		this.bus = Services.bus();
	}
	
	@Test(expected = NotFoundException.class)
	public void getNonExistentCommitInfo() {
		RepositoryRequests
			.commitInfos()
			.prepareGetCommitInfo(UUID.randomUUID().toString())
			.build(TOOLING_ID)
			.execute(bus)
			.getSync();
	}
	
	@Test
	public void getCommitInfo() {
		final String shortName = UUID.randomUUID().toString();
		final String comment = "Code system for commit info 1";
		
		createCodeSystem(shortName, comment);
		final String id = getCommitInfoByComment(comment).getId();
		
		Request<RepositoryContext, CommitInfo> req = RepositoryRequests
				.commitInfos()
				.prepareGetCommitInfo(id)
				.build();
		
		CommitInfo commitInfo = new ResourceRepositoryRequestBuilder<CommitInfo>() {
			@Override
			public Request<RepositoryContext, CommitInfo> build() {
				return req;
			}
		 }.buildAsync().execute(bus).getSync();
		
		assertEquals(id, commitInfo.getId());
		assertEquals(comment, commitInfo.getComment());
		assertEquals(Branch.MAIN_PATH, commitInfo.getBranch());
		assertEquals(USER_ID, commitInfo.getAuthor());
	}
	
	@Test
	public void searchCommitInfoByComment() {
		final String shortName = UUID.randomUUID().toString();
		final String comment = "Code system for commit info 2";
		
		createCodeSystem(shortName, comment);
		final CommitInfo commitInfo = getCommitInfoByComment(comment);
		
		assertEquals(comment, commitInfo.getComment());
	}
	
	@Test
	public void searchCommitInfoByUserId() {
		final String shortName = UUID.randomUUID().toString();
		final String comment = "Code system for commit info 3";
		final String userId = "commitInfo";
		
		createCodeSystem(shortName, comment, userId);
		
		Request<RepositoryContext, CommitInfos> req = RepositoryRequests
				.commitInfos()
				.prepareSearchCommitInfo()
				.filterByAuthor(userId)
				.build();
		
		final CommitInfos commitInfos = new ResourceRepositoryRequestBuilder<CommitInfos>() {
			@Override
			public Request<RepositoryContext, CommitInfos> build() {
				return req;
			}
		 }.buildAsync().execute(bus).getSync();
		
		assertEquals(commitInfos.getTotal(), 1);
		
		final CommitInfo commitInfo = Iterables.getOnlyElement(commitInfos);
		assertEquals(userId, commitInfo.getAuthor());
	}
		
	@Test
	public void searchCommitInfoByTimestamp() {
		final String shortName = UUID.randomUUID().toString();
		final String comment = "Code system for commit info 5";
		
		createCodeSystem(shortName, comment);
		final CommitInfo commitInfo = getCommitInfoByComment(comment);
		
		Request<RepositoryContext, CommitInfos> req = RepositoryRequests
				.commitInfos()
				.prepareSearchCommitInfo()
				.filterByTimestamp(commitInfo.getTimestamp())
				.build();
		
		final CommitInfos commitInfos = new ResourceRepositoryRequestBuilder<CommitInfos>() {
				@Override
				public Request<RepositoryContext, CommitInfos> build() {
					return req;
				}
			 }.buildAsync().execute(bus).getSync();
		
		assertTrue(commitInfos.getTotal() == 1);
		assertEquals(commitInfo.getTimestamp(), Iterables.getOnlyElement(commitInfos.getItems()).getTimestamp());
	}
	
	private void createCodeSystem(final String codeSystemId, final String comment) {
		createCodeSystem(codeSystemId, comment, USER_ID);
	}
	
	private void createCodeSystem(final String codeSystemId, final String comment, final String userId) {
		var oid = UUID.randomUUID().toString();
		CodeSystemRequests.prepareNewCodeSystem()
			.setId(codeSystemId)
			.setOid(oid)
			.setUrl(String.format("http://snomed.info/sct/%s", codeSystemId))
			.setTitle(String.format("%s - %s", codeSystemId, oid))
			.setLanguage("en")
			.setDescription("citation")
			.setToolingId(TOOLING_ID)
			.build(userId, comment)
			.execute(bus)
			.getSync();
	}
	
	private CommitInfo getCommitInfoByComment(final String comment) {
		Request<RepositoryContext, CommitInfos> req = RepositoryRequests
				.commitInfos()
				.prepareSearchCommitInfo()
				.filterByComment(comment)
				.build();
		
		final CommitInfos commitInfos = new ResourceRepositoryRequestBuilder<CommitInfos>() {

			@Override
			public Request<RepositoryContext, CommitInfos> build() {
				return req;
			}
			
		 }
		 .buildAsync()
		 .execute(bus)
		 .getSync();
		 
		assertEquals(commitInfos.getTotal(), 1);
		
		return Iterables.getOnlyElement(commitInfos);
	}
	
}
