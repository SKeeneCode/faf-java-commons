package com.faforever.commons.api.elide.querybuilder;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class QueryBuilderTest {
  @Test
  void testSimpleBuilder() {
    String result = QueryBuilder.of(() -> "A")
      .build();

    assertThat(result, is("A"));
  }

  @Test
  void testAndElementBuilder() {
    String result = QueryBuilder.of(() -> "A")
      .and(() -> "B")
      .build();

    assertThat(result, is("(A);B"));

    result = QueryBuilder.of(() -> "A")
      .and(() -> "B")
      .and(() -> "C")
      .build();

    assertThat(result, is("((A);B);C"));
  }

  @Test
  void testOrElementBuilder() {
    String result = QueryBuilder.of(() -> "A")
      .or(() -> "B")
      .build();

    assertThat(result, is("(A),B"));

    result = QueryBuilder.of(() -> "A")
      .or(() -> "B")
      .or(() -> "C")
      .build();

    assertThat(result, is("((A),B),C"));
  }

  @Test
  void testMixedAndOrElementBuilder() {
    String result = QueryBuilder.of(() -> "A")
      .and(() -> "B")
      .or(() -> "C")
      .and(() -> "D")
      .build();

    assertThat(result, is("(((A);B),C);D"));
  }

  @Test
  void testConcatAdd() {
    String result = QueryBuilder.concatAnd(() -> "A").evaluate();
    assertThat(result, is("(A)"));


    result = QueryBuilder.concatAnd(() -> "A", () -> "B").evaluate();
    assertThat(result, is("(A;B)"));


    result = QueryBuilder.concatAnd(() -> "A", () -> "B", () -> "C").evaluate();
    assertThat(result, is("(A;B;C)"));
  }

  @Test
  void testConcatOr() {
    String result = QueryBuilder.concatOr(() -> "A").evaluate();
    assertThat(result, is("(A)"));


    result = QueryBuilder.concatOr(() -> "A", () -> "B").evaluate();
    assertThat(result, is("(A,B)"));


    result = QueryBuilder.concatOr(() -> "A", () -> "B", () -> "C").evaluate();
    assertThat(result, is("(A,B,C)"));
  }

  @Test
  void testAndQueryBuilder() {

    String result = QueryBuilder.of(() -> "A")
      .and(QueryBuilder.of(() -> "B"))
      .build();

    assertThat(result, is("(A);(B)"));

    result = QueryBuilder.of(() -> "A")
      .and(QueryBuilder.of(() -> "B")
        .and(() -> "C"))
      .and(() -> "D")
      .build();

    assertThat(result, is("((A);((B);C));D"));
  }

  @Test
  void testOrQueryBuilder() {

    String result = QueryBuilder.of(() -> "A")
      .or(QueryBuilder.of(() -> "B"))
      .build();

    assertThat(result, is("(A),(B)"));

    result = QueryBuilder.of(() -> "A")
      .or(QueryBuilder.of(() -> "B")
        .and(() -> "C"))
      .and(() -> "D")
      .build();

    assertThat(result, is("((A),((B);C));D"));
  }
}
