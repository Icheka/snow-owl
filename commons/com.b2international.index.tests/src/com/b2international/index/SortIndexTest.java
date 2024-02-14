/*
 * Copyright 2011-2024 B2i Healthcare, https://b2ihealthcare.com
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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newTreeSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertArrayEquals;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

import org.apache.commons.lang3.RandomStringUtils;
import org.elasticsearch.common.UUIDs;
import org.junit.Test;

import com.b2international.index.Fixtures.Data;
import com.b2international.index.Fixtures.MultipleNestedData;
import com.b2international.index.Fixtures.NestedData;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.index.query.SortBy.Order;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @since 5.4
 */
public class SortIndexTest extends BaseIndexTest {

	private static final int NUM_DOCS = 1000;

	@Override
	protected Collection<Class<?>> getTypes() {
		return List.<Class<?>>of(Data.class, MultipleNestedData.class);
	}

	@Test
	public void sortStringField() throws Exception {
		final TreeSet<String> orderedItems = newTreeSet();
		final List<Data> documents = new ArrayList<>(NUM_DOCS);

		for (int i = 0; i < NUM_DOCS; i++) {
			String item = null;
			while (item == null || orderedItems.contains(item)) {
				item = RandomStringUtils.randomAlphabetic(10);
			}
			orderedItems.add(item);
			
			final Data data = new Data(Integer.toString(i));
			data.setField1(item);
			documents.add(data);
		}

		indexDocuments(documents);
		
		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("field1", Order.ASC))
				.build();

		checkDocumentOrder(ascendingQuery, data -> data.getField1(), orderedItems, String.class);
		
		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("field1", Order.DESC))
				.build();

		checkDocumentOrder(descendingQuery, data -> data.getField1(), orderedItems.descendingSet(), String.class);
	}

	@Test
	public void sortAnalyzedField() throws Exception {
		final TreeSet<String> orderedItems = newTreeSet();
		final List<Data> documents = new ArrayList<>(NUM_DOCS);

		for (int i = 0; i < NUM_DOCS; i++) {
			String item = null;
			while (item == null || orderedItems.contains(item)) {
				item = RandomStringUtils.randomAlphabetic(10);
			}
			orderedItems.add(item);
			
			final Data data = new Data(Integer.toString(i));
			data.setAnalyzedField(item);
			documents.add(data);
		}
		
		indexDocuments(documents);

		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("analyzedField.exact", Order.ASC))
				.build();
		
		checkDocumentOrder(ascendingQuery, data -> data.getAnalyzedField(), orderedItems, String.class);

		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("analyzedField.exact", Order.DESC))
				.build();

		checkDocumentOrder(descendingQuery, data -> data.getAnalyzedField(), orderedItems.descendingSet(), String.class);
	}

	@Test
	public void sortBigDecimalField() throws Exception {
		final PrimitiveIterator.OfDouble doubleIterator = new Random().doubles().iterator();
		final TreeSet<BigDecimal> orderedItems = newTreeSet();
		final List<Data> documents = new ArrayList<>(NUM_DOCS);

		for (int i = 0; i < NUM_DOCS; i++) {
			BigDecimal item = null;
			while (item == null || orderedItems.contains(item)) {
				item = BigDecimal.valueOf(doubleIterator.nextDouble()); 
			}
			orderedItems.add(item);
			
			final Data data = new Data(Integer.toString(i));
			data.setBigDecimalField(item);
			documents.add(data);
		}
		
		indexDocuments(documents);

		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("bigDecimalField", Order.ASC))
				.build();

		checkDocumentOrder(ascendingQuery, data -> data.getBigDecimalField(), orderedItems, BigDecimal.class);

		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("bigDecimalField", Order.DESC))
				.build();

		checkDocumentOrder(descendingQuery, data -> data.getBigDecimalField(), orderedItems.descendingSet(), BigDecimal.class);
	}

	@Test
	public void sortFloatField() throws Exception {
		final PrimitiveIterator.OfDouble doubleIterator = new Random().doubles().iterator();
		final TreeSet<Float> orderedItems = newTreeSet();
		final List<Data> documents = new ArrayList<>(NUM_DOCS);

		for (int i = 0; i < NUM_DOCS; i++) {
			float item = 0.0f;
			while (item == 0.0f || orderedItems.contains(item)) {
				item = (float) doubleIterator.nextDouble(); 
			}
			orderedItems.add(item);
			
			final Data data = new Data(Integer.toString(i));
			data.setFloatField(item);
			documents.add(data);
		}
		
		indexDocuments(documents);

		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("floatField", Order.ASC))
				.build();

		checkDocumentOrder(ascendingQuery, data -> data.getFloatField(), orderedItems, Float.class);

		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("floatField", Order.DESC))
				.build();

		checkDocumentOrder(descendingQuery, data -> data.getFloatField(), orderedItems.descendingSet(), Float.class);
	}

	@Test
	public void sortLongField() throws Exception {
		final PrimitiveIterator.OfLong longIterator = new Random().longs().iterator();
		final TreeSet<Long> orderedItems = newTreeSet(); 
		final List<Data> documents = new ArrayList<>(NUM_DOCS);
		
		for (int i = 0; i < NUM_DOCS; i++) {
			long item = 0L;
			while (item == 0L || orderedItems.contains(item)) {
				item = longIterator.nextLong(); 
			}
			orderedItems.add(item);
			
			final Data data = new Data(Integer.toString(i));
			data.setLongField(item);
			documents.add(data);
		}
		
		indexDocuments(documents);

		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("longField", Order.ASC))
				.build();

		checkDocumentOrder(ascendingQuery, data -> data.getLongField(), orderedItems, Long.class);
		
		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("longField", Order.DESC))
				.build();

		checkDocumentOrder(descendingQuery, data -> data.getLongField(), orderedItems.descendingSet(), Long.class);
	}

	@Test
	public void sortIntField() throws Exception {
		final PrimitiveIterator.OfInt intIterator = new Random().ints().iterator();
		final TreeSet<Integer> orderedItems = newTreeSet(); 
		final List<Data> documents = new ArrayList<>(NUM_DOCS);
		
		for (int i = 0; i < NUM_DOCS; i++) {
			int item = 0;
			while (item == 0 || orderedItems.contains(item)) {
				item = intIterator.nextInt(); 
			}
			orderedItems.add(item);
			
			final Data data = new Data(Integer.toString(i));
			data.setIntField(item);
			documents.add(data);
		}

		indexDocuments(documents);
		
		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("intField", Order.ASC))
				.build();
		
		checkDocumentOrder(ascendingQuery, data -> data.getIntField(), orderedItems, Integer.class);
		
		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("intField", Order.DESC))
				.build();
		
		checkDocumentOrder(descendingQuery, data -> data.getIntField(), orderedItems.descendingSet(), Integer.class);
	}

	@Test
	public void sortShortField() throws Exception {
		final PrimitiveIterator.OfInt intIterator = new Random().ints().iterator();
		final TreeSet<Short> orderedItems = newTreeSet(); 
		final List<Data> documents = new ArrayList<>(NUM_DOCS);
		
		for (short i = 0; i < NUM_DOCS; i++) {
			short item = 0;
			while (item == 0 || orderedItems.contains(item)) {
				item = (short) intIterator.nextInt(); 
			}
			orderedItems.add(item);
			
			final Data data = new Data(Integer.toString(i));
			data.setShortField(item);
			documents.add(data);
		}
		
		indexDocuments(documents);

		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("shortField", Order.ASC))
				.build();

		checkDocumentOrder(ascendingQuery, data -> data.getShortField(), orderedItems, Short.class);

		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("shortField", Order.DESC))
				.build();

		checkDocumentOrder(descendingQuery, data -> data.getShortField(), orderedItems.descendingSet(), Short.class);
	}

	@Test
	public void sortScore() throws Exception {
		final List<String> orderedKeys = newArrayList(); 
		final List<Data> documents = new ArrayList<>(NUM_DOCS);
		
		for (int i = 0; i < NUM_DOCS; i++) {
			String key = Integer.toString(NUM_DOCS - i);
			orderedKeys.add(key);
			final Data data = new Data(key);
			data.setFloatField(NUM_DOCS - i);
			documents.add(data);
		}
		
		indexDocuments(documents);
		
		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.scriptScore(Expressions.matchAll(), Data.Scripts.FIELD_SCORE))
				.limit(NUM_DOCS)
				.sortBy(SortBy.SCORE)
				.build();
		
		checkDocumentOrder(descendingQuery, data -> data.getId(), Sets.newLinkedHashSet(orderedKeys), String.class);
		
		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.scriptScore(Expressions.matchAll(), Data.Scripts.FIELD_SCORE))
				.limit(NUM_DOCS)
				.sortBy(SortBy.field(SortBy.FIELD_SCORE, Order.ASC))
				.build();
		
		checkDocumentOrder(ascendingQuery, data -> data.getId(), Sets.newLinkedHashSet(Lists.reverse(orderedKeys)), String.class);
	}
	
	@Test
	public void sortNestedFieldWithScores() throws Exception {
		indexDocuments(
			new MultipleNestedData(KEY1, List.of(new NestedData("unused", "abdominal knee pain"))),
			new MultipleNestedData(KEY2, List.of(new NestedData("unused", "knee pain"))),
			new MultipleNestedData(UUIDs.randomBase64UUID(), List.of(new NestedData("unused", "pain")))
		);
		
		Hits<MultipleNestedData> hits = search(Query.select(MultipleNestedData.class)
			.where(Expressions.nestedMatch("nestedDatas", Expressions.matchTextAll("analyzedField.text", "knee pain")))
			.build());
		
		assertThat(hits)
			.extracting(m -> m.id)
			.containsOnly(KEY1, KEY2);
		assertThat(hits)
			.extracting(m -> m.score)
			.allSatisfy(score -> assertThat(score).isGreaterThan(0.0f));
	}

	private <T> void checkDocumentOrder(Query<Data> query, Function<? super Data, T> hitFunction, Set<T> keySet, Class<T> clazz) {
		final Hits<Data> hits = search(query);
		final T[] actual = FluentIterable.from(hits).transform(hitFunction::apply).toArray(clazz);
		final T[] expected = Iterables.toArray(keySet, clazz);
		assertArrayEquals(expected, actual);
	}	
}
