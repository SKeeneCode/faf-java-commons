package com.faforever.commons.api.dto;

import com.faforever.commons.api.elide.querybuilder.ExplicitFilterDefinition;
import com.faforever.commons.api.elide.querybuilder.FieldFilterDefinition;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.List;

import static com.faforever.commons.api.elide.querybuilder.QueryOperator.Preset.*;

@Getter
@Setter
@Type("mapVersion")
@ExplicitFilterDefinition(filterPath = "map.displayName", valueType = String.class, allowedOperators = TEXT)
@ExplicitFilterDefinition(filterPath = "map.id", valueType = Integer.class, allowedOperators = NUMERIC)
public class MapVersion extends AbstractEntity {
  private String description;
  @FieldFilterDefinition(allowedOperators = NUMERIC, advancedFilter = true)
  private Integer maxPlayers;
  @FieldFilterDefinition(allowedOperators = NUMERIC, advancedFilter = true)
  private Integer width;
  @FieldFilterDefinition(allowedOperators = NUMERIC, advancedFilter = true)
  private Integer height;
  @FieldFilterDefinition(allowedOperators = NUMERIC, advancedFilter = true)
  private ComparableVersion version;
  @FieldFilterDefinition(allowedOperators = TEXT, advancedFilter = true)
  private String folderName;
  // TODO name consistently with folderName
  private String filename;
  @FieldFilterDefinition(allowedOperators = ENUM)
  private boolean ranked;
  private boolean hidden;
  private URL thumbnailUrlSmall;
  private URL thumbnailUrlLarge;
  private URL downloadUrl;

  @Relationship("map")
  private Map map;

  @Relationship("statistics")
  private MapVersionStatistics statistics;

  @Nullable
  @Relationship("ladder1v1Map")
  private Ladder1v1Map ladder1v1Map;

  @Relationship("reviews")
  private List<MapVersionReview> reviews;
}
