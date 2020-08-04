package com.springvuegradle.hakinakina.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

/**
 * UserActivityRole class
 * This is the relationship table between user and activity entities
 */

@Entity
@Table(name = "User_Activity_Role")
public class UserActivityRole {

    @EmbeddedId
    private UserActivityKey id;

    @ManyToOne
    @MapsId("user_id")
    @JoinColumn(name="user_id")
    @JsonIgnore
    private User user;

    @ManyToOne
    @MapsId("activity_id")
    @JoinColumn(name="activity_id")
    @JsonIgnore
    private Activity activity;

    private ActivityRole activityRole;


    // constructors
    public UserActivityRole() {}

    public UserActivityRole(UserActivityKey userActivityKey, ActivityRole activityRole) {
        this.id = userActivityKey;
        this.activityRole = activityRole;
    }


    public UserActivityKey getId() {
        return id;
    }

    public void setId(UserActivityKey id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public ActivityRole getActivityRole() {
        return activityRole;
    }

    public void setActivityRole(ActivityRole activityRole) {
        this.activityRole = activityRole;
    }
}
