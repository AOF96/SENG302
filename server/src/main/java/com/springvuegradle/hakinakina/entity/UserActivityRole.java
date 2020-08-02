package com.springvuegradle.hakinakina.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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

    // this is the set of roles of the user in an activity(CREATOR/ORGANISER/PARTICIPANT/FOLLOWER)
    @ElementCollection(targetClass = ActivityRole.class, fetch = FetchType.EAGER)
    private Set<ActivityRole> activityRoles = new HashSet<>();

    // constructors
    public UserActivityRole() {}

    public UserActivityRole(UserActivityKey userActivityKey, ActivityRole activityRoles) {
        this.id = userActivityKey;
        this.activityRoles.add(activityRoles);
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

    public Set<ActivityRole> getActivityRoles() {
        return activityRoles;
    }

    public void setActivityRoles(Set<ActivityRole> activityRole) {
        this.activityRoles = activityRole;
    }

    public void addActivityRole(ActivityRole role) {
        this.activityRoles.add(role);
    }

    public void removeActivityRole(ActivityRole role) {
        this.activityRoles.remove(role);
    }
}
