package com.springvuegradle.hakinakina.dto;

import com.springvuegradle.hakinakina.entity.Email;
import com.springvuegradle.hakinakina.entity.Visibility;


import java.util.Set;

public class ActivityVisibilityDto {

    private Visibility visibility;
    private Set<String> accessorsEmails;

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Set<String> getAccessorsEmails() {
        return accessorsEmails;
    }

    public void setAccessorsEmails(Set<String> accessorsEmails) {
        this.accessorsEmails = accessorsEmails;
    }


}
