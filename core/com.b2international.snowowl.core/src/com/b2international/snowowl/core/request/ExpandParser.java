/*
 * Copyright 2011-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.request;

import java.io.IOException;
import java.util.Map;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.options.Options;
import com.b2international.commons.options.OptionsBuilder;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.5
 */
public class ExpandParser {

	private static final JsonFactory JSON_FACTORY = new JsonFactory().enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
	private static final TypeReference<Map<String, Object>> MAP_TYPE_REF = new TypeReference<Map<String, Object>>() { };
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public static Options parse(final String expand) {
		
		boolean inQuote = false;
		boolean backslash = false;
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < expand.length(); i++) {
			final char c = expand.charAt(i);
			
			switch (c) {
				case '\\':
					backslash = !backslash;
					builder.append(c);
					break;
			
				case '\"':
					if (!backslash) {
						inQuote = !inQuote;
					}
					
					builder.append(c);
					
					backslash = false;
					break;
				
				case '(':
					if (!inQuote) {
						builder.append(":{");
					} else {
						builder.append(c);
					}
					
					backslash = false;
					break;
					
				case ')':
					if (!inQuote) {
						builder.append("}");
					} else {
						builder.append(c);
					}
					
					backslash = false;
					break;
					
				default:
					backslash = false;
					builder.append(c);
					break;
			}
		}
		
		String jsonizedOptionPart = String.format("{%s}", builder.toString());

		try {
			JsonParser parser = JSON_FACTORY.createParser(jsonizedOptionPart);
			parser.setCodec(OBJECT_MAPPER);
			Map<String, Object> source = parser.readValueAs(MAP_TYPE_REF);
			return Options.from(source);
		} catch (JsonParseException e) {
			throw new BadRequestException("Expansion parameter %s is malformed.", expand, e);
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Caught I/O exception while reading expansion parameters.", e);
		}
	}
}
