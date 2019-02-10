package com.faforever.commons.api.elide.querybuilder;

import com.faforever.commons.api.elide.ElideEntity;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * Basic implementation of QueryCriterion
 */
@Data
final class QueryCriterionImpl implements QueryCriterion {
  private Class<? extends ElideEntity> rootClass;

  private String apiName;

  private Set<QueryOperator> supportedOperators;

  private List<String> proposals;

  private boolean allowsOnlyProposedValues;
}
