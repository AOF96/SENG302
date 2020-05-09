package com.springvuegradle.Hakinakina.acceptance_tests.steps;

import com.springvuegradle.Hakinakina.controller.UserController;
import com.springvuegradle.Hakinakina.controller.UserService;
import com.springvuegradle.Hakinakina.entity.*;
import com.springvuegradle.Hakinakina.util.ResponseHandler;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class EditProfileSteps {

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
    @Mock
    public SessionRepository sessionRepository;
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

    @Given("User creates a new account with primary email {string}")
    public void iCreateANewAccountWithEmail(String email) throws Exception {
        user1 = new User("John", "Doe", email, "1985-12-20", Gender.MALE,
                4, "password");
        Assert.assertEquals(email, user1.getPrimaryEmail());
        user1.setUserId((long) 1);
        user1.addPassportCountry(passportCountry);
        user1.setMiddleName("Jacob");
        session1.setUser(user1);
        Assert.assertEquals(session1.getUser().getPrimaryEmail(),email);
    }

    @When("User when logged in sets fitness level to {int} and sets non mandatory passport countries to {int}")
    public void non_mandatory_fields_updated(int newFitnessLevel, int countryNum) throws Exception {
        user1.resetPassportCountries();
        user1.setFitnessLevel(newFitnessLevel);
        Assert.assertEquals(user1.getFitnessLevel(), 2);
        Assert.assertEquals(user1.getPassportCountries().size(), countryNum);
    }

    @When("User can swap initial primary email to {string}")
    public void user_can_swap_and_delete_the_initial_primary_email(String newEmail) {
        Email secondary = new Email(newEmail);
        user1.setPrimaryEmail(secondary.getEmail());
        System.out.println(user1.getPrimaryEmail());
        Assert.assertEquals(user1.getPrimaryEmail(), secondary.getEmail());
    }

    @When("User edits the name to {string}")
    public void mandatory_fields_updated(String firstName) throws Exception {
        user1.setFirstName(firstName);
    }

    @Then("User has a new primary email {string}, name {string}, fitness level {int} and {int} countries\"")
   public void user_has_a_new_primary_email(String updatedEmail, String updatedName, int fitnessLevel, int countryNum) {
        Assert.assertEquals(user1.getFirstName(), updatedName);
        Assert.assertEquals(user1.getPrimaryEmail(), updatedEmail);
        Assert.assertEquals(user1.getFitnessLevel(), fitnessLevel);
        Assert.assertEquals(user1.getPassportCountries().size(), countryNum);
    }

    @Given("User creates a new account with first name {string}, last name {string}, primary email {string}, birth date {string}")
    public void user_creates_a_new_account_with_first_name_last_name_primary_email_birth_date(String firstname, String lastname, String email, String birthDate) {
        user1 = new User(firstname, lastname, email, birthDate, Gender.FEMALE, 3, "password");
        Assert.assertEquals(email, user1.getPrimaryEmail());
        user1.setUserId((long) 1);
        user1.addPassportCountry(passportCountry);
        user1.setMiddleName("Jacob");
        session1.setUser(user1);
        Assert.assertEquals(session1.getUser().getPrimaryEmail(),email);
    }

    @When("User cannot delete mandatory field such as the first name")
    public void user_cannot_delete_mandatory_field_such_as_the_first_name() {
        user1.setFirstName("");
    }

    @When("User cannot delete mandatory field such as the last name")
    public void user_cannot_delete_mandatory_field_such_as_the_last_name() {
        user1.setLastName("");
    }

    @When("User cannot delete mandatory field such as the primary email")
    public void user_cannot_delete_mandatory_field_such_as_the_primary_email() {
        user1.setPrimaryEmail("");
    }

    @When("User cannot delete mandatory field such as the birth date")
    public void user_cannot_delete_mandatory_field_such_as_the_birth_date() {
        user1.setBirthDate(null);
    }

    @Then("The user still has first name {string}, last name {string}, primary email {string}, birth date {string}")
    public void the_user_still_has_first_name_last_namel_primary_email_birth_date(String firstname, String lastname, String email, String birthDate) {
        Assert.assertEquals(firstname, user1.getFirstName());
        Assert.assertEquals(lastname, user1.getLastName());
        Assert.assertEquals(email, user1.getPrimaryEmail());
        Assert.assertEquals(birthDate, user1.getBirthDate().toString());
    }
}
