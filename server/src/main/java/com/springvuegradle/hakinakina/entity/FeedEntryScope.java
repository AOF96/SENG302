package com.springvuegradle.hakinakina.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An enum to represent Feed entry scope
 */
public enum FeedEntryScope {
    @JsonProperty("Private")
    PRIVATE,
    @JsonProperty("Activity")
    ACTIVITY,
    @JsonProperty("Global")
    GLOBAL
}
