package com.springvuegradle.Hakinakina.entity;

import javax.persistence.*;

@Entity
public class Email {
    @Id @GeneratedValue
    @Column(name = "email_id")
    private Long emailID;

    private String email;

    @ManyToOne(cascade= CascadeType.PERSIST, fetch=FetchType.EAGER)
    private User user;

    protected Email() {
    }

    public Email(String email) {
        this.email = email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        user.getEmails().add(this);
    }

    public void removeUser() {
        user = null;
    }

    @Override
    public String toString() {
        return email;
    }
}
