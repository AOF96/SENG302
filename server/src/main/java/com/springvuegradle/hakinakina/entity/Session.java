package com.springvuegradle.hakinakina.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
public class Session {
    @Id
    @JsonProperty("token")
    @Column(name = "token")
    private String  token;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    protected Session() {}
    public Session(String token) {
        this.token = token;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

}
