package com.faforever.commons.api.dto;

import com.github.jasminb.jsonapi.annotations.Type;
import lombok.EqualsAndHashCode;

@Type("globalRating")
@EqualsAndHashCode(callSuper = true)
public class GlobalRating extends Rating {
}
