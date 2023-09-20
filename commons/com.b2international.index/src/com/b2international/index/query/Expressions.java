/*
 * Copyright 2011-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.index.query.TextPredicate.MatchType;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;

/**
 * @since 4.7
 */
public class Expressions {
	
	// special 
	private static final String DYNAMIC_VALUE_DELIMITER = "#";
	
	// special in: syntax support
	private static final String IN_CLAUSE = "in:";
	private static final Splitter COMMA_SPLITTER = Splitter.on(",");
	private static final Joiner COMMA_JOINER = Joiner.on(",");

	// special range: syntax support 
	private static final String RANGE_CLAUSE = "range:";
	private static final String RANGE_EXPRESSION_DELIMITER = "..";
	private static final Splitter RANGE_SPLITTER = Splitter.on(RANGE_EXPRESSION_DELIMITER);
	private static final Joiner RANGE_JOINER = Joiner.on(RANGE_EXPRESSION_DELIMITER);

	public static final class ExpressionBuilder extends AbstractExpressionBuilder<ExpressionBuilder> {
		
		@Override
		protected ExpressionBuilder getSelf() {
			return this;
		}

	}

	
	/**
	 * @return a new boolean query expression builder
	 * @deprecated - use {@link Expressions#bool()} instead
	 */
	public static ExpressionBuilder builder() {
		return bool();
	}
	
	/**
	 * @return a new boolean query expression builder
	 */
	public static ExpressionBuilder bool() {
		return new ExpressionBuilder();
	}
	
	public static Expression exists(final String field) {
		return matchRange(field, (String) null, (String) null);
	}
	
	public static Expression nestedMatch(final String path, Expression expression) {
		final List<String> pathSegments = Lists.reverse(Splitter.on(".").splitToList(path));
		Expression previous = expression;
		for (String segment : pathSegments) {
			previous = new NestedPredicate(segment, previous);
		}
		return previous;
	}
	
	public static Expression prefixMatch(final String field, final String prefix) {
		return new PrefixPredicate(field, List.of(prefix));
	}
	
	public static Expression prefixMatch(final String field, final Iterable<String> prefixes) {
		return new PrefixPredicate(field, prefixes);
	}
	
	public static Expression exactMatch(String field, String value) {
		return new StringPredicate(field, value);
	}
	
	public static Expression exactMatch(String field, Long value) {
		return new LongPredicate(field, value);
	}
	
	public static Expression match(String field, Boolean value) {
		return new BooleanPredicate(field, value);
	}
	
	public static Expression match(String field, Integer value) {
		return new IntPredicate(field, value);
	}
	
	public static Expression match(String field, BigDecimal value) {
		return new DecimalPredicate(field, value);
	}
	
	public static Expression match(String field, Double value) {
		return new DoublePredicate(field, value);
	}

	public static Expression matchAll() {
		return MatchAll.INSTANCE;
	}
	
	public static Expression matchNone() {
		return MatchNone.INSTANCE;
	}

	public static Expression hasParent(Class<?> parentType, Expression expression) {
		return new HasParentPredicate(parentType, expression);
	}

	public static Expression matchRange(String fieldName, Object from, Object to) {
		return matchRange(fieldName, from, to, true, true);
	}
	
	public static <T> Expression matchRange(String fieldName, T from, T to, boolean includeFrom, boolean includeTo) {
		return new RangePredicate<>(fieldName, from, to, includeFrom, includeTo);
	}
	
	public static Expression matchAnyInt(String field, Iterable<Integer> values) {
		if (Iterables.size(values) == 1) {
			return new IntPredicate(field, Iterables.getFirst(values, null));
		} else {
			return new IntSetPredicate(field, values);
		}
	}
	
	public static Expression matchAnyEnum(String field, Iterable<? extends Enum<?>> values) {
		final Iterable<String> names = Iterables.transform(values, Enum::name);
		return matchAny(field, names);
	}
	
	public static Expression matchAny(String field, Iterable<String> values) {
		if (Iterables.size(values) == 1) {
			return new StringPredicate(field, Iterables.getFirst(values, null));
		} else {
			return new StringSetPredicate(field, values);
		}
	}
	
	public static Expression matchAnyLong(String field, Iterable<Long> values) {
		if (Iterables.size(values) == 1) {
			return new LongPredicate(field, Iterables.getFirst(values, null));
		} else {
			return new LongSetPredicate(field, values);
		}
	}
	
	public static Expression matchAnyDecimal(String field, Iterable<BigDecimal> values) {
		if (Iterables.size(values) == 1) {
			return new DecimalPredicate(field, Iterables.getFirst(values, null));
		} else {
			return new DecimalSetPredicate(field, values);
		}
	}
	
	public static Expression matchAnyDouble(String field, Iterable<Double> values) {
		if (Iterables.size(values) == 1) {
			return new DoublePredicate(field, Iterables.getFirst(values, null));
		} else {
			return new DoubleSetPredicate(field, values);
		}
	}

	public static Expression boost(Expression expression, float boost) {
		return new BoostPredicate(expression, boost);
	}
	
	public static TextPredicate matchTextAll(String field, String term) {
		return new TextPredicate(field, term, MatchType.ALL);
	}

	public static TextPredicate matchTextAny(String field, String term) {
		return new TextPredicate(field, term, MatchType.ANY);
	}
	
	public static TextPredicate matchTextAny(String field, String term, int minShouldMatch) {
		return new TextPredicate(field, term, MatchType.ANY, minShouldMatch);
	}
	
	/**
	 * <p>
	 * Returns a text predicate that uses <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.10/query-dsl-match-bool-prefix-query.html">match boolean prefix query</a>.
	 * It will match results by constructing a bool query with an AND operator from the analyzed terms,
	 * using the last term in a prefix query and every term preceding it in a term query.
	 * </p>
	 * 
	 * @param field	the text field to query
	 * @param term the string query
	 * @return new match boolean prefix type Text Predicate
	 */
	public static TextPredicate matchBooleanPrefix(String field, String term) {
		return new TextPredicate(field, term, MatchType.BOOLEAN_PREFIX);
	}
	
	public static TextPredicate matchTextPhrase(String field, String term) {
		return new TextPredicate(field, term, MatchType.PHRASE);
	}
	
	public static TextPredicate matchTextParsed(String field, String term) {
		return new TextPredicate(field, term, MatchType.PARSED);
	}
	
	public static RegexpPredicate regexp(String field, String regexp) {
		return regexp(field, regexp, false);
	}
	
	public static RegexpPredicate regexp(String field, String regexp, boolean caseInsensitive) {
		return new RegexpPredicate(field, regexp, caseInsensitive);
	}
	
	public static Expression dismaxWithScoreCategories(Expression...disjuncts) {
		return dismaxWithScoreCategories(List.of(disjuncts));
	}
	
	public static Expression dismaxWithScoreCategories(List<Expression> disjuncts) {
		return new DisMaxPredicate(
			IntStream.range(0, disjuncts.size())
				.mapToObj(i -> scriptScore(disjuncts.get(i), "normalizeWithOffset", Map.of("offset", disjuncts.size() - 1 - i)))
				.collect(Collectors.toList()),
			0.0f
		);
	}
	
	public static Expression dismax(Expression...disjuncts) {
		return dismax(List.of(disjuncts));
	}
	
	public static Expression dismax(Collection<Expression> disjuncts) {
		return new DisMaxPredicate(disjuncts, 0.0f);
	}

	public static Expression scriptScore(Expression query, String scriptName) {
		return new ScriptScoreExpression(query, scriptName, Collections.emptyMap());
	}
	
	public static Expression scriptScore(Expression query, String scriptName, Map<String, Object> params) {
		return new ScriptScoreExpression(query, scriptName, params);
	}
	
	public static Expression scriptQuery(String script) {
		return new ScriptQueryExpression(script, Collections.emptyMap());
	}
	
	public static Expression scriptQuery(String script, Map<String, Object> params) {
		return new ScriptQueryExpression(script, params);
	}

	public static Expression matchAnyObject(String field, Iterable<?> values) {
		Object firstValue = Iterables.getFirst(values, null);
		if (firstValue == null) {
			return matchNone();
		} else if (firstValue instanceof String) {
			return matchAny(field, (Iterable<String>) values);
		} else if (firstValue instanceof Long) {
			return matchAnyLong(field, (Iterable<Long>) values);
		} else if (firstValue instanceof BigDecimal) {
			return matchAnyDecimal(field, (Iterable<BigDecimal>) values);
		} else if (firstValue instanceof Double) {
			return matchAnyDouble(field, (Iterable<Double>) values);
		} else if (firstValue instanceof Enum<?>) {
			return matchAnyEnum(field, (Iterable<Enum<?>>) values);
		} else if (firstValue instanceof Integer) {
			return matchAnyInt(field, (Iterable<Integer>) values);
		} else {
			throw new UnsupportedOperationException("Unsupported term expression value type: " + firstValue);
		}
	}
	
	public static MoreLikeThisPredicate moreLikeThis(Iterable<String> fields, Iterable<String> likeTexts, Iterable<String> unlikeTexts) {
		return new MoreLikeThisPredicate(fields, likeTexts, unlikeTexts);
	}
	
	public static QueryStringExpression queryString(String query) {
		return queryString(query, null);
	}
	
	public static QueryStringExpression queryString(String query, String defaultField) {
		return new QueryStringExpression(query, defaultField);
	}

	public static Expression matchDynamic(String field, Collection<String> dynamicFieldFilters) {
		ExpressionBuilder bool = bool();
		for (String dynamicFieldFilter : dynamicFieldFilters) {
			if (dynamicFieldFilter.contains(DYNAMIC_VALUE_DELIMITER)) {
				final String propertyName = dynamicFieldFilter.split(DYNAMIC_VALUE_DELIMITER)[0];
				final String propertyValue = dynamicFieldFilter.substring(propertyName.length() + 1, dynamicFieldFilter.length());
				if (Strings.isNullOrEmpty(propertyValue)) {
					throw new BadRequestException("'%s' filter argument '%s' is not allowed. Expected format is propertyName" + DYNAMIC_VALUE_DELIMITER + "propertyValue.", field, dynamicFieldFilter);
				}
				
				final String fieldToMatch = String.join(".", field, propertyName);

				// check special syntax first
				if (propertyValue.startsWith(IN_CLAUSE)) {
					// multi-valued -> terms query
					bool.filter(Expressions.matchAny(fieldToMatch, COMMA_SPLITTER.splitToList(propertyValue.substring(IN_CLAUSE.length()))));
				} else if (propertyValue.startsWith(RANGE_CLAUSE)) {
					// range -> range query
					final List<String> rangeValues = RANGE_SPLITTER.splitToList(propertyValue.substring(RANGE_CLAUSE.length()));
					if (rangeValues.size() > 2) {
						throw new BadRequestException("Multiple range expressions (<min>..<max>) are found in property value match. Only a single <min>..<max> expression is allowed.", propertyValue);
					} else if (rangeValues.size() <= 1) {
						throw new BadRequestException("At least one range expression (<min>..<max>) is required to match a value range.", propertyValue);
					} else {
						String lower = Strings.emptyToNull(rangeValues.get(0));
						String upper = Strings.emptyToNull(rangeValues.get(1));
						bool.filter(Expressions.matchRange(fieldToMatch, lower, upper));
					}
				} else if (propertyValue.endsWith("*")) {
					// wildcard at the end -> prefix query
					bool.filter(Expressions.prefixMatch(fieldToMatch, propertyValue.substring(0, (propertyValue.length() - 1))));						
				} else {
					// no special character -> standard term query
					bool.filter(Expressions.exactMatch(fieldToMatch, propertyValue));
				}
			} else {
				//Check if property exists
				bool.filter(Expressions.exists(String.join(".", field, dynamicFieldFilter)));
			}
		}
		return bool.build();
	}

	public static String toDynamicFieldFilter(String field, Object value) {
		if (field == null || value == null) {
			return null;
		} else {
			// convert Object value 
			final String filterValue;
			if (value instanceof Iterable<?> iterable) {
				// to valid in: expression if it is an iterable type
				filterValue = IN_CLAUSE.concat(COMMA_JOINER.join(iterable));
			} else if (value instanceof Range<?> range) {
				// or to range: expression if is is a Guava Range object
				filterValue = RANGE_CLAUSE.concat(RANGE_JOINER.join(range.lowerEndpoint(), range.upperEndpoint()));
			} else {
				// otherwise convert it to string to be able to join with the field name
				filterValue = String.valueOf(value);
			}
			return String.join(DYNAMIC_VALUE_DELIMITER, field, filterValue);
		}
	}

}