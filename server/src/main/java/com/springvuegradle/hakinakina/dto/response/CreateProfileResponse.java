package com.springvuegradle.hakinakina.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.hakinakina.dto.request.CreateProfileRequest;

public class CreateProfileResponse {

    private CreateProfileRequest createProfileRequest;

    private String sessionToken;

    @JsonProperty("profile_id")
    private Long userId;

    public CreateProfileRequest getCreateProfileRequest() {
        return createProfileRequest;
    }

    public void setCreateProfileRequest(CreateProfileRequest createProfileRequest) {
        this.createProfileRequest = createProfileRequest;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
