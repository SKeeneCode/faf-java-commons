package com.faforever.commons.api.elide.querybuilder;

import com.faforever.commons.api.elide.ElideEntity;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * Basic implementation of QueryCriterion
 */
@Data
public final class QueryCriterionImpl<T> implements QueryCriterion<T> {
  private Class<? extends ElideEntity> rootClass;

  private String apiName;

  private Class<T> valueType;

  private Set<QueryOperator> supportedOperators;

  private List<T> proposals;

  private boolean allowsOnlyProposedValues;

  private boolean advancedFilter;

  private int order;
}
