package com.faforever.commons.api.elide.querybuilder;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QueryCriterionImplTest {
  @Test
  void testSuccess() {
    QueryCriterionImpl<String> instance = new QueryCriterionImpl<String>()
      .setSupportedOperators(QueryOperator.Preset.TEXT.getEnumSet())
      .setValueType(String.class)
      .setApiName("someApiName");
    List<String> arguments = Lists.newArrayList("success");

    String result = instance.createRsql(QueryOperator.EQUALS, arguments);
    assertThat(result, is("someApiName=in=(\"success\")"));
  }

  @Test
  void testWithSingleNullElement() {
    QueryCriterionImpl<String> instance = new QueryCriterionImpl<String>()
      .setSupportedOperators(QueryOperator.Preset.TEXT.getEnumSet())
      .setValueType(String.class)
      .setApiName("someApiName");
    String o = null;
    List<String> arguments = Lists.newArrayList(o);

    IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> instance.createRsql(QueryOperator.EQUALS, arguments));
    assertThat(illegalArgumentException.getMessage(), is("There are is only one element allowed for operator `EQUALS`, given: 0"));
  }

  @Test
  void testWrongTyped() {
    QueryCriterionImpl instance = new QueryCriterionImpl<String>()
      .setSupportedOperators(QueryOperator.Preset.TEXT.getEnumSet())
      .setValueType(String.class);
    List<Integer> wronglyTypedList = Lists.newArrayList(1);

    assertThrows(IllegalArgumentException.class, () -> instance.createRsql(QueryOperator.EQUALS, wronglyTypedList));
  }
}
