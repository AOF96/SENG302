package com.springvuegradle.Hakinakina.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
public class Session {
    @Id
    @JsonProperty("token")
    @Column(name = "token")
    private String  token;

    protected Session() {}
    public Session(String token) {
        this.token = token;
    }

    @ManyToOne(cascade= CascadeType.PERSIST, fetch= FetchType.EAGER)
    private User user;


    public void setUser(User user) {
        this.user = user;
        user.getSessions().add(this);
    }

}
