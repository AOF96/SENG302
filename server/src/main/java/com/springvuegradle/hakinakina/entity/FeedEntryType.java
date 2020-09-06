package com.springvuegradle.hakinakina.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An enum to represent Feed entry type
 */
public enum FeedEntryType {
    @JsonProperty("ActivityUpdate")
    ACTIVITYUPDATE,
    @JsonProperty("FollowActivity")
    FOLLOWACTIVITY
}
