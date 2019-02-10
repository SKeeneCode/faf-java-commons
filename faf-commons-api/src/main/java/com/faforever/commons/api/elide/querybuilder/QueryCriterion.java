package com.faforever.commons.api.elide.querybuilder;

import com.faforever.commons.api.elide.ElideEntity;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface QueryCriterion {
  default String getId() {
    return getRootClass().getSimpleName() + "::" + getApiName();
  }

  Class<? extends ElideEntity> getRootClass();

  QueryCriterion setRootClass(Class<? extends ElideEntity> rootClass);

  String getApiName();

  QueryCriterion setApiName(String apiName);

  Set<QueryOperator> getSupportedOperators();

  QueryCriterion setSupportedOperators(Set<QueryOperator> supportedOperators);

  List<String> getProposals();

  QueryCriterion setProposals(List<String> proposals);

  boolean isAllowsOnlyProposedValues();

  QueryCriterion setAllowsOnlyProposedValues(boolean allowsOnlyProposedValues);

  default String createRsql(QueryOperator operator, String[] elements) {
    operator.validateElementAmount(elements.length);

    if (!getSupportedOperators().contains(operator)) {
      throw new IllegalArgumentException(MessageFormat.format("The operator `{0}` is not allowed", operator));
    }

    if (isAllowsOnlyProposedValues()) {
      List<String> disallowedElements = Stream.of(elements)
        .filter(e -> !getProposals().contains(e))
        .collect(Collectors.toList());

      if (disallowedElements.size() > 0) {
        throw new IllegalArgumentException(MessageFormat.format("There are values that are not allowed: {0}",
          disallowedElements.stream()
            .map(Objects::toString)
            .collect(Collectors.joining(", "))));
      }
    }

    return getApiName() + operator.getRsqlOperator() + Stream.of(elements)
      .map(Objects::toString)
      .collect(Collectors.joining(";"));
  }
}
