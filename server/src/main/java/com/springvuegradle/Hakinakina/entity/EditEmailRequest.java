package com.springvuegradle.Hakinakina.entity;

import javax.persistence.Entity;
import java.util.List;

public class EditEmailRequest {

    private String primary_email;
    private List<String> additional_email;

    public EditEmailRequest(String primary_email, List<String> additional_email){

        this.primary_email = primary_email;
        this.additional_email = additional_email;
    }

    public String getPrimary_email() {
        return primary_email;
    }

    public List<String> getAdditional_email() {
        return additional_email;
    }
}
