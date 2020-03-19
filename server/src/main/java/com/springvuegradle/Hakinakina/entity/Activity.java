package com.springvuegradle.Hakinakina.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Activity {
    @Id
    @Column(name = "activity_name")
    private String activity;

    protected Activity() {
    }

    public Activity(String activity) {
        this.activity = activity;
    }
}
