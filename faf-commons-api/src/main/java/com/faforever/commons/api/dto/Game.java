package com.faforever.commons.api.dto;

import com.faforever.commons.api.elide.ElideEntity;
import com.faforever.commons.api.elide.querybuilder.FilterDefinition;
import com.faforever.commons.api.elide.querybuilder.TransientFilter;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.time.OffsetDateTime;
import java.util.List;

import static com.faforever.commons.api.elide.querybuilder.QueryOperator.Preset.*;


@Getter
@Setter
@FieldNameConstants
@EqualsAndHashCode(of = "id")
@Type("game")
public class Game implements ElideEntity {
  @Id
  @FilterDefinition(allowedOperators = NUMERIC, order = 1)
  private String id;

  @FilterDefinition(allowedOperators = TEXT, order = 2)
  private String name;

  @FilterDefinition(allowedOperators = DATETIME)
  private OffsetDateTime startTime;

  @FilterDefinition(allowedOperators = NULLABLE_DATETIME)
  private OffsetDateTime endTime;

  @FilterDefinition(allowedOperators = ENUM, advancedFilter = true)
  private Validity validity;

  @FilterDefinition(allowedOperators = ENUM, advancedFilter = true)
  private VictoryCondition victoryCondition;

  @Relationship("reviews")
  private List<GameReview> reviews;

  @Relationship("playerStats")
  @TransientFilter(enforceRecursion = true)
  private List<GamePlayerStats> playerStats;

  @Relationship("host")
  @TransientFilter(enforceRecursion = true, advancedFilter = true)
  private Player host;

  @Relationship("featuredMod")
  @FilterDefinition(allowedOperators = ENUM, overrideFieldName = "featuredMod.displayName", onlyProposedValues = true,
    proposedValues = {"FAF", "Murder Party", "Nomads", "LABwars", "Ladder1v1", "Xtreme Wars", "Phantom-X",
      "King of the Hill", "Claustrophobia", "FAF Beta", "FAF Develop", "Equilibrium"})
  private FeaturedMod featuredMod;

  @Relationship("mapVersion")
  private MapVersion mapVersion;
}
