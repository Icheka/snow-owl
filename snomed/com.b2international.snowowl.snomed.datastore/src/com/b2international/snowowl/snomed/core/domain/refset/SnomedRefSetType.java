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
package com.b2international.snowowl.snomed.core.domain.refset;

/**
 * Enumeration for SNOMED CT reference set types. 
 */
public enum SnomedRefSetType {
	/*
	 * XXX: The order of literals should not be relied upon, but in case some parts
	 * of the code still do this, add new literals to the end.
	 */
	SIMPLE,
	SIMPLE_MAP,
	LANGUAGE,
	ATTRIBUTE_VALUE,
	QUERY,
	COMPLEX_MAP,
	DESCRIPTION_TYPE,
	CONCRETE_DATA_TYPE,
	ASSOCIATION, 
	MODULE_DEPENDENCY,
	EXTENDED_MAP, 
	SIMPLE_MAP_WITH_DESCRIPTION,
	OWL_AXIOM,
	OWL_ONTOLOGY, 
	MRCM_DOMAIN,
	MRCM_ATTRIBUTE_DOMAIN,
	MRCM_ATTRIBUTE_RANGE,
	MRCM_MODULE_SCOPE,
	ANNOTATION,
	COMPLEX_BLOCK_MAP,
	SIMPLE_MAP_TO;

	/**
	 * Returns the SnomedRefSetType literal with the specified name.
	 * <p>
	 * A less strict version of {@link #valueOf(String)} that returns <code>null</code> 
	 * if the input string does not map to an enum literal.
	 */
	public static SnomedRefSetType getByName(final String name) {
		for (final SnomedRefSetType result : values()) {
			if (result.name().equals(name)) {
				return result;
			}
		}

		return null;
	}
}
