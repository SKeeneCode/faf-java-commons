package com.faforever.commons.api.dto;

import com.faforever.commons.api.elide.ElideEntity;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Type("mod")
@NoArgsConstructor
public class Mod implements ElideEntity {

  @Id
  private String id;
  private String displayName;
  private String author;
  private OffsetDateTime createTime;

  @Relationship("uploader")
  private Player uploader;

  @Relationship("versions")
  private List<ModVersion> versions;

  @Relationship("latestVersion")
  private ModVersion latestVersion;

  public Mod(String id, String displayName, String author, OffsetDateTime createTime) {
    this.id = id;
    this.displayName = displayName;
    this.author = author;
    this.createTime = createTime;
  }
}
