/*
 * Copyright 2011-2022 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.commons.collections;

import java.util.*;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

/**
 * Yet another collection utility class.
 */
public abstract class Collections3 {

	public static <T> Set<T> toImmutableSet(Iterable<T> values) {
		return values != null ? ImmutableSet.copyOf(values) : Collections.emptySet();
	}
	
	public static <T> List<T> toImmutableList(Iterable<T> values) {
		return values != null ? ImmutableList.copyOf(values) : Collections.emptyList();
	}

	public static <T> Set<T> toImmutableSet(T[] values) {
		return values != null ? ImmutableSet.copyOf(values) : Collections.emptySet();
	}
	
	public static <T> List<T> toImmutableList(T[] values) {
		return values != null ? ImmutableList.copyOf(values) : Collections.emptyList();
	}
	
	public static <T extends Comparable<T>> SortedSet<T> toImmutableSortedSet(Iterable<T> values) {
		return values != null ? ImmutableSortedSet.copyOf(values) : Collections.emptySortedSet();
	}
	
	public static <T extends Comparable<T>> SortedSet<T> toImmutableSortedSet(T[] values) {
		return values != null ? ImmutableSortedSet.copyOf(values) : Collections.emptySortedSet();
	}
	
	/**
	 * Returns <code>true</code> if the two {@link Collection}s are equal, ignoring element order and count. This method is essentially the same as
	 * {@link Set#equals(Object)}.
	 * 
	 * @param <T>
	 * @param left
	 * @param right
	 * @return
	 * @see Set#equals(Object)
	 */
	public static <T> boolean equals(Collection<T> left, Collection<T> right) {
		if (left == right) return true;
		if (left == null || right == null) return false;
		if (left.size() != right.size()) return false;
		return left.containsAll(right);
	}
	
	private Collections3() { /*suppress instantiation*/ }

	/**
	 * Computes the intersection of the two {@link Collection} instances.
	 * 
	 * @param left
	 * @param right
	 * @return a non-<code>null</code> value if any of the input arguments is not <code>null</code>, otherwise <code>null</code>
	 * @see Sets#intersection(Set, Set)
	 */
	public static Set<String> intersection(Collection<String> left, Collection<String> right) {
		if (left == null && right == null) {
			return null;
		} else if (left == null && right != null) {
			return toImmutableSet(right);
		} else if (left != null && right == null) {
			return toImmutableSet(left);
		} else {
			return Sets.intersection(toImmutableSet(left), toImmutableSet(right));
		}
	}
}