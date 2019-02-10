package com.faforever.commons.api.elide.querybuilder;

import com.faforever.commons.api.elide.ElideEntity;
import lombok.Data;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class QueryCriterion {
  private Class<? extends ElideEntity> rootClass;

  private String apiName;

  private Set<QueryOperator> supportedOperators;

  private List<String> proposals;

  private boolean allowsOnlyProposedValues;

  public String getId() {
    return rootClass.getSimpleName() + "::" + apiName;
  }

  public String createRsql(QueryOperator operator, String[] elements) {
    operator.validateElementAmount(elements.length);

    if (!getSupportedOperators().contains(operator)) {
      throw new IllegalArgumentException(MessageFormat.format("The operator `{0}` is not allowed", operator));
    }

    if (allowsOnlyProposedValues) {
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
