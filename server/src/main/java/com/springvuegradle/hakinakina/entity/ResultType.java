package com.springvuegradle.hakinakina.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The different result types possible for an achievement
 */
public enum ResultType {
    @JsonProperty("CUSTOM_TYPE")
    CUSTOM_TYPE,
    @JsonProperty("QUANTITY")
    QUANTITY,
    @JsonProperty("TIME")
    TIME,
    @JsonProperty("FINISH_TIME")
    FINISH_TIME,
    @JsonProperty("START_TIME")
    START_TIME,
    @JsonProperty("METRES")
    METRES,
    @JsonProperty("HOURS")
    HOURS,
    @JsonProperty("MINUTES")
    MINUTES,
    @JsonProperty("SECONDS")
    SECONDS,
    @JsonProperty("POSITION")
    POSITION
}
