package com.faforever.commons.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.github.jasminb.jsonapi.annotations.Id;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.OffsetDateTime;

@Value
@EqualsAndHashCode(of = "id")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.WRAPPER_OBJECT)
public class Tournament {
  @Id
  String id;
  String name;
  String description;
  @JsonProperty("tournament_type")
  String tournamentType;
  @JsonProperty("created_at")
  OffsetDateTime createdAt;
  @JsonProperty("participants_count")
  int participantCount;
  @JsonProperty("start_at")
  OffsetDateTime startingAt;
  @JsonProperty("completed_at")
  OffsetDateTime completedAt;
  @JsonProperty("full_challonge_url")
  String challongeUrl;
  @JsonProperty("live_image_url")
  String liveImageUrl;
  @JsonProperty("sign_up_url")
  String signUpUrl;
  @JsonProperty("open_signup")
  boolean openForSignup;
}
