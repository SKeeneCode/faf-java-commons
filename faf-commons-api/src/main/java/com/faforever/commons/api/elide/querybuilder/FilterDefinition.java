package com.faforever.commons.api.elide.querybuilder;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
public @interface FilterDefinition {

  QueryOperator.Preset allowedOperators();

  String[] proposedValues() default {};

  boolean onlyProposedValues() default false;

  boolean advancedFilter() default false;

  int order() default 50;
}
