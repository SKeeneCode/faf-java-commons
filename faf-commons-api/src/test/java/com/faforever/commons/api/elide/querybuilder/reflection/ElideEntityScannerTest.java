package com.faforever.commons.api.elide.querybuilder.reflection;

import com.faforever.commons.api.dto.Game;
import com.faforever.commons.api.elide.querybuilder.QueryCriterion;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class ElideEntityScannerTest {
  ElideEntityScanner instance;

  @Before
  public void setUp() {
    instance = new ElideEntityScanner();
  }

  @Test
  public void testScan() {
    Map<String, QueryCriterion> result = new ElideEntityScanner().scan(Game.class);
  }
}
