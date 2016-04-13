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
package com.b2international.collections.set;

import java.util.Set;

import com.b2international.collections.ByteCollection;
import com.b2international.collections.LongCollection;
import com.google.common.hash.HashFunction;

/**
 * @since 4.7
 */
public final class FastUtilPrimitiveSetFactory implements PrimitiveSetFactory {
	
	@Override
	public ByteSet newByteOpenHashSet(ByteCollection source) {
		return ByteOpenHashSetWrapper.create(source);
	}

	@Override
	public IntSet newIntOpenHashSet() {
		return IntOpenHashSetWrapper.create();
	}

	@Override
	public IntSet newIntOpenHashSet(int expectedSize) {
		return IntOpenHashSetWrapper.create(expectedSize);
	}

	@Override
	public LongSet newLongOpenHashSet() {
		return LongOpenHashSetWrapper.create();
	}

	@Override
	public LongSet newLongOpenHashSet(HashFunction hashFunction) {
		return LongOpenHashSetWrapper.create(hashFunction);
	}

	@Override
	public LongSet newLongOpenHashSet(int expectedSize) {
		return LongOpenHashSetWrapper.create(expectedSize);
	}

	@Override
	public LongSet newLongOpenHashSet(int expectedSize, double fillFactor) {
		return LongOpenHashSetWrapper.create(expectedSize, fillFactor);
	}

	@Override
	public LongSet newLongOpenHashSet(long[] source) {
		return LongOpenHashSetWrapper.create(source);
	}

	@Override
	public LongSet newLongOpenHashSet(LongCollection source) {
		return LongOpenHashSetWrapper.create(source);
	}

	@Override
	public Set<Long> newLongSetToSetAdapter(LongSet source) {
		throw new UnsupportedOperationException();
	}

	@Override
	public LongSet newUnmodifiableLongSet(LongSet source) {
		throw new UnsupportedOperationException();
	}
}
