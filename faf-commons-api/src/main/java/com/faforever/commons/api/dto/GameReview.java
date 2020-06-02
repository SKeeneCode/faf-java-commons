package com.faforever.commons.api.dto;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Type("gameReview")
@EqualsAndHashCode(callSuper = true)
public class GameReview extends Review {

    @Relationship("game")
    private Game game;
}
