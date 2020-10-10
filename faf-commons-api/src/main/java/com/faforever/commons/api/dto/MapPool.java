package com.faforever.commons.api.dto;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Type("mapPool")
public class MapPool extends AbstractEntity {

    private String name;

    @Relationship("mapVersions")
    private List<MapVersion> mapVersions;

}
