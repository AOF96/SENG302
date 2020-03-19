package com.springvuegradle.Hakinakina.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An enum to represent a User's activities
 */
public enum ActivityTypes {
    @JsonProperty("Relaxing")
    RELAXING,
    @JsonProperty("Fun")
    FUN,
    @JsonProperty("Adventurous")
    AVENTUROUS,
    @JsonProperty("Extreme")
    EXTREME,
    @JsonProperty("TeamSport")
    TEAMSPORT
}