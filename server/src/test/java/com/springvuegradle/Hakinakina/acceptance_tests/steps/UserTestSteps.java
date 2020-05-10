package com.springvuegradle.Hakinakina.acceptance_tests.steps;

import com.springvuegradle.Hakinakina.Main;
import com.springvuegradle.Hakinakina.controller.ActivityService;
import com.springvuegradle.Hakinakina.controller.UserController;
import com.springvuegradle.Hakinakina.controller.UserService;
import com.springvuegradle.Hakinakina.entity.*;
import com.springvuegradle.Hakinakina.util.DatabaseConnection;
import com.springvuegradle.Hakinakina.util.ResponseHandler;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.Assert;
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
import java.util.List;

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
    private List<User> returnedUsers = new ArrayList<>();

    private User user;

    @Before
    public void setup()
    {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(userController).build();
    }

    @Given("I create a new account with email {string}")
    public void iCreateANewAccountWithEmail(String email) throws Exception {
        user = new User("name", "Doe", email, "1995-12-20", Gender.MALE, 4, "password");
       Assert.assertNotNull(user);

    }

    @When("I save the user to the database")
    public void iGetAllTheAccountsTheHaveBeenCreated() throws Exception {
        userRepository.save(user);
    }

    @And("I retrieve all users with the email {string} from the database")
    public void iRetrieveAllUsersFromTheDatabase(String email) {
        returnedUsers.add(userRepository.findUserByEmail(email));
    }

    @Then("Exactly one account is returned")
    public void exactlyOneAccountIsReturned() {
        Assert.assertEquals(1, returnedUsers.size());
    }

    @Given("I create a new account with id {long}")
    public void iCreateANewAccountWithId(long arg0) {
        user = new User("name", "Doe", "test", "1995-12-20", Gender.MALE, 4, "password");
        user.setUserId(arg0);
    }

    @And("I retrieve all users with the id {long} from the database")
    public void iRetrieveAllUsersWithTheIdFromTheDatabase(long arg0) {
        returnedUsers.add(userRepository.getOne(arg0));
    }

    @Given("I create three new users and save all users to the database")
    public void iCreateNewUsers() {
        User user1 = new User("u1", "Doe", "test1", "1995-12-20", Gender.MALE, 4, "password");
        User user2 = new User("u2", "Doe", "test2", "1995-12-20", Gender.MALE, 4, "password");
        User user3 = new User("u3", "Doe", "test3", "1995-12-20", Gender.MALE, 4, "password");
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
    }

    @When("I retrieve all users")
    public void iRetrieveAllUsers() {
        returnedUsers.add(userRepository.findUserByEmail("test1"));
        returnedUsers.add(userRepository.findUserByEmail("test1"));
        returnedUsers.add(userRepository.findUserByEmail("test1"));
    }

    @Then("Exactly {int} accounts are returned")
    public void exactlyAccountsAreReturned(int arg0) {
        Assert.assertEquals(3, returnedUsers.size());
    }

    @And("I delete the data from the database")
    public void iDeleteTheDataFromTheDatabase() {
        userRepository.deleteById((long) 75);
    }

    @Then("Exactly {int} users are returned")
    public void exactlyUsersAreReturned(int arg0) {
        Assert.assertNull(returnedUsers.get(arg0));
    }
}
