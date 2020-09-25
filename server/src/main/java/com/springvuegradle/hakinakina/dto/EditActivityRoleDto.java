package com.springvuegradle.hakinakina.dto;

import javax.validation.constraints.NotNull;

/**
 * editActivityRequest entity class
 */
public class EditActivityRoleDto {

    @NotNull(message = "subscriber field cannot be null")
    private EditSubscriberRoleDto subscriber;


    // constructor
    public EditActivityRoleDto() {}

    public EditActivityRoleDto(EditSubscriberRoleDto dto) {
        this.subscriber = dto;
    }


    public EditSubscriberRoleDto getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(EditSubscriberRoleDto subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public String toString() {
        return "{" +
                "\"subscriber\": " + subscriber +
                '}';
    }
}
