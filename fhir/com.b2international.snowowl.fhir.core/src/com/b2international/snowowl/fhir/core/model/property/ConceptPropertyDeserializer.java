/*
 * Copyright 2021 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.fhir.core.model.property;

import java.io.IOException;
import java.util.Iterator;

import com.b2international.snowowl.fhir.core.codesystems.PropertyType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.collect.Sets;

/**
 * Polymorphic deserializer to handle different types of {@link ConceptProperty}s.
 * @see {@link ConceptProperty}
 * @since 8.0.0
 */
public class ConceptPropertyDeserializer extends StdDeserializer<ConceptProperty<?>> {

	private static final long serialVersionUID = 1L;
	
	private static final String VALUE_PREFIX = "value";

	protected ConceptPropertyDeserializer() {
		super(ConceptProperty.class);
	}
	
	@Override
	public ConceptProperty<?> deserialize(JsonParser parser, DeserializationContext arg1)
			throws IOException, JsonProcessingException {
		
		TreeNode node = parser.readValueAsTree();
		ObjectCodec objectCodec = parser.getCodec();
		
		Iterator<String> fieldNames = node.fieldNames();
		
		PropertyType[] propertyTypes = PropertyType.values();
		PropertyType propertyType = null;
		
		while (fieldNames.hasNext()) {
			String fieldName = (String) fieldNames.next();
			if (fieldName.startsWith(VALUE_PREFIX)) {
				
				String type = fieldName.replace(VALUE_PREFIX, "");
				propertyType = Sets.newHashSet(propertyTypes).stream()
					.filter(t -> t.getDisplayName().equalsIgnoreCase(type))
					.findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown property type '" + fieldName + "'."));
				
				break;
			}
		}

		if (propertyType == null) {
			throw new IllegalArgumentException("Invalid property type with null value.");
		}
		
		switch (propertyType) {
		case CODING:
			return objectCodec.treeToValue(node, CodingConceptProperty.class);
		case CODE:
			return objectCodec.treeToValue(node, CodeConceptProperty.class);
		case DATETIME:
			return objectCodec.treeToValue(node, DateTimeConceptProperty.class);
		case STRING:
			return objectCodec.treeToValue(node, StringConceptProperty.class);
		case BOOLEAN:
			return objectCodec.treeToValue(node, BooleanConceptProperty.class);
		case DECIMAL:
			return objectCodec.treeToValue(node, DecimalConceptProperty.class);
		case INTEGER:
			return objectCodec.treeToValue(node, IntegerConceptProperty.class);
		default:
			throw new IllegalArgumentException("Unsupported property type '" + propertyType + "'.");
		}
	}
	
	
}