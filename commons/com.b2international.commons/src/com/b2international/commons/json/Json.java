/*
 * Copyright 2021-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.commons.json;

import java.util.List;
import java.util.Map;

import com.b2international.commons.CompareUtils;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * @since 7.14.1
 */
public final class Json extends ForwardingMap<String, Object> {

	@JsonUnwrapped
	private final Map<String, Object> source;
	
	public Json(Map<String, Object> source) {
		super();
		this.source = source == null ? Map.of() : source;
	}
	
	@Override
	public String toString() {
		return source.toString();
	}
	
	@Override
	protected Map<String, Object> delegate() {
		return source;
	}
	
	public Json with(String property, Object value) {
		final Map<String, Object> newJson = Maps.newLinkedHashMap(source);
		newJson.put(property, value);
		return new Json(newJson);
	}
	
	public Json without(String property) {
		if (Strings.isNullOrEmpty(property) || !containsKey(property)) {
			return this;
		}
		final Map<String, Object> newJson = Maps.newLinkedHashMap(source);
		newJson.remove(property);
		return new Json(newJson);
	}
	
	public Json with(Map<String, Object> object) {
		if (object == null) {
			return this;
		}
		final Map<String, Object> newJson = Maps.newLinkedHashMap(source);
		newJson.putAll(object);
		return new Json(newJson);
	}
	
	public Json merge(Map<String, Object> object) {
		if (object == null) {
			return this;
		}
		final Map<String, Object> newJson = deepMerge(source, object);
		return new Json(newJson);
	}
	
	private static <K, V> Map<K, V> deepMerge(Map<K, V> target, Map<K, V> source) {
		final Map<K, V> updatedTarget = Maps.newLinkedHashMap(target);
		source.forEach((key, value) -> {
			updatedTarget.merge(key, value, (oldValue, newValue) -> {
				if (oldValue instanceof Map<?, ?> && newValue instanceof Map<?, ?>) {
					// perform deep merge on two nested value maps
					return (V) deepMerge((Map) oldValue, (Map) newValue);
				} else {
					// simply override
					return newValue;
				}
			});
		});
		return updatedTarget;
	}

	@SafeVarargs
	public static <T> List<T> array(T...values) {
		return ImmutableList.copyOf(values);
	}
	
	/**
	 * Construct a new {@link Json} object which is basically a Map of String keys to any Object value. <code>null</code> values are ignored and the
	 * associated keys will not be registered in the resulting {@link Json} object.
	 * 
	 * @param properties - key-value property pairs or <code>null</code>
	 * @return a {@link Json} object instance, never <code>null</code>
	 */
	public static Json object(Object...properties) {
		final ImmutableMap.Builder<String, Object> props = ImmutableMap.builder();
		if (properties != null) {
			Preconditions.checkArgument(properties.length % 2 == 0, "Invalid number of property-value pairs specified. Got: %s", properties.length);
			for (int i = 0; i < properties.length / 2; i++) {
				final int propIdx = i * 2;
				final Object key = properties[propIdx];
				Preconditions.checkArgument(key instanceof String, "Property key at index '%s' is not a String object", propIdx);
				final Object value = properties[propIdx + 1];
				if (value != null) {
					props.put((String) key, value);
				}
			}
		}
		return new Json(props.build());
	}
	
	@SafeVarargs
	public static Json assign(Map<String, Object>...sources) {
		if (CompareUtils.isEmpty(sources)) {
			return new Json(Map.of());
		} else {
			Json result = new Json(sources[0]);
			if (sources.length > 1) {
				for (int i = 1; i < sources.length; i++) {
					result = result.with(sources[i]);
				}
			}
			return result;
		}
	}
	
	@SafeVarargs
	public static Json merge(Map<String, Object>...sources) {
		if (CompareUtils.isEmpty(sources)) {
			return new Json(Map.of());
		} else {
			Json result = new Json(sources[0]);
			if (sources.length > 1) {
				for (int i = 1; i < sources.length; i++) {
					result = result.merge(sources[i]);
				}
			}
			return result;
		}
	}

}
