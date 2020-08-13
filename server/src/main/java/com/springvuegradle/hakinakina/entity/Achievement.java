package com.springvuegradle.hakinakina.entity;

import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;

/**
 * Achievement entity class for activity achievements
 */
@Entity
public class Achievement {
    @Id @GeneratedValue
    @Column(name = "achievement_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "result_type")
    private ResultType resultType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Activity activity;

    protected Achievement() {}

    public Achievement(String name, String description, ResultType resultType) {
        this.name = name;
        this.description = description;
        this.resultType = resultType;
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

    public ResultType getResultType() {
        return resultType;
    }

    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
