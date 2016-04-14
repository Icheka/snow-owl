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
package com.b2international.collections.list;

import java.util.NoSuchElementException;

import com.b2international.collections.longs.LongDeque;

public class LongArrayDequeWrapper extends LongArrayListWrapper implements LongDeque {

	public static LongDeque create() {
		return new LongArrayDequeWrapper(new it.unimi.dsi.fastutil.longs.LongArrayList());
	}

	private LongArrayDequeWrapper(it.unimi.dsi.fastutil.longs.LongArrayList delegate) {
		super(delegate);
	}

	@Override
	public void addLast(long value) {
		delegate.add(delegate.size(), value);
	}

	@Override
	public long getLast() {
		if (!delegate.isEmpty()) {
			return delegate.getLong(delegate.size() - 1);
		} else {
			throw new NoSuchElementException();
		}
	}

	@Override
	public long removeLast() {
		if (!delegate.isEmpty()) {
			return delegate.removeLong(delegate.size() - 1);
		} else {
			throw new NoSuchElementException();
		}
	}
}
