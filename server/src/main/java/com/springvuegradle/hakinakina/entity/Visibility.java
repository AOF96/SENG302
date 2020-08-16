package com.springvuegradle.hakinakina.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Visibility {
    @JsonProperty("public")
    PUBLIC,
    @JsonProperty("restricted")
    RESTRICTED,
    @JsonProperty("private")
    PRIVATE
}
