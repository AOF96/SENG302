package com.springvuegradle.hakinakina.dto;

import com.springvuegradle.hakinakina.entity.ActivityRole;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * editSubscriberDTO that is needed for editActivityRequest entity class
 */
public class EditSubscriberRoleDto {

    @Email(message = "Email field is not valid")
    private String email;
    @NotBlank(message = "Role field cannot be blank")
    private ActivityRole role;


    // constructor
    public EditSubscriberRoleDto() {}

    public EditSubscriberRoleDto(String email, ActivityRole role){
        this.email = email;
        this.role = role;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ActivityRole getRole() {
        return role;
    }

    public void setRole(ActivityRole role) {
        this.role = role;
    }
}
