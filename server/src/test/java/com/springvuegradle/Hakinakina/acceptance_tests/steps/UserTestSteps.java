package com.springvuegradle.Hakinakina.acceptance_tests.steps;

import com.springvuegradle.Hakinakina.Main;
import com.springvuegradle.Hakinakina.controller.ActivityService;
import com.springvuegradle.Hakinakina.controller.UserController;
import com.springvuegradle.Hakinakina.controller.UserService;
import com.springvuegradle.Hakinakina.entity.*;
import com.springvuegradle.Hakinakina.util.DatabaseConnection;
import com.springvuegradle.Hakinakina.util.ResponseHandler;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.Date;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(User.class)
@AutoConfigureWebMvc
public class UserTestSteps {
    @Autowired
    private MockMvc mockMvc;

    private MvcResult result;

    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;
    @Mock
    private UserRepository userRepository;

    private ResponseHandler responseHandler = new ResponseHandler();

    @Before
    public void setup()
    {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(userController).build();
    }

    @Given("I create a new account with email {string}")
    public void iCreateANewAccountWithEmail(String email) throws Exception {

        try(Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String sql = "INSERT INTO USER (firstname, lastname, middlename, gender, password, bio, nickname, birthdate, fitnesslevel, salt, primaryemail, permissionlevel) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, "Mayuko");
            statement.setString(2, "Mayuko");
            statement.setString(3, "Mayuko");
            statement.setString(4, "FEMALE");
            statement.setString(5, "FEMALE123");
            statement.setString(6, "FEMALE123");
            statement.setString(7, "FEMALE123");
            statement.setDate(8, new Date(2000,12,12));
            statement.setInt(9, 1);
            statement.setString(10, "asdasd");
            statement.setString(11, "%" + email + "%");
            statement.setInt(12, 0);

            statement.executeUpdate();
        }
        /*String input = "{\n" +
                "  \"lastname\": \"Williams\",\n" +
                "  \"firstname\": \"Mayuko\",\n" +
                "  \"middlename\": \"007\",\n" +
                "  \"nickname\": \"MWill\",\n" +
                "  \"primary_email\": \"" + email + "\",\n" +
                "  \"password\": \"SuPeRSeCuReP@sSw0rD!!123\",\n" +
                "  \"bio\": \"bio\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"female\",\n" +
                "  \"fitness\": 3\n" +
                "}";

        when(userService.validateCreateProfile(any(User.class))).thenReturn(responseHandler
                .formatSuccessResponse(200, "User updated"));
        this.mockMvc.perform(post("/profiles").contentType(MediaType.APPLICATION_JSON)
                .content(input)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("User updated")));*/
    }

    @When("I get all the accounts the have been created")
    public void iGetAllTheAccountsTheHaveBeenCreated() throws Exception {
    }

    @Then("Exactly one account is returned")
    public void exactlyOneAccountIsReturned() {
    }
}
