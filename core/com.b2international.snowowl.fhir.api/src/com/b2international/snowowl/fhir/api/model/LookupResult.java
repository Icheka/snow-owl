/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.api.model;

import java.util.Collection;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.core.annotation.Order;

import com.b2international.snowowl.fhir.api.model.serialization.SerializableParameter;
import com.google.common.collect.Lists;

/**
 * 
 * @since 6.3
 */
//@JsonSubTypes
public class LookupResult extends FhirModel {
	
	//A display name for the code system (1..1)
	@Order(value=1)
	@NotEmpty
	private String name;
	
	//The version that these details are based on (0..1)
	@Order(value=2)
	private String version;
	
	//The preferred display for this concept (1..1)
	@Order(value=3)
	private String display;
	
	//Additional representations for this concept (0..*)
	@Order(value=4)
	private Collection<Designation> designations = Lists.newArrayList();   
	
	/*
	 * One or more properties that contain additional information about the code, 
	 * including status. For complex terminologies (e.g. SNOMED CT, LOINC, medications), these properties serve to decompose the code
	 * 0..*
	 */
	@Order(value=5)
	private Collection<Property> properties = Lists.newArrayList();
	
	private LookupResult(final String name, 
			final String version, 
			final String display, 
			Collection<Designation> designations,
			Collection<Property> properties) {
		
		this.name = name;
		this.version = version;
		this.display = display;
		this.designations = designations;
		this.properties = properties;
	}
	
	/*
	public void add(SerializableParameter parameter) {
		parameters.add(parameter);
	}

	public void addAll(Collection<SerializableParameter> fhirParameters) {
		this.parameters.addAll(fhirParameters);
	}

	public Collection<SerializableParameter> getParameters() {
		return parameters;
	}
	*/
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private String name;
		private String version;
		private String display;
		
		private Collection<Designation> designations = Lists.newArrayList();
		
		private Collection<Property> properties = Lists.newArrayList();;

		public Builder name(final String name) {
			this.name = name;
			return this;
		}
		
		public Builder use(String version) {
			this.version = version;
			return this;
		}
		
		public Builder value(String display) {
			this.display = display;
			return this;
		}
		
		public Builder addDesignation(Designation designation) {
			designations.add(designation);
			return this;
		}

		public Builder addProperty(Property property) {
			properties.add(property);
			return this;
		}
		public LookupResult build() {
			return new LookupResult(name, version, display, designations, properties);
		}
	}
	
	@Override
	protected Collection<SerializableParameter> getCollectionParameters(Object value) throws Exception {
		
		Collection<SerializableParameter> collectionParameters = Lists.newArrayList();

		@SuppressWarnings("rawtypes")
		Collection values = (Collection) value;
		
		for (Object object : values) {
			if (object instanceof Designation) {
				Collection<SerializableParameter> designationParams = ((Designation) object).toParameters();
				SerializableParameter fhirParam = new SerializableParameter("designation", "part", designationParams);
				collectionParameters.add(fhirParam);
			} else if (object instanceof Property) {
				Collection<SerializableParameter> propertyParams = ((Property) object).toParameters();
				SerializableParameter fhirParam = new SerializableParameter("property", "part", propertyParams);
				collectionParameters.add(fhirParam);
			}
		}
		return collectionParameters;
	}
	
}
