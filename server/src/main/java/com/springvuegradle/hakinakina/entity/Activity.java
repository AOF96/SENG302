package com.springvuegradle.hakinakina.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.hakinakina.util.ErrorHandler;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * Activities entity class.
 */
@Entity
public class Activity {
    @Id
    @GeneratedValue
    @Column(name = "activity_id")
    private Long id;

    @JsonProperty("activity_name")
    @Column(name = "activity_name")
    @NotBlank(message = "Activity name is mandatory")
    private String name;

    @JsonProperty("description")
    @Column(name = "description")
    private String description;

    @JsonProperty("activity_type")
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(
            name = "Activity_ActivityType",
            joinColumns = {@JoinColumn(name = "activity_id")},
            inverseJoinColumns = {@JoinColumn(name = "type_id")}
    )
    private Set<ActivityType> activityTypes = new HashSet<>();

    @JsonProperty("continuous")
    @Column(name = "continuous")
    @NotNull(message = "Activity must be Continuous or Duration")
    private boolean continuous;

    @JsonProperty("start_time")
    @Column(name = "start_time")
    private java.sql.Timestamp startTime;

    @JsonProperty("end_time")
    @Column(name = "end_time")
    private java.sql.Timestamp endTime;

    @JsonProperty("location")
    @Column(name = "location")
    private String location;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "activity")
    private Set<UserActivityRole> userActivityRoles;

    @ManyToMany(mappedBy = "activitiesShared", cascade= CascadeType.MERGE, fetch=FetchType.LAZY)
    private Set<User> usersShared = new HashSet<>();



    @Column(name = "visibility")
    private Visibility visibility;

    public Activity() {}

    public Activity(String name, String description, boolean continuous, java.sql.Timestamp startTime, java.sql.Timestamp endTime, String location) {
        this.name = name;
        this.description = description;
        this.continuous = continuous;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
    }

    public Set<ActivityType> getActivityTypes() {
        return activityTypes;
    }

    public void setActivityTypes(Set<ActivityType> activityTypes) {
        this.activityTypes = activityTypes;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Set<UserActivityRole> getUserActivityRoles() {
        return userActivityRoles;
    }

    public void setUserActivityRoles(Set<UserActivityRole> userActivityRoles) {
        this.userActivityRoles = userActivityRoles;
    }

    public Set<User> getUsersShared() {
        return usersShared;
    }

    public void setUsersShared(Set<User> usersShared) {
        this.usersShared = usersShared;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", continuous=" + continuous +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", location='" + location + '\'' +
                '}';
    }

    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        String activityStr = null;
        try {
            activityStr = objectMapper.writeValueAsString(this);
        } catch (Exception exception) {
            ErrorHandler.printProgramException(exception, "Could not map user to JSON string");
        }
        return activityStr;
    }

}