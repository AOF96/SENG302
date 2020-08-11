package com.springvuegradle.hakinakina.dto;

import com.springvuegradle.hakinakina.entity.ActivityChange;
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

    public void setContent(ActivityChange activityChange) {
        this.activityId = activityChange.getActivity().getId();
        this.activityName = activityChange.getActivity().getName();
        this.authorName = activityChange.getAuthor().getFirstName()+" "+activityChange.getAuthor().getLastName();
        this.dateTime = activityChange.getChangeTime();
        this.postType = "activityUpdate";
        this.textContext = activityChange.getDescription();
    }
}
