package com.springvuegradle.hakinakina.dto;

import com.springvuegradle.hakinakina.entity.ActivityType;

import java.sql.Timestamp;
import java.util.Set;

/**
 * FeedPost class
 */

public class FeedPostDto {
    public String postType;
    public Timestamp dateTime;
    public String authorName;
    public String activityName;
    public Long activityId;
    public String textContext;
}
