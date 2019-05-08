package com.faforever.commons.api.elide.querybuilder;

import com.faforever.commons.api.elide.ElideEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ElideEntityScannerTest {
  private ElideEntityScanner instance;

  @BeforeEach
  void beforeEach() {
    instance = new ElideEntityScanner();
  }

  @Test
  void testScanFilterDefinition() {
    List<QueryCriterion> result = instance.scan(TestFilterClass.class);

    assertThat(result, iterableWithSize(1));

    QueryCriterion stringField = result.iterator().next();
    assertAll(
      () -> assertEquals(stringField.getId(), "TestFilterClass::stringField"),
      () -> assertEquals(stringField.getValueType(), String.class),
      () -> assertEquals(stringField.getApiName(), "stringField"),
      () -> assertEquals(stringField.getProposals().size(), 1),
      () -> assertEquals(stringField.getProposals().get(0), "hello"),
      () -> assertThat(stringField.getSupportedOperators(), is(QueryOperator.Preset.TEXT.getEnumSet())),
      () -> assertThat(stringField.isAllowsOnlyProposedValues(), is(true)),
      () -> assertThat(stringField.isAdvancedFilter(), is(true)),
      () -> assertThat(stringField.getOrder(), is(51))
    );
  }

  @Test
  void testScanTransientFilterSimple() {
    List<QueryCriterion> result = instance.scan(TestTransientFilterClassSimple.class);

    assertThat(result, iterableWithSize(1));

    QueryCriterion stringField = result.iterator().next();
    assertAll(
      () -> assertEquals(stringField.getId(), "TestTransientFilterClassSimple::directRelation.stringField"),
      () -> assertEquals(stringField.getValueType(), String.class),
      () -> assertEquals(stringField.getApiName(), "directRelation.stringField"),
      () -> assertEquals(stringField.getProposals().size(), 1),
      () -> assertEquals(stringField.getProposals().get(0), "hello"),
      () -> assertThat(stringField.getSupportedOperators(), is(QueryOperator.Preset.TEXT.getEnumSet())),
      () -> assertThat(stringField.isAllowsOnlyProposedValues(), is(true)),
      () -> assertThat(stringField.isAdvancedFilter(), is(true)),
      () -> assertThat(stringField.getOrder(), is(51))
    );
  }

  @Test
  void testScanTransientFilterComplex() {
    Map<String, QueryCriterion> result = instance.scan(TestTransientFilterClassComplex.class).stream()
      .collect(Collectors.toMap(QueryCriterion::getId, Function.identity()));

    assertThat(result, aMapWithSize(2));
    assertThat(result, hasKey("TestTransientFilterClassComplex::test"));
    assertThat(result, hasKey("TestTransientFilterClassComplex::recursiveRelation.directRelation.stringField"));

    QueryCriterion intField = result.get("TestTransientFilterClassComplex::test");
    assertAll(
      () -> assertEquals(intField.getId(), "TestTransientFilterClassComplex::test"),
      () -> assertEquals(intField.getValueType(), Integer.class),
      () -> assertEquals(intField.getApiName(), "test"),
      () -> assertEquals(intField.getProposals().size(), 0),
      () -> assertThat(intField.getSupportedOperators(), is(QueryOperator.Preset.NUMERIC.getEnumSet())),
      () -> assertThat(intField.isAllowsOnlyProposedValues(), is(false)),
      () -> assertThat(intField.isAdvancedFilter(), is(false)),
      () -> assertThat(intField.getOrder(), is(50))
    );

    QueryCriterion stringField = result.get("TestTransientFilterClassComplex::recursiveRelation.directRelation.stringField");
    assertAll(
      () -> assertEquals(stringField.getId(), "TestTransientFilterClassComplex::recursiveRelation.directRelation.stringField"),
      () -> assertEquals(stringField.getValueType(), String.class),
      () -> assertEquals(stringField.getApiName(), "recursiveRelation.directRelation.stringField"),
      () -> assertEquals(stringField.getProposals().size(), 1),
      () -> assertEquals(stringField.getProposals().get(0), "hello"),
      () -> assertThat(stringField.getSupportedOperators(), is(QueryOperator.Preset.TEXT.getEnumSet())),
      () -> assertThat(stringField.isAllowsOnlyProposedValues(), is(true)),
      () -> assertThat(stringField.isAdvancedFilter(), is(true)),
      () -> assertThat(stringField.getOrder(), is(51))
    );
  }

  public class TestFilterClass implements ElideEntity {
    @FieldFilterDefinition(allowedOperators = QueryOperator.Preset.TEXT, proposedValues = {"hello"},
      onlyProposedValues = true, advancedFilter = true, order = 51)
    private String stringField;

    @Override
    public String getId() {
      return null;
    }
  }

  public class TestTransientFilterClassSimple implements ElideEntity {
    @TransientFilter
    private TestFilterClass directRelation;

    @Override
    public String getId() {
      return null;
    }
  }

  public class TestTransientFilterClassComplex implements ElideEntity {
    @TransientFilter
    private TestTransientFilterClassSimple nonRecursiveRelation;

    @TransientFilter
    private TestTransientFilterClassSimple recursiveRelation;

    @FieldFilterDefinition(allowedOperators = QueryOperator.Preset.NUMERIC)
    private Integer test;

    @Override
    public String getId() {
      return null;
    }
  }
}
