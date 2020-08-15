package com.springvuegradle.hakinakina.dto;


public class ResultDto {
    private Long id;
    private String value;
    private Long achievementId;
    private Long userId;

    @Override
    public String toString() {
        return "ResultDto{" +
                "id=" + id +
                ", value='" + value + '\'' +
                ", achievementId=" + achievementId +
                ", userId=" + userId +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(Long achievementId) {
        this.achievementId = achievementId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
