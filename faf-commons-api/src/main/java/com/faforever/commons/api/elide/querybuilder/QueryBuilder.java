package com.faforever.commons.api.elide.querybuilder;

import java.text.MessageFormat;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryBuilder {
  private static final String RSQL_OR_OPERATOR = ",";
  private static final String RSQL_AND_OPERATOR = ";";

  private String currentQuery;

  private QueryBuilder(QueryElement element) {
    currentQuery = element.evaluate();
  }

  public static QueryElement concatAnd(QueryElement... elements) {
    return () -> Stream.of(elements)
      .map(QueryElement::evaluate)
      .collect(Collectors.joining(RSQL_AND_OPERATOR, "(", ")"));
  }

  public static QueryElement concatOr(QueryElement... elements) {
    return () -> Stream.of(elements)
      .map(QueryElement::evaluate)
      .collect(Collectors.joining(RSQL_OR_OPERATOR, "(", ")"));
  }

  public static QueryBuilder of(QueryElement element) {
    return new QueryBuilder(element);
  }

  private void addElement(String logicalOperator, String newQueryElement) {
    currentQuery = MessageFormat.format("({0}){1}{2}",
      currentQuery,
      logicalOperator,
      newQueryElement);
  }

  public QueryBuilder and(QueryBuilder query) {
    addElement(RSQL_AND_OPERATOR, "(" + query.build() + ")");
    return this;
  }

  public QueryBuilder and(QueryElement element) {
    addElement(RSQL_AND_OPERATOR, element.evaluate());
    return this;
  }

  public QueryBuilder or(QueryBuilder query) {
    addElement(RSQL_OR_OPERATOR, "(" + query.build() + ")");
    return this;
  }

  public QueryBuilder or(QueryElement element) {
    addElement(RSQL_OR_OPERATOR, element.evaluate());
    return this;
  }

  public String build() {
    return currentQuery;
  }
}
