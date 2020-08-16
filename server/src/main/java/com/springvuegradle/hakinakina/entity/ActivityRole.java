package com.springvuegradle.hakinakina.entity;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * An enum to represent a User's role in an activity
 */
public enum ActivityRole {

    CREATOR("creator"),
    ORGANISER("organiser"),
    PARTICIPANT("participant"),
    FOLLOWER("follower");

    private String role;

    ActivityRole(String text) {
        this.role = text;
    }

    @JsonValue
    public String getRole() {
        return this.toString();
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
