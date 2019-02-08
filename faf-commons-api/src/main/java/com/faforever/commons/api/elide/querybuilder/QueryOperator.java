package com.faforever.commons.api.elide.querybuilder;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Set;

import static com.faforever.commons.api.elide.querybuilder.QueryOperator.ArgumentAmount.*;

@AllArgsConstructor
public enum QueryOperator {
  LIKE("==", SINGLE),
  EQUALS("=in=", SINGLE),
  UNEQUALS("=out=", SINGLE),
  GREATER_THAN("=gt=", SINGLE),
  GREATER_EQUALS_THAN("=ge=", SINGLE),
  LESSER_THAN("=lt=", SINGLE),
  LESSER_EQUALS_THAN("=le=", SINGLE),
  IN("=in=", MULTI),
  NOT_IN("=out=", MULTI),
  IS_NULL("=isnull=true", NONE),
  NOT_IS_NULL("=isnull=false", NONE);

  @Getter
  private String rsqlOperator;

  @Getter
  private ArgumentAmount argumentAmount;

  public void validateElementAmount(int noOfElements) {
    if (argumentAmount == NONE && noOfElements > 0) {
      throw new IllegalArgumentException(MessageFormat.format(
        "There are no elements allowed for operator `{0}`, given: {1}", this, noOfElements));
    }

    if (argumentAmount == SINGLE && noOfElements != 1) {
      throw new IllegalArgumentException(MessageFormat.format(
        "There are is only one element allowed for operator `{0}`, given: {1}", this, noOfElements));
    }

    if (argumentAmount == MULTI && noOfElements < 1) {
      throw new IllegalArgumentException(MessageFormat.format(
        "At least one element is required for operator `{0}`, given: {1}", this, noOfElements));
    }
  }

  public enum Preset {
    NUMERIC(new QueryOperator[]{EQUALS, UNEQUALS, GREATER_THAN, GREATER_EQUALS_THAN, LESSER_THAN, LESSER_EQUALS_THAN, IN, NOT_IN}),
    NULLABLE_NUMERIC(new QueryOperator[]{EQUALS, UNEQUALS, GREATER_THAN, GREATER_EQUALS_THAN, LESSER_THAN, LESSER_EQUALS_THAN, IN, NOT_IN, IS_NULL, NOT_IS_NULL}),
    TEXT(new QueryOperator[]{LIKE, EQUALS, UNEQUALS, IN, NOT_IN}),
    NULLABLE_TEXT(new QueryOperator[]{LIKE, EQUALS, UNEQUALS, IN, NOT_IN, IS_NULL, NOT_IS_NULL}),
    DATETIME(new QueryOperator[]{LIKE, EQUALS, UNEQUALS, IN, NOT_IN}),
    NULLABLE_DATETIME(new QueryOperator[]{LIKE, EQUALS, UNEQUALS, IN, NOT_IN, IS_NULL, NOT_IS_NULL}),
    ENUM(new QueryOperator[]{EQUALS, UNEQUALS, IN, NOT_IN}),
    NULLABLE_ENUM(new QueryOperator[]{EQUALS, UNEQUALS, IN, NOT_IN, IS_NULL, NOT_IS_NULL});

    @Getter
    private final Set<QueryOperator> enumSet;

    Preset(QueryOperator[] operators) {
      enumSet = Sets.immutableEnumSet(Arrays.asList(operators));
    }
  }

  public enum ArgumentAmount {
    /**
     * No arguments are allowed
     */
    NONE,

    /**
     * Exactly one argument is allowed
     */
    SINGLE,

    /**
     * One argument is required, more are allowed
     */
    MULTI
  }
}
