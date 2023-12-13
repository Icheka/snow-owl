/*
 * Copyright 2011-2018 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.cis.gen;

import java.util.Set;

import com.b2international.snowowl.core.terminology.ComponentCategory;

/**
 * Determines an algorithm on how to generate a valid SNOMED CT Item identifier without extension namespace-identifier, partition-identifier and
 * check-digit parts.
 * 
 * @since 4.0
 */
public interface ItemIdGenerationStrategy {

	/**
	 * Generates a brand new SNOMED CT Item Identifier.
	 * 
	 * @param namespace the namespace identifier
	 * @param category the terminology independent category of the component
	 * @param quantity the number of requested item IDs
	 * @param attempt the current attempt to generate quantity amount of item IDs
	 *   
	 * @return the generated item identifiers, never <code>null</code>
	 */
	Set<String> generateItemIds(String namespace, ComponentCategory category, int quantity, int attempt);

	/**
	 * {@link ItemIdGenerationStrategy} implementation which generates a random Item identifier, independent of the given namespace and category.
	 */
	ItemIdGenerationStrategy RANDOM = new RandomItemIdGenerationStrategy();
}
