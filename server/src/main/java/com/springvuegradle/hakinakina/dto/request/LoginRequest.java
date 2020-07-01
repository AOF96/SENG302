package com.springvuegradle.hakinakina.dto.request;

/**
 * Login request data transfer object
 * Created according to U1 JSON Request
 *
 * # POST /login
 * {
 *   "email": "my@email.com",
 *   "password": "mysecurepwd"
 * }
 *
 * POST /login has been changed in UserController
 *
 */

public class LoginRequest {

    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
