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
package com.b2international.commons.options;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;

import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @since 4.5
 */
public class HashMapOptions extends HashMap<String, Object> implements Options {

	private static final long serialVersionUID = 4190786291142214160L;

	public HashMapOptions() {
		this(5);
	}
	
	public HashMapOptions(int initialCapacity) {
		super(initialCapacity);
	}
	
	public HashMapOptions(Map<String, Object> options) {
		super(options);
	}
	
	@Override
	public boolean containsKey(Object key) {
		if (key instanceof Enum) {
			return super.containsKey(((Enum<?>) key).name());
		} else {
			return super.containsKey(key);
		}
	}
	
	@Override
	public final Object get(String key) {
		return super.get(key);
	}
	
	@Override
	public Object get(Enum<?> key) {
		return get(key.name());
	}
	
	@Override
	public final boolean getBoolean(final String key) {
		final Boolean value = get(key, Boolean.class);
		return value == null ? false : value;
	}
	
	@Override
	public boolean getBoolean(Enum<?> key) {
		return getBoolean(key.name());
	}
	
	@Override
	public final String getString(final String key) {
		return get(key, String.class);
	}
	
	@Override
	public String getString(Enum<?> key) {
		return getString(key.name());
	}
	
	@Override
	@Nullable
	public final <T> T get(@Nullable final String key, @Nullable final Class<T> expectedType) throws IllegalArgumentException {
		if (key != null && expectedType != null) {
			Object value = get(key);
			if (value != null) {
				if (value instanceof String && expectedType != String.class) {
					if (Boolean.class == expectedType) {
						value = Boolean.parseBoolean((String) value);
					} else if (Short.class == expectedType) {
						value = Short.parseShort((String) value);
					} else if (Integer.class == expectedType) {
						value = Integer.parseInt((String) value);
					} else if (Long.class == expectedType) {
						value = Long.parseLong((String) value);
					}
				}
				if (expectedType.isInstance(value)) {
					return expectedType.cast(value);
				} else {
					throw new IllegalArgumentException(String.format(
							"Expected type '%s' is not valid for the value '%s(%s)' returned for the key '%s'",
							expectedType.getSimpleName(), value, value.getClass().getSimpleName(), key));
				}
			}
		}
		return null;
	}
	
	@Override
	public <T> T get(Enum<?> key, Class<T> expectedType) {
		return get(key.name(), expectedType);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public final <T> Collection<T> getCollection(String key, Class<T> type) {
		final Object value = get(key);
		if (type.isInstance(value) && !Collection.class.isAssignableFrom(value.getClass())) {
			return Collections.singleton(type.cast(value));
		} else {
			final Collection<Object> collection = get(key, Collection.class);
			final Object first = collection != null ? Iterables.getFirst(collection, null) : null;
			if (first != null) {
				if (type.isInstance(first)) {
					return (Collection<T>) collection;
				}
				throw new IllegalArgumentException(String.format("The elements (%s) in the collection are not the instance of the given type (%s)", first.getClass(), type));
			}
			return emptySet();
		}
	}
	
	@Override
	public <T> Collection<T> getCollection(Enum<?> key, Class<T> type) {
		return getCollection(key.name(), type);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public final <T> Set<T> getSet(String key, Class<T> type) {
		final Object value = get(key);
		if (type.isInstance(value) && !Set.class.isAssignableFrom(value.getClass())) {
			return Collections.singleton(type.cast(value));
		} else {
			final Set<Object> set = get(key, Set.class);
			final Object first = set != null ? Iterables.getFirst(set, null) : null;
			if (first != null) {
				if (type.isInstance(first)) {
					return (Set<T>) set;
				}
				throw new IllegalArgumentException(String.format("The elements (%s) in the List are not the instance of the given type (%s)", first.getClass(), type));
			}
			return emptySet();
		}
	}
	
	@Override
	public <T> Set<T> getSet(Enum<?> key, Class<T> type) {
		return getSet(key.name(), type);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public final <T> List<T> getList(String key, Class<T> type) {
		final Object value = get(key);
		if (type.isInstance(value) && !List.class.isAssignableFrom(value.getClass())) {
			return Collections.singletonList(type.cast(value));
		} else {
			final List<Object> list = get(key, List.class);
			final Object first = list != null ? Iterables.getFirst(list, null) : null;
			if (first != null) {
				if (type.isInstance(first)) {
					return (List<T>) list;
				}
				throw new IllegalArgumentException(String.format("The elements (%s) in the List are not the instance of the given type (%s)", first.getClass(), type));
			}
			return emptyList();
		}
	}
	
	@Override
	public <T> List<T> getList(Enum<?> key, Class<T> type) {
		return getList(key.name(), type);
	}
	
	@Override
	public final Options getOptions(String key) {
		return containsKey(key) ? get(key, Options.class) : OptionsBuilder.newBuilder().build();
	}
	
	@Override
	public Options getOptions(Enum<?> key) {
		return getOptions(key.name());
	}
	
	@Nonnull
	protected final Iterable<String> toImmutableStringList(@Nullable final Iterable<Object> elements) {
		if (elements == null) {
			return Collections.emptyList();
		}
		return Lists.transform(ImmutableList.copyOf(elements), String::valueOf);
	}

}
