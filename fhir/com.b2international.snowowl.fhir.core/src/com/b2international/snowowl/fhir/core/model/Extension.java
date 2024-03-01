/*
 * Copyright 2011-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.fhir.core.model;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.codesystems.ExtensionType;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.serialization.ExtensionSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 
 * FHIR Extension content
 * 
 * @see <a href="https://www.hl7.org/fhir/extensibility.html#Extension">FHIR:Foundation:Extensibility</a>
 * @since 6.3
 */
@JsonSerialize(using=ExtensionSerializer.class)
@JsonDeserialize(using = ExtensionDeserializer.class)
public abstract class Extension<T> extends Element {
	
	//Identifies the meaning of the extension
	//TODO: validator needs to be called.
	@Valid
	@NotNull
	protected final Uri url;
	
	protected final T value;
	
	protected Extension(final String id, final List<Extension<?>> extensions,
			final Uri url, final T value) {
		
		super(id, extensions);
		this.url = url;
		this.value = value;
	}
	
	/**
	 * Return the type of this extension (valueX)
	 * @return
	 */
	@JsonIgnore
	public abstract ExtensionType getExtensionType();
	
	public Uri getUrl() {
		return url;
	}
	
	//Ignored as it is serialized via the custom serializer on the class
	@JsonIgnore 
	public T getValue() {
		return value;
	}
	
	//An extension SHALL have either a value (i.e. a value[x] element) or sub-extensions, but not both.
	@AssertTrue(message = "An extension SHALL have either a value (i.e. a value[x] element) or sub-extensions")
	private boolean isValid() {
		if (value != null && getExtensions() != null && !getExtensions().isEmpty()) {
			return false;
		}

		if (value == null && (getExtensions() == null || getExtensions().isEmpty())) {
			return false;
		}
		return true;
	}
	
	
	public static abstract class Builder<B extends Builder<B, ET, T>, ET extends Extension<T>, T> extends Element.Builder<B, ET> {

		protected Uri url;

		protected T value;
		
		public B url(Uri url) {
			this.url = url;
			return getSelf();
		}
		
		public B url(String url) {
			this.url = new Uri(url);
			return getSelf();
		}
		
		public B value(final T value) {
			this.value = value;
			return getSelf();
		}
		
	}
}
