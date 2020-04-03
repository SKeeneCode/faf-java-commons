package com.faforever.commons.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Type("moderationReport")
@Getter
@Setter
public class ModerationReport extends AbstractEntity {
  private String reportDescription;
  private ModerationReportStatus reportStatus;
  private String gameIncidentTimecode;
  private String moderatorNotice;
  private String moderatorPrivateNote;

  @Relationship("bans")
  @JsonIgnore
  private Set<BanInfo> bans;
  @Relationship("reporter")
  @JsonIgnore
  private User reporter;
  @Relationship("game")
  @JsonIgnore
  private Game game;
  @Relationship("lastModerator")
  @JsonIgnore
  private User lastModerator;
  @Relationship("reportedUsers")
  @JsonIgnore
  private Set<User> reportedUsers;
}
