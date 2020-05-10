package com.springvuegradle.hakinakina.acceptance_tests.steps;

import com.springvuegradle.hakinakina.controller.UserController;
import com.springvuegradle.hakinakina.repository.PassportCountryRepository;
import com.springvuegradle.hakinakina.repository.UserRepository;
import com.springvuegradle.hakinakina.service.UserService;
import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.util.ResponseHandler;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AddPassportCountrySteps {

    @Autowired
    private MockMvc mockMvc;

    private MvcResult result;

    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PassportCountryRepository passportCountryRepository;
    @Mock
    public PassportCountry passportCountry = new PassportCountry("IND", "India");
    public  PassportCountry passportCountry2 = new PassportCountry("NZL", "New Zealand");

    private ResponseHandler responseHandler = new ResponseHandler();

    private User user1;
    private User user2;
    Session session1 = new Session("t0k3n");



    @Before
    public void setup()
    {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(userController).build();
    }

    @Given("User creates a new account with email {string} with no passport country")
    public void iCreateANewAccountWithEmail(String email) throws Exception {
        user1 = new User("John", "Doe", email, "1985-12-20", Gender.MALE,
                4, "password");
        Assert.assertEquals(email, user1.getPrimaryEmail());
        user1.setUserId((long) 1);
        session1.setUser(user1);
    }
    @When("User adds a new country to the user")
    public void passportCountryGetsAdded() throws Exception {
        user1.addPassportCountry(passportCountry);

    }

    @Then("country is added to the user passports")
    public void countryGetsAddedT0Database() {
        Assert.assertEquals(user1.getPassportCountries().size(), 1);
    }

    @Given("User exists with {int} passport country")
    public void user_exists_with_passport_country(int countryNum) {
        user2 = new User("John", "Doe", "Some@some.com", "1985-12-20", Gender.MALE,
                4, "password");
        user2.setUserId((long) 12);
        session1.setUser(user2);
        user2.addPassportCountry(passportCountry);
        Assert.assertEquals(user2.getPassportCountries().size(), countryNum);
    }
    @When("User adds another country {string} to his passport countries")
    public void userAddsAnotherCountry(String country) {
        passportCountry2.setName(country);
        user2.addPassportCountry(passportCountry2);
        Assert.assertEquals(user2.getPassportCountries().size(), 2);

    }
    @Then("User has {int} passport countries added")
    public void multipleCountriesAddedToUser(int CountryNum) {
        Assert.assertEquals(user2.getPassportCountries().size(), CountryNum);
    }
}
