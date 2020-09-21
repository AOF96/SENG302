package com.springvuegradle.hakinakina.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.hakinakina.entity.Activity;
import com.springvuegradle.hakinakina.entity.ActivityType;
import com.springvuegradle.hakinakina.entity.Location;
import com.springvuegradle.hakinakina.entity.Visibility;
import com.springvuegradle.hakinakina.repository.ActivityRepository;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;



public class ActivityMapDto {

    private Long id;
    private String name;
    private String description;
    private boolean continuous;

    @JsonProperty("start_time")
    private java.sql.Timestamp startTime;

    @JsonProperty("end_time")
    private java.sql.Timestamp endTime;

    private Visibility visibility;

    @JsonProperty("activity_types")
    private Set<String> activityTypes = new HashSet<>();

    private Location location;
    private int numFollowers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isContinuous() {
        return continuous;
    }

    public void setContinuous(boolean continuous) {
        this.continuous = continuous;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Set<String> getActivityTypes() {
        return activityTypes;
    }

    public void setActivityTypes(Set<String> activityTypes) {
        this.activityTypes = activityTypes;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getNumFollowers() {
        return numFollowers;
    }

    public void setNumFollowers(int numFollowers) {
        this.numFollowers = numFollowers;
    }

}
