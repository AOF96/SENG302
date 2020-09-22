package com.springvuegradle.hakinakina.acceptance_tests.steps;

import com.springvuegradle.hakinakina.controller.ActivityController;
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
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(Location.class)
@AutoConfigureWebMvc
public class LocationTestSteps {
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

    Activity activity;
    Location location;
    User user;
    Session session;

    @Before
    public void setup()
    {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(activityController).build();
    }

    @Given("I own an account with name {string}, email {string} and ID {int}")
    public void iOwnAccountWithNameEmailAndID(String name, String email, int ID) {
        user = new User(name, "Doe", email, "1995-12-20", Gender.MALE, 4, "password");
        user.setUserId((long) ID);
        userRepository.save(user);
        String token = "T0k3n";
        session = new Session(token);
        session.setUser(user);
        sessionRepository.insertToken(token, user.getUserId());

        assertEquals(email, user.getPrimaryEmail());
        assertEquals(ID, user.getUserId());
        assertEquals(session.getUser().getPrimaryEmail(), user.getPrimaryEmail());
    }

    @Given("I create an activity with ID {int}")
    public void iCreateAccountWithID(int ID) {
        activity = new Activity("Test activity", "Testing is fun :)", true, null, null);
        activity.setId((long)ID);
        Set<ActivityType> activityTypes = new HashSet<>();
        activityTypes.add(new ActivityType("Fun"));
        activity.setActivityTypes(activityTypes);
        activityRepository.save(activity);

        assertEquals(1, activity.getId());
        assertEquals("Test activity", activity.getName());
    }

    @And("the activity is located in latitude {int} and longitude {int}")
    public void theActivityIsLocatedInLatitudeAndLongitude(int latitude, int longitude) {
        location = new Location("street", "suburb", "city", 8041, "state", "country", latitude, longitude);
        location.setId((long)1);
        activity.setLocation(location);

        assertEquals(latitude, location.getLatitude());
        assertEquals(longitude, location.getLongitude());
    }

    @When("I retrieve activities that are within latitudes {int}, {int} and longitudes {int}, {int}")
    public void iRetrieveActivitiesThatAreWithinLatitudesAndLongitudes(int latBottomLeft, int latTopRight,
                                                                       int longBottomLeft, int longTopRight) throws Exception {

        List<Activity> activityList = new ArrayList<>();
        activityList.add(activity);
        final Cookie tokenCookie = new Cookie("s_id", "t0k3n");
        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(session);
        when(activityService.getActivitiesWithinGivenRange(latBottomLeft, latTopRight,
                longBottomLeft, longTopRight, user.getUserId())).thenReturn(new ResponseEntity(activityList, HttpStatus.valueOf(200)));

        result = this.mockMvc.perform(get("/activities/?latitudeTopRight=" + latTopRight + "&longitudeTopRight=" +
                longTopRight + "&latitudeBottomLeft=" + latBottomLeft + "&longitudeBottomLeft=" + longBottomLeft).cookie(tokenCookie))
                .andExpect(status().is(200)).andReturn();

    }

    @Then("the response code is {int}")
    public void theResponseCodeIs(int responseCode) throws UnsupportedEncodingException {
        assertEquals(responseCode,result.getResponse().getStatus());
    }
}
