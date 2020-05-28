package com.springvuegradle.hakinakina.dto;

/**
 * SearchUserResponse entity class
 */

public class SearchUserResponse {

    private String email;
    private String fullName;
    private String nickname;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getNickname() {
        return nickname;
    }
}
