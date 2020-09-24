package com.springvuegradle.hakinakina.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.hakinakina.entity.Visibility;

import java.sql.Timestamp;
import java.util.Optional;

/**
 * DTO for Search Activity response
 */
public class SearchActivityDto {
    private Long id;
    private String name;
    private Visibility visibility;
    private boolean continuous;

    @JsonProperty("start_time")
    private java.sql.Timestamp startTime;

    @JsonProperty("end_time")
    private java.sql.Timestamp endTime;

    @JsonProperty("location")
    private Optional<SearchActivityLocationDto> searchActivityLocationDto;

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

    public Visibility getVisibility() { return visibility; }

    public void setVisibility(Visibility visibility) { this.visibility = visibility; }

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

    public Optional<SearchActivityLocationDto> getSearchActivityLocationDto() {
        return searchActivityLocationDto;
    }

    public void setSearchActivityLocationDto(SearchActivityLocationDto searchActivityLocationDto) {
        this.searchActivityLocationDto = Optional.ofNullable(searchActivityLocationDto);
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", visibility=" + visibility +
                ", continuous=" + continuous +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", searchActivityLocationDto=" + searchActivityLocationDto +
                '}';
    }
}
