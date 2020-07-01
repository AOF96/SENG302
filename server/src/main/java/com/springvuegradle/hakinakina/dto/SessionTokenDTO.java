package com.springvuegradle.hakinakina.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Pattern;

public class SessionTokenDTO {

    private String sessionToken;

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
}
