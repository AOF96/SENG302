package com.springvuegradle.hakinakina.acceptance_tests.steps;

import com.springvuegradle.hakinakina.controller.ActivityController;
import com.springvuegradle.hakinakina.controller.UserController;
import com.springvuegradle.hakinakina.repository.ActivityRepository;
import com.springvuegradle.hakinakina.repository.SessionRepository;
import com.springvuegradle.hakinakina.repository.UserRepository;
import com.springvuegradle.hakinakina.service.ActivityService;
import com.springvuegradle.hakinakina.service.UserService;
import com.springvuegradle.hakinakina.entity.*;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import javax.servlet.http.Cookie;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(User.class)
@AutoConfigureWebMvc
public class AdministratorsTestSteps {
    @Autowired
    private MockMvc mockMvc;

    private MvcResult result;
    @Mock
    private ActivityRepository activityRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ActivityService activityService;
    @Mock
    private UserService userService;
    @Mock
    private SessionRepository sessionRepository;
    @InjectMocks
    private UserController userController;

    private User user;
    private User userToPromote;
    Session session;



    @Before
    public void setup()
    {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(userController).build();
    }

    @Given("I create an account with name {string}, email {string} and ID {int}")
    public void iCreateAccountWithNameEmailAndID(String name, String email, int ID) {
        user.setUserId((long) ID);
        userRepository.save(user);
        assertEquals(email, user.getPrimaryEmail());
        assertEquals(ID, user.getUserId());
    }

    @And("I have the authorization token {string}")
    public void iHaveTheToken(String token) {
        session = new Session(token);
        session.setUser(user);
        sessionRepository.insertToken(token, user.getUserId());
        assertEquals(session.getUser().getPrimaryEmail(), user.getPrimaryEmail());
    }

    @And("My account has permission level {int}")
    public void accountHasPermissionLevel(int permissionLevel) {
        user.setPermissionLevel(permissionLevel);
        assertEquals(permissionLevel, user.getPermissionLevel());
    }

    @When("There is an account with name {string}, email {string}, ID {int} and permission level {int}")
    public void thereIsAccountWithValues(String name, String email, int ID, int permissionLevel) {
        userToPromote = new User(name, "Doe", email, "1995-12-20", Gender.FEMALE, 4, "password");
        userToPromote.setPermissionLevel(0);
        userRepository.save(userToPromote);
        assertEquals(email, userToPromote.getPrimaryEmail());
        assertEquals(ID, userToPromote.getUserId());
        assertEquals(permissionLevel, userToPromote.getPermissionLevel());
    }

    @And("I try to promote that account to be an admin using token {string}")
    public void iPromoteAccountToAdmin(String token) throws Exception {
        String request = "{\n" +
                "  \"role\": \"" + "admin" + "\",\n" +
                "}";

        final Cookie tokenCookie = new Cookie("s_id", token);
        when(userService.promoteUser(any(String.class), any(Long.class), any(String.class))).thenReturn(new ResponseEntity("User successfully promoted", HttpStatus.OK));
        result = mockMvc.perform(post("/profiles/" + userToPromote.getUserId() + "/role")
                .cookie(tokenCookie)
                .content(request).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("User successfully promoted")))
                .andReturn();
    }
}
