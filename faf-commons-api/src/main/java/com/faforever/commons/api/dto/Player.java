package com.faforever.commons.api.dto;

import com.faforever.commons.api.elide.querybuilder.ExplicitFilterDefinition;
import com.faforever.commons.api.elide.querybuilder.FieldFilterDefinition;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static com.faforever.commons.api.elide.querybuilder.QueryOperator.Preset.NUMERIC;
import static com.faforever.commons.api.elide.querybuilder.QueryOperator.Preset.TEXT;

@Getter
@Setter
@Type("player")
@ExplicitFilterDefinition(filterPath = "globalRating.rating", valueType = Double.class, allowedOperators = NUMERIC)
@ExplicitFilterDefinition(filterPath = "ladder1v1Rating.rating", valueType = Double.class, allowedOperators = NUMERIC)
public class Player extends AbstractEntity {
  @FieldFilterDefinition(allowedOperators = TEXT)
  private String login;
    @Relationship("names")
    List<NameRecord> names;
    @RestrictedVisibility("IsModerator")
    private String email;
    private String userAgent;
    @RestrictedVisibility("IsModerator")
    private String steamId;
    @RestrictedVisibility("IsModerator")
    private String recentIpAddress;
    @RestrictedVisibility("IsModerator")
    private OffsetDateTime lastLogin;

    @Relationship("globalRating")
    private GlobalRating globalRating;

    @Relationship("ladder1v1Rating")
    private Ladder1v1Rating ladder1v1Rating;

    @Relationship("lobbyGroup")
    private LobbyGroup lobbyGroup;

    @Relationship("bans")
    private List<BanInfo> bans;

    @Relationship("avatarAssignments")
    @JsonIgnore
    private List<AvatarAssignment> avatarAssignments;

    @JsonBackReference
    @Relationship("reporterOnModerationReports")
    private Set<ModerationReport> reporterOnModerationReports;

    @Override
    public String toString() {
        return String.format("%s [id %s]", login, id);
    }
}
