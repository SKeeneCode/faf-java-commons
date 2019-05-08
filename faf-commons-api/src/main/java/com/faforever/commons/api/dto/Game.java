package com.faforever.commons.api.dto;

import com.faforever.commons.api.elide.ElideEntity;
import com.faforever.commons.api.elide.querybuilder.ExplicitFilterDefinition;
import com.faforever.commons.api.elide.querybuilder.FieldFilterDefinition;
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
@ExplicitFilterDefinition(filterPath = "featuredMod.displayName", valueType = String.class,
  allowedOperators = ENUM, onlyProposedValues = true,
  proposedValues = {"FAF", "Murder Party", "Nomads", "LABwars", "Ladder1v1", "Xtreme Wars", "Phantom-X",
    "King of the Hill", "Claustrophobia", "FAF Beta", "FAF Develop", "Equilibrium"})
public class Game implements ElideEntity {
  @Id
  @FieldFilterDefinition(allowedOperators = NUMERIC, order = 1)
  private String id;

  @FieldFilterDefinition(allowedOperators = TEXT, order = 2)
  private String name;

  @FieldFilterDefinition(allowedOperators = DATETIME)
  private OffsetDateTime startTime;

  @FieldFilterDefinition(allowedOperators = NULLABLE_DATETIME)
  private OffsetDateTime endTime;

  @FieldFilterDefinition(allowedOperators = ENUM, advancedFilter = true)
  private Validity validity;

  @FieldFilterDefinition(allowedOperators = ENUM, advancedFilter = true)
  private VictoryCondition victoryCondition;

  @Relationship("reviews")
  private List<GameReview> reviews;

  @Relationship("playerStats")
  @TransientFilter
  private List<GamePlayerStats> playerStats;

  @Relationship("host")
  @TransientFilter(advancedFilter = true)
  private Player host;

  @Relationship("featuredMod")
  private FeaturedMod featuredMod;

  @Relationship("mapVersion")
  @TransientFilter
  private MapVersion mapVersion;
}
