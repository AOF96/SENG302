package com.springvuegradle.Hakinakina.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.springvuegradle.Hakinakina.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.springvuegradle.Hakinakina.util.EncryptionUtil;
import com.springvuegradle.Hakinakina.util.ErrorHandler;

import javax.persistence.*;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * User entity class
 */
@Entity
@JsonDeserialize(using=UserDeserializer.class)
public class User {
    @Id @GeneratedValue
    @JsonProperty("profile_id")
    @Column(name = "user_id")
    private Long userID;

    @JsonProperty("firstname")
    @Column(name = "first_name")
    private String firstName;

    @JsonProperty("lastname")
    @Column(name = "last_name")
    private String lastName;

    @JsonProperty("middlename")
    @Column(name = "middle_name")
    private String middleName;

    @JsonProperty("gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @JsonIgnore
    @JsonDeserialize(using=PasswordDeserializer.class)
    private String password;

    private String bio;

    @JsonProperty("nickname")
    @Column(name = "nick_name")
    private String nickName;

    @JsonProperty("date_of_birth")
    @Column(name = "date_of_birth")
    @JsonSerialize(using= DateSerializer.class)
    private java.sql.Date birthDate;

    @Column(name = "fitness_level")
    private int fitnessLevel;

    @JsonProperty("passports")
    @JsonSerialize(using=PassportSerializer.class)
    @JsonDeserialize(using = CountryDeserializer.class)
    @ManyToMany(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST)
    private Set<PassportCountry> passportCountries = new HashSet<>();

    @JsonIgnore
    private String salt;

    @JsonProperty("primary_email")
    @Column(name = "primary_email")
    private String primaryEmail;

    @JsonProperty("additional_email")
    @JsonSerialize(using=EmailSerializer.class)
    @OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.REMOVE, orphanRemoval = true)
    private Set<Email> emails = new HashSet<>();

    @JsonProperty("session_tokens")
    @OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.REMOVE, orphanRemoval = true)
    private Set<Session> sessionToken = new HashSet<>();

    protected User() {}

    public User(String firstName, String lastName, String primaryEmail, String birthDate, Gender gender, int fitnessLevel, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        if (birthDate.equals("")) {
            this.birthDate = null;
        } else {
            this.birthDate = Date.valueOf(birthDate);
        }
        this.fitnessLevel = fitnessLevel;
        System.out.println(this.fitnessLevel);
        this.primaryEmail = primaryEmail;

        try {
            this.salt = EncryptionUtil.getNewSalt();
            if (password == null) {
                this.password = null;
            } else {
                this.password = EncryptionUtil.getEncryptedPassword(password, this.salt);
            }
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "Error while creating password.");
        }
    }

    public void addEmail(Email email) {
        emails.add(email);
        email.setUser(this);
    }

    public void addSession(Session session) {
        sessionToken.add(session);
        session.setUser(this);
    }

    public void removeEmail(Email email) {
        email.removeUser();
        emails.remove(email);
    }

    public Set<PassportCountry> getPassportCountries() {
        return passportCountries;
    }

    /**
     * Adds passport country to relation
     * @param passportCountry
     */
    public void addPassportCountry(PassportCountry passportCountry) {
        passportCountries.add(passportCountry);
        passportCountry.getUsers().add(this);
    }

    public Set<Email> getEmails() {
        return emails;
    }

    public Set<Session> getSessions() {
        return sessionToken;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public Long getUser_id() {
        return userID;
    }

    public void setUser_id(Long userID) {
        this.userID = userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setEncryptedPassword(String password) {
        this.password = password;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public int getFitnessLevel() {
        return fitnessLevel;
    }

    public void setFitnessLevel(int fitnessLevel) {
        this.fitnessLevel = fitnessLevel;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public String toString() {
        String result = String.format("Name: %s %s %s",firstName, middleName, lastName) +
                "\nNickname: " + getNickName() + "\nEmails: " + emails + "\nBio: " + getBio() +
                "\nDate of Birth: " + getBirthDate().toString() + "\nGender: " + getGender().toString();
        return result;
    }

    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        String userStr = null;
        try {
            userStr = objectMapper.writeValueAsString(this);
        } catch (Exception exception) {
            ErrorHandler.printProgramException(exception, "Could not map user to JSON string");
        }
        return userStr;
    }
}
