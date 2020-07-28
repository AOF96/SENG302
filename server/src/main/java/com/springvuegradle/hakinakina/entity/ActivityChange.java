package com.springvuegradle.hakinakina.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;

/**
 * The entity class for ActivityChange. This entity represents a log of a change to an Activity authored by a User.
 */
@Entity
@Table(name = "Activity_Change")
public class ActivityChange {
    @Id
    @GeneratedValue
    @Column(name = "activity_change_id")
    private Long id;

    @JsonProperty("description")
    @Column(name = "description")
    private String description;

    @Column(name = "change_time")
    private java.sql.Timestamp changeTime;

    @ManyToOne
    private User author;

    @ManyToOne
    private Activity activity;

    public ActivityChange(String description, Timestamp changeTime, User author, Activity activity) {
        this.description = description;
        this.changeTime = changeTime;
        this.author = author;
        this.activity = activity;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Timestamp getChangeTime() {
        return changeTime;
    }

    public User getAuthor() {
        return author;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setChangeTime(Timestamp changeTime) {
        this.changeTime = changeTime;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
