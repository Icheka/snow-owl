/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.b2international.index.migrate.Migrator;

/**
 * @since 4.7
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Doc {

	String type() default "";

	boolean nested() default true;

	/**
	 * Whether to index or not the type annotated with this annotation. Root objects always indexed and have index fields, nested objects how ever can
	 * set this value to either <code>true</code> or <code>false</code> depending on whether they would like to support search or not.
	 * 
	 * @return
	 */
	boolean index() default true;

	/**
	 * @return an array of field names that should be used for computing the revision hash, if empty no hash will be computed and the component will
	 *         not have property level change history
	 */
	String[] revisionHash() default {};
	
	/**
	 * Schema change migrators that require to be run when a model change is detected between the current mapping `_meta` value and the last {@link Migrator#version()} added to this list.
	 * 
	 * NOTE: only index level types can be migrated to a newer version via this API
	 * 
	 * @return an array of schema migrators assigned to this type or an empty array if no migration is required.
	 */
	Migrator[] migrators() default {};
	
}
