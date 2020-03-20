package com.springvuegradle.Hakinakina.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * An enum to represent a User's activities
 */
@Entity
public class ActivityType {
    @Id @GeneratedValue
    @Column(name = "types_id")
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "activityTypes", cascade= CascadeType.PERSIST, fetch=FetchType.EAGER)
    private Set<User> users = new HashSet<>();

    protected ActivityType() {}

    public ActivityType(String name){
        this.id = id;
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
        user.getActivityTypes().add(this);
    }

    @Override
    public String toString() {
        return name;
    }
}