package com.springvuegradle.hakinakina.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * UserActivityKey class
 * This is an entity that has userId and activityId
 * This is used in UserActivityRole entity as UserActivityRole key
 */

@Embeddable
public class UserActivityKey implements Serializable {

    @Column(name = "user_id")
    private long userId;

    @Column(name = "activity_id")
    private long activityId;


    // constructors
    public UserActivityKey() {
    }

    public UserActivityKey(Long userId, Long activityId) {
        this.userId = userId;
        this.activityId = activityId;
    }


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getActivityId() {
        return activityId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }
}
