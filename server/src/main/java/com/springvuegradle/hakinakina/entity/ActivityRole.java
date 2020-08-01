package com.springvuegradle.hakinakina.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An enum to represent a User's role in an activity
 */
public enum ActivityRole {

    @JsonProperty("Creator")
    CREATOR,
    @JsonProperty("Organiser")
    ORGANISER,
    @JsonProperty("Participant")
    PARTICIPANT,
    @JsonProperty("Follower")
    FOLLOWER

}
