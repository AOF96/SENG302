package com.springvuegradle.Hakinakina.endpoints;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.springvuegradle.Hakinakina.controller.UserController;
import com.springvuegradle.Hakinakina.controller.UserService;
import com.springvuegradle.Hakinakina.entity.*;
import com.springvuegradle.Hakinakina.util.ResponseHandler;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PassportCountryRepository countryRepository;

    @MockBean
    private EmailRepository emailRepository;

    @MockBean
    private SessionRepository sessionRepository;

    private ResponseHandler responseHandler = new ResponseHandler();

    @Test
    public void userCreationSuccess() throws Exception {
        String input = "{\n" +
                "  \"lastname\": \"Qiu\",\n" +
                "  \"firstname\": \"Jackie\",\n" +
                "  \"middlename\": \"Danger\",\n" +
                "  \"nickname\": \"J\",\n" +
                "  \"primary_email\": \"jqi26@uclive.ac.nz\",\n" +
                "  \"password\": \"Password1\",\n" +
                "  \"bio\": \"bio\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": 2\n" +
                "}";

        when(service.validateCreateProfile(any(User.class))).thenReturn(responseHandler
                .formatSuccessResponse(200, "User updated"));
        this.mockMvc.perform(post("/profiles").contentType(MediaType.APPLICATION_JSON)
                .content(input)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("User updated")));
    }

    @Test
    public void loginSuccess() throws Exception {
        String input = "{\n" +
                "  \"email\": \"my@email.com\",\n" +
                "  \"password\": \"mysecurepwd\"\n" +
                "}";
        when(service.checkLogin("my@email.com", "mysecurepwd"))
                .thenReturn(new ResponseEntity("{}", HttpStatus.valueOf(201)));
        this.mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                .content(input)).andDo(print()).andExpect(status().is(201));
    }

    @Test
    public void loginFail() throws Exception {
        String input = "{\n" +
                "  \"email\": \"my@email.com\",\n" +
                "  \"password\": \"mysecurepwd\"\n" +
                "}";
        when(service.checkLogin("my@email.com", "mysecurepwd"))
                .thenReturn(new ResponseEntity("Incorrect password", HttpStatus.FORBIDDEN));
        this.mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                .content(input)).andDo(print()).andExpect(status().is(403))
                .andExpect(content().string(containsString("Incorrect password")));
    }

    @Test
    public void logoutSuccess() throws Exception {
        doNothing().when(sessionRepository).removeToken(any(String.class));
        this.mockMvc.perform(post("/logout").cookie(new Cookie("s_id", "1")))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("User logged out")));
    }


}
