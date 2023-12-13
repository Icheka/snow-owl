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
package com.b2international.index.mapping;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.b2international.index.Analyzers;
import com.b2international.index.Normalizers;

/**
 * @since 8.0
 */
@Documented
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface FieldAlias {

	String name();
	
	FieldAliasType type();
	
	Analyzers analyzer() default Analyzers.DEFAULT;
	
	Analyzers searchAnalyzer() default Analyzers.INDEX;
	
	Normalizers normalizer() default Normalizers.NONE;
	
	boolean index() default true;
	
	public enum FieldAliasType {
		KEYWORD, 
		TEXT
	}
	
}
