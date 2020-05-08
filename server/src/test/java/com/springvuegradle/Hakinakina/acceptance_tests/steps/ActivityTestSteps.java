package com.springvuegradle.Hakinakina.acceptance_tests.steps;

import com.springvuegradle.Hakinakina.controller.ActivityController;
import com.springvuegradle.Hakinakina.controller.ActivityService;
import com.springvuegradle.Hakinakina.controller.UserService;
import com.springvuegradle.Hakinakina.entity.*;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.Mockito.when;



@WebMvcTest(Activity.class)
@AutoConfigureWebMvc
public class ActivityTestSteps {

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
    private ActivityController activityController;

    private User user1;
    private User user;
    Session session;

    Session session1 = new Session("t0k3n");
    Activity activity;
    Activity activity1;



    @Before
    public void setup()
    {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(activityController).build();
    }

    @Given("I create an account with name {string}, email {string} and ID {int}")
    public void iCreateAccountWithNameEmailAndID(String name, String email, int ID) throws Exception {
        user = new User(name, "Doe", email, "1995-12-20", Gender.MALE, 4, "password");
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

    @When("I create the following activity: {string} {string} {string} {string} {string} {string} {string}")
    public void iCreateTheFollowingActivity(
            String activity_name, String description, String activity_types, String continuous, String start_time,
            String end_time, String location) throws Exception {

        String request = "{\n" +
                "  \"activity_name\": \"" + activity_name + "\",\n" +
                "  \"description\": \"" + description + "\",\n" +
                "  \"activity_type\":[ \n" +
                "    \"" + activity_types + "\" \n" +
                "  ],\n" +
                "  \"continous\": " + continuous + ",\n" +
                "  \"start_time\": \"" + start_time + "\", \n" +
                "  \"end_time\": \"" + end_time + "\",\n" +
                "  \"location\": \"" + location + "\"\n" +
                "}";

        System.out.println(request);
        when(activityService.addActivity(any(Activity.class), any(Long.class), any(String.class))).thenReturn(new ResponseEntity("Activity has been created", HttpStatus.CREATED));;
        result = mockMvc.perform(post("/profiles/" + user.getUserId() + "/activities")
                .header("token", session)
                .content(request).contentType(MediaType.APPLICATION_JSON)).andReturn();
        System.out.println(result.getResponse().getStatus());
    }

    @Then("the response status is {int}")
    public void theResponseStatusIsCode(int statusCode) {
        assertEquals(statusCode, result.getResponse().getStatus());
    }

    @When("I create the activity: {string} {string} {string} {string} {string} {string} {string} with token {string}")
    public void iCreateTheActivityWithToken(
            String activity_name, String description, String activity_types, String continuous, String start_time,
            String end_time, String location, String token) throws Exception {

        String request = "{\n" +
                "  \"activity_name\": \"" + activity_name + "\",\n" +
                "  \"description\": \"" + description + "\",\n" +
                "  \"activity_type\":[ \n" +
                "    \"" + activity_types + "\" \n" +
                "  ],\n" +
                "  \"continous\": " + continuous + ",\n" +
                "  \"start_time\": \"" + start_time + "\", \n" +
                "  \"end_time\": \"" + end_time + "\",\n" +
                "  \"location\": \"" + location + "\"\n" +
                "}";

        when(activityService.addActivity(any(Activity.class), any(Long.class), any(String.class))).thenReturn(new ResponseEntity("Invalid Session", HttpStatus.FORBIDDEN));;
        result = mockMvc.perform(post("/profiles/" + user.getUserId() + "/activities").header("token", token)
                .content(request).contentType(MediaType.APPLICATION_JSON)).andReturn();
        String test = "/profiles/" + user.getUserId() + "/activities";
        System.out.println(test);
        System.out.println(result.getResponse().getStatus());
    }




//    @When("user {int} activities are retrieved")
//    public void userActivitiesAreRetrieved(int userId) {
//        user1.
//        assertTrue(true);
//    }

//    @Then("exactly {int} activity should be returned")
//    public void exactlyActivityShouldBeReturned(int expectedNumOfAcitivities) {
//        assertTrue(true);
//    }




    @And("I create an activity with name {string}, description {string} and ID {long}")
    public void i_create_an_activity(String name, String description, long id) throws Exception {
        activity1 = new Activity(name, description,
                true, new Date(2021, 10, 10), new Date(2021, 10, 11),
                "Park");

        activity1.setId(id);
        Set<ActivityType> activityTypes = new HashSet<ActivityType>();
        activityTypes.add(new ActivityType("Fun"));
        activity1.setActivityTypes(activityTypes);

        Activity newActivity = activityRepository.save(activity1);
        activityRepository.insertActivityForUser((long) 1, id);

        assertEquals(name, activity1.getName());
    }

    @When("I delete the created activity")
    public void i_delete_the_created_activity() {
        activityService.removeActivity(user1.getUserId(), activity1.getId(), "t0k3n");
    }

    @Then("The created activity with ID {long} no longer exists")
    public void activityNoLongerExists(long ID) {
        assertNull(activityRepository.findActivityById(ID));
    }





}
