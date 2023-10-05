/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.converter;

import java.util.List;

import org.linuxforhealth.fhir.model.r4b.resource.ValueSet;
import org.linuxforhealth.fhir.model.r4b.resource.Parameters;
import org.linuxforhealth.fhir.model.r4b.type.*;
import org.linuxforhealth.fhir.model.r4b.type.Boolean;
import org.linuxforhealth.fhir.model.r4b.type.Integer;
import org.linuxforhealth.fhir.model.r4b.type.String;
import org.linuxforhealth.fhir.model.r4b.type.code.FilterOperator;
import org.linuxforhealth.fhir.model.r4b.type.code.PublicationStatus;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.ValidateCodeResult;
import com.b2international.snowowl.fhir.core.model.valueset.ValidateCodeRequest;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.*;

/**
 * @since 9.0
 */
public class ValueSetConverter_43 extends AbstractConverter_43 implements ValueSetConverter<ValueSet, Parameters> {

	public static final ValueSetConverter<ValueSet, Parameters> INSTANCE = new ValueSetConverter_43();
	
	private ValueSetConverter_43() {
		super();
	}
	
	@Override
	public ValueSet fromInternal(com.b2international.snowowl.fhir.core.model.valueset.ValueSet valueSet) {
		if (valueSet == null) {
			return null;
		}
		
		ValueSet.Builder builder = ValueSet.builder();
		
		fromInternalResource(builder, valueSet);
		fromInternalDomainResource(builder, valueSet);
		
		// ValueSet properties

		builder.url(fromInternal(valueSet.getUrl()));
		
		var identifiers = valueSet.getIdentifiers();
		if (!CompareUtils.isEmpty(identifiers)) {
			for (var identifier : identifiers) {
				if (identifier != null) {
					builder.identifier(fromInternal(identifier));
				}
			}
		}
		
		builder.version(valueSet.getVersion());
		builder.name(valueSet.getName());
		builder.title(valueSet.getTitle());
		
		Code status = fromInternal(valueSet.getStatus());
		if (status != null) {
			builder.status(PublicationStatus.of(status.getValue()));
		}
		
		builder.experimental(valueSet.getExperimental());
		builder.date(fromInternal(valueSet.getDate()));
		builder.publisher(valueSet.getPublisher());
		
		var contacts = valueSet.getContacts();
		if (!CompareUtils.isEmpty(contacts)) {
			for (var contact : contacts) {
				if (contact != null) {
					builder.contact(fromInternal(contact));
				}
			}
		}
		
		builder.description(fromInternalToMarkdown(valueSet.getDescription()));
		// "useContext" is not converted
		// "jurisdiction" is not converted
		builder.purpose(fromInternalToMarkdown(valueSet.getPurpose()));
		builder.copyright(fromInternalToMarkdown(valueSet.getCopyright()));
		
		builder.immutable(valueSet.getImmutable());
		builder.compose(fromInternal(valueSet.getCompose()));
		builder.expansion(fromInternal(valueSet.getExpansion()));
		// "scope" is not converted
		
		return builder.build();
	}

	// Elements

	private ValueSet.Compose fromInternal(com.b2international.snowowl.fhir.core.model.valueset.Compose compose) {
		if (compose == null) {
			return null;
		}
		
		ValueSet.Compose.Builder builder = ValueSet.Compose.builder();
		
		// "lockedDate" is not converted
		// "inactive" is not converted
		var includes = compose.getIncludes();
		if (!CompareUtils.isEmpty(includes)) {
			for (var include : includes) {
				if (include != null) {
					builder.include(fromInternal(include));
				}
			}
		}
		
		var excludes = compose.getExcludes();
		if (!CompareUtils.isEmpty(excludes)) {
			for (var exclude : excludes) {
				if (exclude != null) {
					builder.exclude(fromInternal(exclude));
				}
			}
		}
		
		return builder.build();
	}
	
	private ValueSet.Compose.Include fromInternal(com.b2international.snowowl.fhir.core.model.valueset.Include include) {
		if (include == null) {
			return null;
		}
		
		ValueSet.Compose.Include.Builder builder = ValueSet.Compose.Include.builder();
		
		builder.system(fromInternal(include.getSystem()));
		builder.version(include.getVersion());
		
		var concepts = include.getConcepts();
		if (!CompareUtils.isEmpty(concepts)) {
			for (var concept : concepts) {
				if (concept != null) {
					builder.concept(fromInternal(concept));
				}
			}
		}
		
		var filters = include.getFilters();
		if (!CompareUtils.isEmpty(filters)) {
			for (var filter : filters) {
				if (filter != null) {
					builder.filter(fromInternal(filter));
				}
			}
		}
		
		var valueSets = include.getValueSets();
		if (!CompareUtils.isEmpty(valueSets)) {
			for (var valueSet : valueSets) {
				if (valueSet != null) {
					builder.valueSet(fromInternalToCanonical(valueSet));
				}
			}
		}
		
		return builder.build();
	}
	
	
	private ValueSet.Compose.Include.Concept fromInternal(com.b2international.snowowl.fhir.core.model.valueset.ValueSetConcept concept) {
		if (concept == null) {
			return null;
		}
		
		ValueSet.Compose.Include.Concept.Builder builder = ValueSet.Compose.Include.Concept.builder();
		
		builder.code(fromInternal(concept.getCode()));
		builder.display(concept.getDisplay());
		
		var designations = concept.getDesignations();
		if (!CompareUtils.isEmpty(designations)) {
			for (var designation : designations) {
				if (designation != null) {
					builder.designation(fromInternal(designation));
				}
			}
		}

		return builder.build();
	}

	private ValueSet.Compose.Include.Concept.Designation fromInternal(com.b2international.snowowl.fhir.core.model.Designation designation) {
		if (designation == null) {
			return null;
		}
		
		ValueSet.Compose.Include.Concept.Designation.Builder builder = ValueSet.Compose.Include.Concept.Designation.builder();
		
		builder.language(fromInternal(designation.getLanguageCode()));
		builder.use(fromInternal(designation.getUse()));
		builder.value(designation.getValue());
		
		return builder.build();
	}
	
	private ValueSet.Compose.Include.Filter fromInternal(com.b2international.snowowl.fhir.core.model.valueset.ValueSetFilter filter) {
		if (filter == null) {
			return null;
		}

		ValueSet.Compose.Include.Filter.Builder builder = ValueSet.Compose.Include.Filter.builder();
		
		builder.property(fromInternal(filter.getProperty()));
		
		Code op = fromInternal(filter.getOperator());
		if (op != null) {
			builder.op(FilterOperator.of(op.getValue()));
		}
		
		builder.value(filter.getValue());
		
		return builder.build();
	}
	
	private ValueSet.Expansion fromInternal(com.b2international.snowowl.fhir.core.model.valueset.expansion.Expansion expansion) {
		if (expansion == null) {
			return null;
		}
		
		ValueSet.Expansion.Builder builder = ValueSet.Expansion.builder();
		
		builder.identifier(fromInternal(expansion.getIdentifier()));
		// there is no "next" property in R4B unfortunately
		builder.timestamp(fromInternal(expansion.getTimestamp()));
		builder.total(expansion.getTotal());
		// "offset" is not converted
		
		var parameters = expansion.getParameters();
		if (CompareUtils.isEmpty(parameters)) {
			for (var parameter : parameters) {
				if (parameter != null) {
					builder.parameter(fromInternal(parameter));
				}
			}
		}
		
		var containedConcepts = expansion.getContains();
		if (CompareUtils.isEmpty(containedConcepts)) {
			for (var containedConcept : containedConcepts) {
				if (containedConcept != null) {
					builder.contains(fromInternal(containedConcept));
				}
			}
		}
		
		return builder.build();
	}

	private ValueSet.Expansion.Parameter fromInternal(com.b2international.snowowl.fhir.core.model.valueset.expansion.Parameter<?> parameter) {
		if (parameter == null) {
			return null;
		}
		
		ValueSet.Expansion.Parameter.Builder builder = ValueSet.Expansion.Parameter.builder();

		builder.name(parameter.getName());
		
		switch (parameter.getType()) {
			case STRING:
				builder.value((java.lang.String) parameter.getValue());
				break;
			case BOOLEAN:
				builder.value((java.lang.Boolean) parameter.getValue());
				break;
			case INTEGER:
				builder.value((java.lang.Integer) parameter.getValue());
				break;
			case DECIMAL:
				builder.value(fromInternal((java.lang.Float) parameter.getValue()));
				break;
			case URI:
				builder.value(fromInternal((com.b2international.snowowl.fhir.core.model.dt.Uri) parameter.getValue()));
				break;
			case CODE:
				builder.value(fromInternal((com.b2international.snowowl.fhir.core.model.dt.Code) parameter.getValue()));
				break;
			case DATETIME:
				builder.value(fromInternal((java.util.Date) parameter.getValue()));
				break;
			default:
				throw new IllegalArgumentException("Unexpected expansion parameter type '" + parameter.getType() + "'.");
		}
		
		return builder.build();
	}
	
	private ValueSet.Expansion.Contains fromInternal(com.b2international.snowowl.fhir.core.model.valueset.expansion.Contains contains) {
		if (contains == null) {
			return null;
		}
		
		ValueSet.Expansion.Contains.Builder builder = ValueSet.Expansion.Contains.builder();

		builder.system(fromInternal(contains.getSystem()));
		builder._abstract(contains.getIsAbstract());
		builder.inactive(contains.getInactive());
		builder.version(contains.getVersion());
		builder.code(fromInternal(contains.getCode()));
		builder.display(contains.getDisplay());
		
		var designations = contains.getDesignations();
		if (!CompareUtils.isEmpty(designations)) {
			for (var designation : designations) {
				if (designation != null) {
					builder.designation(fromInternal(designation));
				}
			}
		}
		
		var nestedConcepts = contains.getContains();
		if (CompareUtils.isEmpty(nestedConcepts)) {
			for (var nestedConcept : nestedConcepts) {
				if (nestedConcept != null) {
					builder.contains(fromInternal(nestedConcept));
				}
			}
		}

		return builder.build();
	}

	@Override
	public com.b2international.snowowl.fhir.core.model.valueset.ValueSet toInternal(ValueSet valueSet) {
		if (valueSet == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.valueset.ValueSet.builder();
		
		toInternalResource(builder, valueSet);
		toInternalDomainResource(builder, valueSet);
		
		// ValueSet properties
		builder.url(toInternal(valueSet.getUrl()));
		
		List<Identifier> identifiers = valueSet.getIdentifier();
		for (Identifier identifier : identifiers) {
			builder.addIdentifier(toInternal(identifier));
		}
		
		builder.version(toInternal(valueSet.getVersion()));
		builder.name(toInternal(valueSet.getName()));
		builder.title(toInternal(valueSet.getTitle()));
		
		var status = toInternal(valueSet.getStatus());
		if (status != null) {
			builder.status(com.b2international.snowowl.fhir.core.codesystems.PublicationStatus.getByCodeValue(status.getCodeValue()));
		}
		
		builder.experimental(toInternal(valueSet.getExperimental()));
		builder.date(toInternal(valueSet.getDate()));
		builder.publisher(toInternal(valueSet.getPublisher()));
		
		List<ContactDetail> contacts = valueSet.getContact();
		for (ContactDetail contact : contacts) {
			builder.addContact(toInternal(contact));
		}
		
		builder.description(toInternal(valueSet.getDescription()));
		// "useContext" is not converted
		// "jurisdiction" is not converted
		builder.purpose(toInternal(valueSet.getPurpose()));
		builder.copyright(toInternal(valueSet.getCopyright()));
		
		builder.immutable(toInternal(valueSet.getImmutable()));
		builder.compose(toInternal(valueSet.getCompose()));
		builder.expansion(toInternal(valueSet.getExpansion()));
		// "scope" is not converted
		
		return builder.build();
	}

	// Elements
	
	private com.b2international.snowowl.fhir.core.model.valueset.Compose toInternal(ValueSet.Compose compose) {
		if (compose == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.valueset.Compose.builder();
		
		// "lockedDate" is not converted
		// "inactive" is not converted
		List<ValueSet.Compose.Include> includes = compose.getInclude();
		for (ValueSet.Compose.Include include : includes) {
			builder.addInclude(toInternal(include));
		}
		
		List<ValueSet.Compose.Include> excludes = compose.getExclude();
		for (ValueSet.Compose.Include exclude : excludes) {
			builder.addExclude(toInternal(exclude));
		}
		
		return builder.build();
	}

	private com.b2international.snowowl.fhir.core.model.valueset.Include toInternal(ValueSet.Compose.Include include) {
		if (include == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.valueset.Include.builder();
		
		builder.system(toInternalString(include.getSystem()));
		builder.version(toInternal(include.getVersion()));
		
		List<ValueSet.Compose.Include.Concept> concepts = include.getConcept();
		for (ValueSet.Compose.Include.Concept concept : concepts) {
			builder.addConcept(toInternal(concept));
		}
		
		List<ValueSet.Compose.Include.Filter> filters = include.getFilter();
		for (ValueSet.Compose.Include.Filter filter : filters) {
			builder.addFilters(toInternal(filter));
		}
		
		List<Canonical> valueSets = include.getValueSet();
		for (Canonical valueSet : valueSets) {
			builder.addValueSet(toInternalString(valueSet));
		}
		
		return builder.build();
	}

	private com.b2international.snowowl.fhir.core.model.valueset.ValueSetConcept toInternal(ValueSet.Compose.Include.Concept concept) {
		if (concept == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.valueset.ValueSetConcept.builder();
		
		builder.code(toInternalString(concept.getCode()));
		builder.display(toInternal(concept.getDisplay()));
		
		List<ValueSet.Compose.Include.Concept.Designation> designations = concept.getDesignation();
		for (ValueSet.Compose.Include.Concept.Designation designation : designations) {
			builder.addDesignation(toInternal(designation));
		}
	
		return builder.build();
	}

	private com.b2international.snowowl.fhir.core.model.Designation toInternal(ValueSet.Compose.Include.Concept.Designation designation) {
		if (designation == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.Designation.builder();
		
		builder.language(toInternal(designation.getLanguage()));
		builder.use(toInternal(designation.getUse()));
		// "additionalUse" is not converted (new in R5)
		builder.value(toInternal(designation.getValue()));
		
		return builder.build();
	}

	private com.b2international.snowowl.fhir.core.model.valueset.ValueSetFilter toInternal(ValueSet.Compose.Include.Filter filter) {
		if (filter == null) {
			return null;
		}
	
		var builder = com.b2international.snowowl.fhir.core.model.valueset.ValueSetFilter.builder();
		
		builder.property(toInternalString(filter.getProperty()));
		
		var op = toInternal(filter.getOp());
		if (op != null) {
			builder.operator(com.b2international.snowowl.fhir.core.codesystems.FilterOperator.forValue(op.getCodeValue()));
		}
		
		builder.value(toInternal(filter.getValue()));
		
		return builder.build();
	}

	private com.b2international.snowowl.fhir.core.model.valueset.expansion.Expansion toInternal(ValueSet.Expansion expansion) {
		if (expansion == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.valueset.expansion.Expansion.builder();
		
		builder.identifier(toInternalString(expansion.getIdentifier()));
		// there is no "next" URI in R4B
		builder.timestamp(toInternal(expansion.getTimestamp()));
		builder.total(toInternal(expansion.getTotal()));
		// "offset" is not converted
		
		List<ValueSet.Expansion.Parameter> parameters = expansion.getParameter();
		for (ValueSet.Expansion.Parameter parameter : parameters) {
			builder.addParameter(toInternal(parameter));
		}
		
		List<ValueSet.Expansion.Contains> containedConcepts = expansion.getContains();
		for (ValueSet.Expansion.Contains containedConcept : containedConcepts) {
			builder.addContains(toInternal(containedConcept));
		}
		
		return builder.build();
	}

	private com.b2international.snowowl.fhir.core.model.valueset.expansion.Parameter<?> toInternal(ValueSet.Expansion.Parameter parameter) {
		if (parameter == null) {
			return null;
		}
		
		final com.b2international.snowowl.fhir.core.model.valueset.expansion.Parameter.Builder<?, ?, ?> builder;
	
		Element value = parameter.getValue();
		if (value instanceof String string) {
			builder = StringParameter.builder().value(toInternal(string));
		} else if (value instanceof Boolean b) {
			builder = BooleanParameter.builder().value(toInternal(b));
		} else if (value instanceof Integer integer) {
			builder = IntegerParameter.builder().value(toInternal(integer));
		} else if (value instanceof Decimal decimal) {
			builder = DecimalParameter.builder().value(toInternalAsDouble(decimal));
		} else if (value instanceof Uri uri) {
			builder = UriParameter.builder().value(toInternal(uri));
		} else if (value instanceof Code code) {
			builder = CodeParameter.builder().value(toInternal(code));
		} else if (value instanceof DateTime dateTime) {
			builder = DateTimeParameter.builder().value(toInternal(dateTime));
		} else {
			throw new IllegalArgumentException("Unexpected expansion parameter type '" + value.getClass().getSimpleName() + "'.");			
		}
		
		builder.name(toInternal(parameter.getName()));

		return builder.build();
	}

	private com.b2international.snowowl.fhir.core.model.valueset.expansion.Contains toInternal(ValueSet.Expansion.Contains contains) {
		if (contains == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.valueset.expansion.Contains.builder();
	
		builder.system(toInternal(contains.getSystem()));
		builder.isAbstract(toInternal(contains.getAbstract()));
		builder.inactive(toInternal(contains.getInactive()));
		builder.version(toInternal(contains.getVersion()));
		builder.code(toInternal(contains.getCode()));
		builder.display(toInternal(contains.getDisplay()));
		
		List<ValueSet.Compose.Include.Concept.Designation> designations = contains.getDesignation();
		for (ValueSet.Compose.Include.Concept.Designation designation : designations) {
			builder.addDesignation(toInternal(designation));
		}
		
		var nestedConcepts = contains.getContains();
		if (CompareUtils.isEmpty(nestedConcepts)) {
			for (var nestedConcept : nestedConcepts) {
				if (nestedConcept != null) {
					builder.addContains(toInternal(nestedConcept));
				}
			}
		}
	
		return builder.build();
	}

	@Override
	public Parameters fromValidateCodeResult(ValidateCodeResult validateCodeResult) {
		return super.fromValidateCodeResult(validateCodeResult);
	}

	@Override
	public ValidateCodeRequest toValidateCodeRequest(Parameters parameters) {
		if (parameters == null) {
			return null;
		}
		
		var builder = ValidateCodeRequest.builder();
		
		List<Parameters.Parameter> parameterElements = parameters.getParameter();
		for (Parameters.Parameter parameter : parameterElements) {
			java.lang.String parameterName = toInternal(parameter.getName());
			
			switch (parameterName) {
				case "url":
					var url = toInternal(parameter.getValue().as(Uri.class));
					if (url != null) {
						builder.url(url.getUriValue());
					}
					break;
					
				case "context":
					var context = toInternal(parameter.getValue().as(Uri.class));
					if (context != null) {
						builder.context(context);
					}
					break;
					
				case "valueSet":
					throw new BadRequestException("Inline input parameter 'valueSet' is not supported.");
					
				case "valueSetVersion":
					var valueSetVersion = toInternal(parameter.getValue().as(String.class));
					if (!StringUtils.isEmpty(valueSetVersion)) {
						builder.valueSetVersion(valueSetVersion);
					}
					break;
	
				case "code":
					var code = toInternal(parameter.getValue().as(Code.class));
					if (code != null) {
						builder.code(code.getCodeValue());
					}
					break;
	
				case "system":
					var system = toInternal(parameter.getValue().as(Uri.class));
					if (system != null) {
						builder.system(system.getUriValue());
					}
					break;
	
				case "systemVersion":
					var systemVersion = toInternal(parameter.getValue().as(String.class));
					if (!StringUtils.isEmpty(systemVersion)) {
						builder.systemVersion(systemVersion);
					}
					break;
	
				case "display":
					var display = toInternal(parameter.getValue().as(String.class));
					if (!StringUtils.isEmpty(display)) {
						builder.display(display);
					}
					break;
					
				case "coding":
					var coding = toInternal(parameter.getValue().as(Coding.class));
					if (coding != null) {
						builder.coding(coding);
					}
					break;
					
				case "codeableConcept":
					var codeableConcept = toInternal(parameter.getValue().as(CodeableConcept.class));
					if (codeableConcept != null) {
						builder.codeableConcept(codeableConcept);
					}
					break;
					
				case "date":
					DateTime dateTime = parameter.getValue().as(DateTime.class);
					if (dateTime != null) {
						builder.date(DateTime.PARSER_FORMATTER.format(dateTime.getValue()));
					}
					break;
	
				case "abstract":
					var isAbstract = toInternal(parameter.getValue().as(Boolean.class));
					if (isAbstract != null) {
						builder.isAbstract(isAbstract);
					}
					break;
					
				case "displayLanguage":
					var displayLanguage = toInternal(parameter.getValue().as(Code.class));
					if (displayLanguage != null) {
						builder.displayLanguage(displayLanguage);
					}
					break;
	
				default:
					throw new IllegalStateException("Unexpected in parameter '" + parameterName + "'.");
			}
		}
		
		return builder.build();
	}
}
