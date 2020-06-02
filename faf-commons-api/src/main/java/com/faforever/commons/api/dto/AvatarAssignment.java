package com.faforever.commons.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@EqualsAndHashCode(callSuper = true)
@Type("avatarAssignment")
@Getter
@Setter
public class AvatarAssignment extends AbstractEntity {
  private Boolean selected;
  private OffsetDateTime expiresAt;
  @Relationship("player")
  @JsonIgnore
  private Player player;
  @Relationship("avatar")
  @JsonIgnore
  private Avatar avatar;
}
