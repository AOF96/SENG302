package com.springvuegradle.Hakinakina.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Passport_Country")
public class PassportCountry {
    @Id
    @Column(name = "country_id")
    private String countryId;

    private String name;

    @ManyToMany(mappedBy = "passportCountries", cascade= CascadeType.MERGE, fetch=FetchType.EAGER)
    private Set<User> users = new HashSet<>();

    protected PassportCountry() {}

    /**
     * Constructor for passport county
     * @param name
     */
    public PassportCountry(String countryId, String name) {
        this.countryId = countryId;
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

    /**
     * Adds user to relation
     * @param user
     */
    public void addUser(User user) {
        users.add(user);
        user.getPassportCountries().add(this);
    }

    public String getCountryId() {
        return countryId;
    }

    @Override
    public String toString() {
        return name;
    }
}
