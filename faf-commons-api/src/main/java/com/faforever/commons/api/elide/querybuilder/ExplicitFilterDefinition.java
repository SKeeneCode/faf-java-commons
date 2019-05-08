package com.faforever.commons.api.elide.querybuilder;


import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Repeatable(ExplicitFilterDefinitions.class)
@Target({TYPE})
@Retention(RUNTIME)
public @interface ExplicitFilterDefinition {
  String filterPath();

  Class<?> valueType();

  QueryOperator.Preset allowedOperators();

  String[] proposedValues() default {};

  boolean onlyProposedValues() default false;

  boolean advancedFilter() default false;

  int order() default 50;
}
