package com.faforever.commons.api.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Type("player")
public class Player extends AbstractEntity {
    private String login;
    @Relationship("names")
    List<NameRecord> names;

    @Relationship("globalRating")
    private GlobalRating globalRating;

    @Relationship("ladder1v1Rating")
    private Ladder1v1Rating ladder1v1Rating;

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
