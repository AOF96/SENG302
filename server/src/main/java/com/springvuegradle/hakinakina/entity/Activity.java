package com.springvuegradle.hakinakina.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.springvuegradle.hakinakina.serialize.ActivityDeserializer;
import com.springvuegradle.hakinakina.serialize.UserDeserializer;
import com.springvuegradle.hakinakina.util.ErrorHandler;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.*;

/**
 * Activities entity class.
 */
@Entity
@JsonDeserialize(using= ActivityDeserializer.class)
public class Activity {
    @Id
    @GeneratedValue
    @Column(name = "activity_id")
    private Long id;

    @ManyToMany(mappedBy = "activities", cascade= CascadeType.MERGE, fetch=FetchType.LAZY)
    @JsonIgnore
    private Set<User> users = new HashSet<>();

    @JsonProperty("activity_name")
    @Column(name = "activity_name")
    @NotBlank(message = "Activity name is mandatory")
    private String name;

    @JsonProperty("description")
    @Column(name = "description")
    private String description;

    @JsonProperty("activity_type")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "Activity_ActivityType",
            joinColumns = {@JoinColumn(name = "activity_id")},
            inverseJoinColumns = {@JoinColumn(name = "type_id")}
    )
    private Set<ActivityType> activityTypes = new HashSet<>();

    @JsonProperty("continuous")
    @Column(name = "continuous")
    @NotNull(message = "Activity must be Continuous or Duration")
    private boolean continuous;

    @JsonProperty("start_time")
    @Column(name = "start_time")
    private java.sql.Timestamp startTime;

    @JsonProperty("end_time")
    @Column(name = "end_time")
    private java.sql.Timestamp endTime;

    @JsonIgnore
    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Achievement> achievements = new HashSet<>();

    @ManyToOne
    private User author;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "activity")
    private Set<UserActivityRole> userActivityRoles;

    @ManyToMany(cascade= CascadeType.MERGE, fetch=FetchType.LAZY)
    @JsonIgnore
    @JoinTable(
            name = "User_Activities_Shared",
            joinColumns = { @JoinColumn(name = "activity_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") }
    )
    private Set<User> usersShared = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "activity", fetch=FetchType.LAZY, cascade=CascadeType.REMOVE, orphanRemoval = true)
    private Set<HomeFeedEntry> involvedEntries = new HashSet<>();

    @Column(name = "visibility")
    private Visibility visibility;

    @OneToOne
    @JoinColumn(name = "location_id", referencedColumnName = "location_id")
    private Location location;

    public Activity() {}

    public Activity(String name, String description, boolean continuous, java.sql.Timestamp startTime, java.sql.Timestamp endTime) {
        this.name = name;
        this.description = description;
        this.continuous = continuous;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void addAchievement(Achievement achievement) {
        this.achievements.add(achievement);
        achievement.setActivity(this);
    }

    public void removeAchievement(Achievement achievement) {
        this.achievements.remove(achievement);
    }

    public Set<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(Set<Achievement> achievements) {
        this.achievements = achievements;
    }

    public Set<ActivityType> getActivityTypes() {
        return activityTypes;
    }

    public void setActivityTypes(Set<ActivityType> activityTypes) {
        this.activityTypes = activityTypes;
    }

    @JsonIgnore
    public Set<User> getUsers() {
        return users;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public void addUsers(User user) {
        this.usersShared.add(user);
        user.addActivitiesShared(this);
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public void removeUser(User user) {
        user.unfollowActivity(this);
        users.remove(user);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isContinuous() {
        return continuous;
    }

    public void setContinuous(boolean continuous) {
        this.continuous = continuous;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Set<UserActivityRole> getUserActivityRoles() {
        return userActivityRoles;
    }

    public void setUserActivityRoles(Set<UserActivityRole> userActivityRoles) {
        this.userActivityRoles = userActivityRoles;
    }

    public Set<User> getUsersShared() {
        return usersShared;
    }

    public void setUsersShared(Set<User> usersShared) {
        this.usersShared = usersShared;
    }

    // add user who got shared the activity and add user back to the activity
    public void addSharedUser(User user) {
        this.usersShared.add(user);
        user.addActivitiesShared(this);
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", continuous=" + continuous +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", location='" + location + '\'' +
                '}';
    }

    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        String activityStr = null;
        try {
            activityStr = objectMapper.writeValueAsString(this);
        } catch (Exception exception) {
            ErrorHandler.printProgramException(exception, "Could not map activity to JSON string");
        }
        return activityStr;
    }

    /**
     * Compares this Activity with other Activity to find which attributes are different
     * @param other The other Activity to compare to
     * @return A Set of ActivityAttributes that changed.
     */
    public Set<ActivityAttribute> findActivityChanges(Activity other) {
        Set<ActivityAttribute> differences = new HashSet<>();
        if (!this.getName().equals(other.getName())) {
            differences.add(ActivityAttribute.NAME);
        }
        if (this.getDescription() != null && !this.getDescription().equals(other.getDescription())) {
            differences.add(ActivityAttribute.DESCRIPTION);
        }

        boolean sameActivityTypes = true;
        if (!(this.getActivityTypes().size() == other.getActivityTypes().size())) {
            sameActivityTypes = false;
        } else {
            ArrayList<ActivityType> otherActivities = new ArrayList<>(other.getActivityTypes());
            ArrayList<ActivityType> thisActivities = new ArrayList<>(this.getActivityTypes());

            otherActivities.sort(Comparator.comparing(ActivityType::getName));
            thisActivities.sort(Comparator.comparing(ActivityType::getName));

            for (int i = 0; i < otherActivities.size(); i++) {
                if (!(otherActivities.get(i).getName().equals(thisActivities.get(i).getName()))) {
                    sameActivityTypes = false;
                    break;
                }
            }
        }

        if (!sameActivityTypes) {
            differences.add(ActivityAttribute.ACTIVITY_TYPES);
        }

        if (this.isContinuous() != other.isContinuous()) {
            differences.add(ActivityAttribute.CONTINUOUS);
        }
        if(this.getStartTime() != null && other.getStartTime() != null) {
            if (!this.getStartTime().equals(other.getStartTime())) {
                differences.add(ActivityAttribute.START_TIME);
            }
        }
        if((this.getStartTime() != null && other.getStartTime() == null) || (this.getStartTime() == null && other.getStartTime() != null)){
            differences.add(ActivityAttribute.START_TIME);
        }
        if(this.getEndTime() != null && other.getEndTime() != null) {
            if (!this.getEndTime().equals(other.getEndTime())) {
                differences.add(ActivityAttribute.END_TIME);
            }
        }
        if((this.getEndTime() != null && other.getEndTime() == null) || (this.getEndTime() == null && other.getEndTime() != null)){
            differences.add(ActivityAttribute.END_TIME);
        }
        if (this.getLocation() != null && !this.getLocation().equals(other.getLocation())) {
            differences.add(ActivityAttribute.LOCATION);
        }
        if (this.getVisibility() != null && !this.getVisibility().equals(other.getVisibility())) {
            differences.add(ActivityAttribute.VISIBILITY);
        }
        boolean sameUsers = true;
        if (this.getUsers().size() != other.getUsers().size()) {
            sameUsers = false;
        } else {
            ArrayList<User> otherUsers = new ArrayList<>(other.getUsers());
            ArrayList<User> thisUsers = new ArrayList<>(this.getUsers());
            for (int i = 0; i < otherUsers.size(); i++) {
                if (!(otherUsers.get(i).getUserId().equals(thisUsers.get(i).getUserId()))) {
                    sameUsers = false;
                    break;
                }
            }
        }

        if (!sameUsers) {
            differences.add(ActivityAttribute.USERS);
        }

        return differences;
    }
//    Returns a json string of the activity including the basic info needed for map pins
    public String getBasicActivityInfo(){
        String response = "" +
                "id: " + id +
                ", authorId: " + author.getUserId() +
                ", name: '" + name + '\'' +
                ", description: '" + description + '\'' +
                ", continuous: " + continuous +
                ", startTime: " + startTime +
                ", endTime: " + endTime +
                ", latitude: '" + location.getLatitude() + '\'' +
                ", longitude: '" + location.getLongitude() + '\'' +
                '}';
        return response;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return continuous == activity.continuous &&
                Objects.equals(name, activity.name) &&
                Objects.equals(description, activity.description) &&
                Objects.equals(startTime, activity.startTime) &&
                Objects.equals(endTime, activity.endTime) &&
                Objects.equals(author, activity.author) &&
                visibility == activity.visibility &&
                Objects.equals(location, activity.location);
    }
}