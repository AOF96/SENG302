package com.springvuegradle.hakinakina.dto;

import com.springvuegradle.hakinakina.entity.ResultType;

public class AchievementDto {
    private Long id;
    private String name;
    private String description;
    private ResultType resultType;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ResultType getResultType() {
        return resultType;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }
}
