package com.springvuegradle.hakinakina.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * The entity class for HomeFeedEntry. This entity represents a all Home feed entries.
 */
@Entity
@Table(name = "Home_Feed_Entry")
public class HomeFeedEntry {
    @Id
    @GeneratedValue
    @Column(name = "feed_entry_id")
    private Long id;

    @JsonProperty("scope")
    @Column(name = "scope")
    @Enumerated(EnumType.STRING)
    private FeedEntryScope scope;

    @JsonProperty("type")
    @Column(name = "post_type")
    @Enumerated(EnumType.STRING)
    private FeedEntryType type;

    @JsonProperty("content")
    @Column(name = "content")
    private String content;

    @ManyToOne
    private User user;

    @ManyToOne
    private Activity activity;

    @Column(name = "datetime")
    private Timestamp datetime;

    protected HomeFeedEntry() {}

    public HomeFeedEntry(String content, Timestamp datetime, User user, Activity activity, FeedEntryType type, FeedEntryScope scope) {
        this.content = content;
        this.datetime = datetime;
        this.user = user;
        this.activity = activity;
        this.type = type;
        this.scope = scope;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FeedEntryScope getScope() {
        return scope;
    }

    public void setScope(FeedEntryScope scope) {
        this.scope = scope;
    }

    public FeedEntryType getType() {
        return type;
    }

    public void setType(FeedEntryType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Timestamp getDatetime() {
        return datetime;
    }

    public void setDatetime(Timestamp datetime) {
        this.datetime = datetime;
    }
}
