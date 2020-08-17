package com.springvuegradle.hakinakina.dto;

import com.springvuegradle.hakinakina.entity.Email;
import com.springvuegradle.hakinakina.entity.Visibility;


import java.util.List;
import java.util.Map;
import java.util.Set;

public class ActivityVisibilityDto {

    private Visibility visibility;
    private List<Map<String, String>> accessors;

    // constructor
    public ActivityVisibilityDto(){};

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public List<Map<String, String>> getAccessors() {
        return accessors;
    }

    public void setAccessors(List<Map<String, String>> accessorsEmails) {
        this.accessors = accessorsEmails;
    }


}
