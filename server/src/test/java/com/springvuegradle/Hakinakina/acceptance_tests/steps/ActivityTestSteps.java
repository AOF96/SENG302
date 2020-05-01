package com.springvuegradle.Hakinakina.acceptance_tests.steps;

import com.springvuegradle.Hakinakina.entity.Activity;
import com.springvuegradle.Hakinakina.entity.ActivityRepository;
import com.springvuegradle.Hakinakina.entity.UserRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class ActivityTestSteps {
    private ActivityRepository activityRepository;
    private UserRepository userRepository;

    @Before
    public void resetLocalFields() throws SQLException {
        activityRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Given("that user {int} adds the following activity")
    public void theUserAddsTheFollowingActivity(io.cucumber.datatable.DataTable dataTable) throws Exception {
        assertTrue(true);
    }

    @When("user {int} activities are retrieved")
    public void userActivitiesAreRetrieved(int userId) {
        assertTrue(true);
    }

    @Then("exactly {int} activity should be returned")
    public void exactlyActivityShouldBeReturned(int expectedNumOfAcitivities) {
        assertTrue(true);
    }
}
