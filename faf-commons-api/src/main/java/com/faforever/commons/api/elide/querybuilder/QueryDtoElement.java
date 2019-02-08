package com.faforever.commons.api.elide.querybuilder;

import lombok.Value;

@Value
public class QueryDtoElement implements QueryElement {
  private final String prefix;
  private final QueryCriterion criteria;
  private final QueryOperator operator;
  private final String[] elements;

  public QueryDtoElement(String prefix, QueryCriterion criteria, QueryOperator operator, String[] elements) {
    this.prefix = prefix;
    this.criteria = criteria;
    this.operator = operator;
    this.elements = elements;
  }

  public QueryDtoElement(QueryCriterion criteria, QueryOperator operator, String[] elements) {
    this(null, criteria, operator, elements);
  }

  @Override
  public String evaluate() {
    return (prefix == null ? "" : (prefix + ".")) + criteria.createRsql(operator, elements);
  }
}
