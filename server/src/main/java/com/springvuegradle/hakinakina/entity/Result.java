package com.springvuegradle.hakinakina.entity;

import javax.persistence.*;

/**
 * Relationship between user and achievement that stores the value of the result
 */
@Entity
public class Result {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name="value")
    private String value;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Achievement achievement;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private User user;

    public Result() {}

    public Result(String value) {
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Achievement getAchievement() {
        return achievement;
    }

    public void setAchievement(Achievement achievement) {
        this.achievement = achievement;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
