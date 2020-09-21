package com.springvuegradle.hakinakina.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.springvuegradle.hakinakina.serialize.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.springvuegradle.hakinakina.serialize.*;
import com.springvuegradle.hakinakina.util.EncryptionUtil;
import com.springvuegradle.hakinakina.util.ErrorHandler;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.sql.Date;
import java.util.HashSet;
import java.util.Objects;
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
    private Long userId;

    @JsonProperty("firstname")
    @Column(name = "first_name")
    @NotBlank
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

    @OneToOne
    private Location location;

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

    @JsonProperty("fitness")
    @Column(name = "fitness_level")
    private int fitnessLevel;

    @JsonProperty("city")
    @Column(name = "city")
    private String city;


    @JsonProperty("state")
    @Column(name = "state")
    private String state;


    @JsonProperty("country")
    @Column(name = "country")

    private String country;

    @JsonProperty("passports")
    @JsonSerialize(using= PassportSerializer.class)
    @JsonDeserialize(using = CountryDeserializer.class)
    @ManyToMany(fetch=FetchType.EAGER, cascade=CascadeType.MERGE)
    @JoinTable(
            name = "User_PassportCountry",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "country_id") }
    )
    private Set<PassportCountry> passportCountries = new HashSet<>();

    @JsonProperty("activities")
    @JsonSerialize(using= ActivityTypeSerializer.class)
    @ManyToMany(fetch=FetchType.EAGER, cascade=CascadeType.MERGE)
    @JoinTable(
            name = "User_ActivityTypes",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "type_id") }
    )
    private Set<ActivityType> activityTypes = new HashSet<>();

    @ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.MERGE)
    @JoinTable(
            name = "User_Activities",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "activity_id") }
    )

    @JsonIgnore
    private Set<Activity> activities = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy="usersShared", fetch=FetchType.LAZY, cascade=CascadeType.MERGE)
    private Set<Activity> activitiesShared = new HashSet<>();

    @JsonIgnore
    @OneToMany
    private Set<Activity> authoredActivities = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<HomeFeedEntry> involvedEntries = new HashSet<>();

    @JsonIgnore
    private String salt;

    @JsonProperty("primary_email")
    @Column(name = "primary_email")
    private String primaryEmail;

    @JsonProperty("additional_email")
    @JsonSerialize(using= EmailSerializer.class)
    @OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.REMOVE, orphanRemoval = true)
    private Set<Email> emails = new HashSet<>();

    @JsonProperty("permission_level")
    @Column(name = "permission_level")
    private Integer permissionLevel;

    @JsonIgnore
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL
//            orphanRemoval = true
    )
    private Set<Session> sessions = new HashSet<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "user")
    private Set<UserActivityRole> userActivityRoles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Result> results = new HashSet<>();

    public User() {}

    public User(String firstName,
                String lastName,
                String primaryEmail,
                String birthDate,
                Gender gender,
                int fitnessLevel,
                String password
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        if (birthDate == null) {
            this.birthDate = null;
        } else if (birthDate.equals("")) {
            this.birthDate = null;
        } else {
            this.birthDate = Date.valueOf(birthDate);
        }
        this.fitnessLevel = fitnessLevel;
        this.primaryEmail = primaryEmail;
        if(this.permissionLevel == null) {
            this.permissionLevel = 0;
        }

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

    public void addResult(Result result) {
        results.add(result);
        result.setUser(this);
    }

    public void addEmail(Email email) {
        emails.add(email);
        email.setUser(this);
    }

    public void removeEmail(Email email) {
        email.removeUser();
        emails.remove(email);
    }

    public void addSession(Session session) {
        sessions.add(session);
        session.setUser(this);
    }

    public void removeSession(Session session) {
        sessions.remove(session);
        session.setUser(null);
    }

    public void followActivity(Activity activity) {
        activitiesShared.add(activity);
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @JsonIgnore
    public Set<Activity> getActivities() {
        return activities;
    }

    public void unfollowActivity(Activity activity) {
        activitiesShared.remove(activity);
    }

    public Set<PassportCountry> getPassportCountries() {
        return passportCountries;
    }

    public Set<ActivityType> getActivityTypes() {
        return activityTypes;
    }

    public void setActivityTypes(Set<ActivityType> activityTypes) {
        this.activityTypes = activityTypes;
    }

    public void setActivity(Set<Activity> activities) {
        this.activities = activities;
    }

    /**
     * Adds passport country to relation
     * @param passportCountry
     */
    public void addPassportCountry(PassportCountry passportCountry) {
        passportCountries.add(passportCountry);
        passportCountry.getUsers().add(this);
    }

    /**
     * Adds activity type to relation
     * @param acitivityType
     */
    public void addActivityTypes(ActivityType acitivityType) {
        activityTypes.add(acitivityType);
        acitivityType.getUsers().add(this);
    }

    public Set<Email> getEmails() {
        return emails;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        if(primaryEmail.isBlank()) return;
        this.primaryEmail = primaryEmail;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if(firstName.isBlank()) return;
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if(lastName.isBlank()) return;
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

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setState(String state) {
        this.state = state;
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
        if(birthDate == null) return;
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

    public Integer getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(Integer permissionLevel) {
        if(permissionLevel == null) return;
        this.permissionLevel = permissionLevel;
    }

    public Set<Activity> getAuthoredActivities() {
        return authoredActivities;
    }

    public void setAuthoredActivities(Set<Activity> authoredActivities) {
        this.authoredActivities = authoredActivities;
    }

    public void addAuthoredActivities(Activity authoredActivity) {
        this.authoredActivities.add(authoredActivity);
    }

    public Set<UserActivityRole> getUserActivityRoles() {
        return userActivityRoles;
    }

    public void setUserActivityRoles(Set<UserActivityRole> userActivityRoles) {
        this.userActivityRoles = userActivityRoles;
    }

    public void setActivities(Set<Activity> participateActivities) {
        this.activities = participateActivities;
    }

    public void addActivitiesShared(Activity activity) {
        this.activitiesShared.add(activity);
    }

    public void setActivitiesShared(Set<Activity> activitiesShared) {
        this.activitiesShared = activitiesShared;
    }

    public Set<Activity> getActivitiesShared() {
        return activitiesShared;
    }

    @Override
    public String toString() {
        String result = "ID: " + getUserId() + String.format("\nName: %s %s %s",firstName, middleName, lastName) +
                "\nNickname: " + getNickName() + "\nEmails: " + getEmails() + "\nBio: " + getBio() +
                "\nDate of Birth: " + getBirthDate() + "\nGender: " + getGender()
                + "\nPassword: " + getPassword() + "\nFitness Level: " + getFitnessLevel() +
                "\nPassport Countries: " + getPassportCountries() + "\nSalt: " + getSalt() +
                "\nPrimary Email: " + getPrimaryEmail();
        return result;
    }

    @Override
    public boolean equals(Object other){
        if(other instanceof User){
            User otherUser = (User) other;
            if (otherUser.userId == null || this.userId == null) {
                return false;
            }
            return this.userId.equals(otherUser.userId);
        }
        return false;
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

    public void resetPassportCountries() {
        passportCountries = new HashSet<PassportCountry>();
    }
}
