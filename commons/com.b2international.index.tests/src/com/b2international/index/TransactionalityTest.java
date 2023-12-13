/*
 * Copyright 2011-2021 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.index;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collection;

import org.junit.Test;

import com.b2international.index.Fixtures.Data;
import com.google.common.collect.ImmutableList;

/**
 * @since 4.7
 */
public class TransactionalityTest extends BaseIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(Data.class);
	}
	
	@Test
	public void uncommittedTransactionShouldNotChangeTheIndex() throws Exception {
		final Data data = new Data(KEY1);
		index().write(index -> {
			index.put(data);
			return null;
		});
		// after the failed transaction data should not be in the index
		assertNull(getDocument(Data.class, KEY1));
	}
	
	@Test
	public void tx1CommitShouldNotCommitTx2Changes() throws Exception {
		final Data data1 = new Data(KEY1);
		final Data data2 = new Data(KEY2);
		Writer tx1 = client().writer(); 
		Writer tx2 = client().writer();
		tx1.put(data1);
		tx2.put(data2);
		tx1.commit();
		
		// at this point tx2 content should not be visible
		Searcher searcher = client().searcher();
		assertEquals(data1, searcher.get(Data.class, KEY1));
		assertNull(searcher.get(Data.class, KEY2));
		
		tx2.commit();
		
		assertEquals(data1, searcher.get(Data.class, KEY1));
		assertEquals(data2, searcher.get(Data.class, KEY2));
	}
	
}
