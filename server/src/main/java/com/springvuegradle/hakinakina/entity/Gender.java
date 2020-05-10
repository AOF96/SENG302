package com.springvuegradle.hakinakina.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An enum to represent a User's gender
 */
public enum Gender {
    @JsonProperty("Male")
    MALE,
    @JsonProperty("Female")
    FEMALE,
    @JsonProperty("Non-Binary")
    NON_BINARY
}
