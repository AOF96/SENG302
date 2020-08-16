package com.springvuegradle.hakinakina.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The different result types possible for an achievement
 */
public enum ResultType {
    @JsonProperty("WORD")
    WORD,
    @JsonProperty("QUANTITY")
    QUANTITY,
    @JsonProperty("TIME")
    TIME,
    @JsonProperty("MONEY")
    MONEY
}
