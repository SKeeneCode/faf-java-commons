package com.faforever.commons.api.elide.querybuilder.reflection;

import com.faforever.commons.api.dto.Game;
import com.faforever.commons.api.elide.querybuilder.ElideEntityScanner;
import com.faforever.commons.api.elide.querybuilder.QueryCriterion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

class ElideEntityScannerTest {
  private ElideEntityScanner instance;

  @BeforeEach
  void beforeEach() {
    instance = new ElideEntityScanner();
  }

  @Test
  void testScan() {
    Map<String, QueryCriterion> result = new ElideEntityScanner().scan(Game.class);
  }
}
