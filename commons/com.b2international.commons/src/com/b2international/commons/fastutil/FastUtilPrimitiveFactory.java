/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons.fastutil;

import java.util.Collection;
import java.util.Set;

import com.b2international.commons.collections.primitive.ByteCollection;
import com.b2international.commons.collections.primitive.IntCollection;
import com.b2international.commons.collections.primitive.LongCollection;
import com.b2international.commons.collections.primitive.PrimitiveFactory;
import com.b2international.commons.collections.primitive.list.ByteList;
import com.b2international.commons.collections.primitive.list.IntDeque;
import com.b2international.commons.collections.primitive.list.IntList;
import com.b2international.commons.collections.primitive.list.LongDeque;
import com.b2international.commons.collections.primitive.list.LongList;
import com.b2international.commons.collections.primitive.map.ByteKeyLongMap;
import com.b2international.commons.collections.primitive.map.ByteKeyMap;
import com.b2international.commons.collections.primitive.map.ByteValueMap;
import com.b2international.commons.collections.primitive.map.IntKeyMap;
import com.b2international.commons.collections.primitive.map.LongKeyFloatMap;
import com.b2international.commons.collections.primitive.map.LongKeyIntMap;
import com.b2international.commons.collections.primitive.map.LongKeyLongMap;
import com.b2international.commons.collections.primitive.map.LongKeyMap;
import com.b2international.commons.collections.primitive.map.LongValueMap;
import com.b2international.commons.collections.primitive.set.ByteSet;
import com.b2international.commons.collections.primitive.set.IntSet;
import com.b2international.commons.collections.primitive.set.LongSet;
import com.google.common.hash.HashFunction;

/**
 * @since 4.6
 */
public class FastUtilPrimitiveFactory implements PrimitiveFactory {

	@Override
	public ByteList newByteArrayList(byte[] source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteList newByteArrayList(ByteCollection source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteList newByteArrayList(int expectedSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteKeyLongMap newByteKeyLongOpenHashMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V> ByteKeyMap<V> newByteKeyOpenHashMap(int expectedSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteSet newByteOpenHashSet(ByteCollection source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntDeque newIntArrayDeque(int[] source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntList newIntArrayList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntList newIntArrayList(int[] source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntSet newIntBitSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntSet newIntBitSet(int expectedSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntSet newIntBitSet(int[] source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntSet newIntBitSet(IntCollection source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V> IntKeyMap<V> newIntKeyOpenHashMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V> IntKeyMap<V> newIntKeyOpenHashMap(int expectedSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntSet newIntOpenHashSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntSet newIntOpenHashSet(int expectedSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongDeque newLongArrayDeque() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongList newLongArrayList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongList newLongArrayList(int expectedSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongList newLongArrayList(long[] source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongList newLongArrayList(LongCollection source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongSet newLongChainedHashSet(long[] source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Long> newLongCollectionToCollectionAdapter(LongCollection source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongKeyFloatMap newLongKeyFloatOpenHashMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongKeyFloatMap newLongKeyFloatOpenHashMap(int expectedSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongKeyIntMap newLongKeyIntOpenHashMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongKeyIntMap newLongKeyIntOpenHashMap(int expectedSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongKeyLongMap newLongKeyLongOpenHashMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongKeyLongMap newLongKeyLongOpenHashMap(int expectedSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V> LongKeyMap<V> newLongKeyOpenHashMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V> LongKeyMap<V> newLongKeyOpenHashMap(HashFunction hashFunction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V> LongKeyMap<V> newLongKeyOpenHashMap(int expectedSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongSet newLongOpenHashSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongSet newLongOpenHashSet(HashFunction hashFunction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongSet newLongOpenHashSet(int expectedSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongSet newLongOpenHashSet(int expectedSize, double fillFactor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongSet newLongOpenHashSet(long[] source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongSet newLongOpenHashSet(LongCollection source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Long> newLongSetToSetAdapter(LongSet source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K> ByteValueMap<K> newObjectKeyByteOpenHashMap(int expectedSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K> LongValueMap<K> newObjectKeyLongOpenHashMap(int expectedSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntList newUnmodifiableIntList(IntList source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntSet newUnmodifiableIntSet(IntSet source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongSet newUnmodifiableLongSet(LongSet source) {
		// TODO Auto-generated method stub
		return null;
	}

}
