package com.springvuegradle.Hakinakina.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An enum to represent a User's gender
 */
public enum Gender {
    @JsonProperty("male")
    MALE,
    @JsonProperty("female")
    FEMALE,
    @JsonProperty("non_binary")
    NON_BINARY
}
