package com.springvuegradle.hakinakina.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * An enum to represent a User's activities
 */
@Entity
@Table(name = "Activity_Type")
public class ActivityType {
    @Id
    @Column(name = "type_id")
    private String name;

    @ManyToMany(mappedBy = "activityTypes", cascade= CascadeType.MERGE, fetch=FetchType.EAGER)
    private Set<User> users = new HashSet<>();

    @ManyToMany(mappedBy = "activityTypes", cascade= CascadeType.MERGE, fetch=FetchType.EAGER)
    private Set<Activity> activities = new HashSet<>();

    protected ActivityType() {}

    public ActivityType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ActivityType) {
            ActivityType otherActivityType = (ActivityType) other;
            return this.name.equals(otherActivityType.getName());
        }
        return false;
    }
}