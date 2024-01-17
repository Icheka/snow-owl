/*
 * Copyright 2020-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.index.query;

/**
 * @since 7.10
 */
public final class RegexpPredicate extends SingleArgumentPredicate<String> {

	private final boolean caseInsensitive;
	
	RegexpPredicate(String field, String regexp, boolean caseInsensitive) {
		super(field, regexp);
		this.caseInsensitive = caseInsensitive;
	}
	
	public boolean isCaseInsensitive() {
		return caseInsensitive;
	}
	
	@Override
	public String toString() {
		return String.format("%s = regexp(%s)%s", getField(), getArgument(), isCaseInsensitive() ? "[ci]" : "[cs]");
	}

}
