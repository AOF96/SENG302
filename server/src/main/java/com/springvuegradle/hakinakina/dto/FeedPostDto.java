package com.springvuegradle.hakinakina.dto;

import com.springvuegradle.hakinakina.entity.ActivityChange;
import com.springvuegradle.hakinakina.entity.ActivityType;
import com.springvuegradle.hakinakina.entity.FeedEntryType;
import com.springvuegradle.hakinakina.entity.HomeFeedEntry;

import java.sql.Timestamp;
import java.util.Set;

/**
 * FeedPost class
 */

public class FeedPostDto {
    public FeedEntryType postType;
    public Timestamp dateTime;
    public String authorName;
    public String activityName;
    public Long activityId;
    public String textContext;

    public void setContent(HomeFeedEntry homeFeedEntry) {
        this.activityId = homeFeedEntry.getActivity().getId();
        this.activityName = homeFeedEntry.getActivity().getName();
        this.authorName = homeFeedEntry.getUser().getFirstName()+" "+homeFeedEntry.getUser().getLastName();
        this.dateTime = homeFeedEntry.getDatetime();
        this.postType = homeFeedEntry.getType();
        this.textContext = homeFeedEntry.getContent();
    }
}
