package com.springvuegradle.hakinakina.acceptance_tests.steps;

import com.springvuegradle.hakinakina.controller.ActivityController;
import com.springvuegradle.hakinakina.controller.UserController;
import com.springvuegradle.hakinakina.repository.ActivityRepository;
import com.springvuegradle.hakinakina.repository.LocationRepository;
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
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
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
    @Mock
    private LocationRepository locationRepository;
    @InjectMocks
    private ActivityController activityController;
    @InjectMocks
    private UserController userController;


    Activity activity;
    Location location;
    User user;
    Session session;

    private final String locationJson = "{\n" +
            "  \"streetAddress\": \"street\",\n" +
            "  \"suburb\": \"suburb\",\n" +
            "  \"city\": \"city\",\n" +
            "  \"postcode\": 8041,\n" +
            "  \"state\": \"state\",\n" +
            "  \"country\": \"country\",\n" +
            "  \"latitude\": -100,\n" +
            "  \"longitude\": 200 \n" +
            "}";

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

    @Given("I am logged into an account with name {string}, email {string} and ID {int}")
    public void IAmLoggedIntoAnAccountWithName(String name, String email, int ID) {
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

    @And("I set my location to latitude {double}, longitude {double}")
    public void ISetMyLocationToLatitudeLongitude(double lat, double lng) {
        location = new Location("street", "suburb", "city", 8041,
                "state", "country", lat, lng);
        location.setId((long)1);

        user.setLocation(location);
        location.setUser(user);

        assertNotNull(user.getLocation());
        assertNotNull(location.getUser());
    }

    @When("I retrieve my location")
    public void IRetrieveMyLocation() {
        location = user.getLocation();

        assertNotNull(location);
    }

    @Then("It's latitude is {double} and it's longitude is {double}")
    public void ItsLatitudeIsAndItsLongitudeIs(double lat, double lng) {
        assertEquals(lat, location.getLatitude());
        assertEquals(lng, location.getLongitude());
    }

    @Given("I am logged into an account with name {string}, email {string} and ID {int} and have location with latitude {double}, longitude {double}")
    public void IAmLoggedIntoAnAccountWithLocation(String name, String email, int profileId, double lat, double lng) {
        user = new User(name, "Doe", email, "1995-12-20", Gender.MALE, 4, "password");
        user.setUserId((long) profileId);
        userRepository.save(user);
        String token = "T0k3n";
        session = new Session(token);
        session.setUser(user);
        sessionRepository.insertToken(token, user.getUserId());

        location = new Location("street", "suburb", "city", 8041,
                "state", "country", lat, lng);
        location.setId(1L);

        user.setLocation(location);
        location.setUser(user);

        assertEquals(email, user.getPrimaryEmail());
        assertEquals(profileId, user.getUserId());
        assertEquals(session.getUser().getPrimaryEmail(), user.getPrimaryEmail());
        assertEquals(lat, user.getLocation().getLatitude());
        assertEquals(lng, user.getLocation().getLongitude());
    }

    @And("I edit my location so that the latitude is now {double} and longitude is now {double}")
    public void iEditMyLocationSoThatTheLatitudeIsNowAndLongitudeIsNow(double lat, double lng) {
        location.setLatitude(lat);
        location.setLongitude(lng);
    }

    @When("I retrieve my new location")
    public void iRetrieveMyNewLocation() {
        Location location = user.getLocation();

        assertNotNull(location);
    }


    @Then("My location's latitude is now {double} and longitude is now {double}")
    public void myLocationSLatitudeIsNowAndLongitudeIsNow(double lat, double lng) {
        assertEquals(lat, location.getLatitude());
        assertEquals(lng, location.getLongitude());
    }

    @Given("I have account with name {string}, email {string} and ID {int} and have location with latitude {int}, longitude {int}")
    public void iHaveAccountWithNameEmailAndIDAndHaveLocationWithLatitudeLongitude(String arg0, String arg1, int arg2, int arg3, int arg4) {
    }

    @And("I make a call to the edit location endpoint so that my location is changed to {int} and {int}")
    public void iMakeACallToTheEditLocationEndpointSoThatMyLocationIsChangedToAnd(int arg0, int arg1) {
    }

    @When("I retrieve my new location using the get endpoint")
    public void iRetrieveMyNewLocationUsingTheGetEndpoint() {
    }

    @Then("The new lat is equal to {int} and new long is equal to {int}")
    public void theNewLatIsEqualToAndNewLongIsEqualTo(int arg0, int arg1) {
    }

    /*@Given("I have account with name {string}, email {string} and ID {int} and have location with latitude {double}, longitude {double}")
    public void iHaveAccountWithNameEmailAndIDAndHaveLocationWithLatitudeLongitude(String name, String email, int profileId, double lat, double lng) {
        user = new User(name, "Doe", email, "1995-12-20", Gender.MALE, 4, "password");
        user.setUserId((long) profileId);
        userRepository.save(user);
        String token = "T0k3n";
        session = new Session(token);
        session.setUser(user);
        sessionRepository.insertToken(token, user.getUserId());

        location = new Location("street", "suburb", "city", 8041,
                "state", "country", lat, lng);
        location.setId(1L);

        user.setLocation(location);
        location.setUser(user);

        userRepository.save(user);
        locationRepository.save(location);

        assertEquals(email, user.getPrimaryEmail());
        assertEquals(profileId, user.getUserId());
        assertEquals(session.getUser().getPrimaryEmail(), user.getPrimaryEmail());
        assertEquals(lat, user.getLocation().getLatitude());
        assertEquals(lng, user.getLocation().getLongitude());
    }

    @And("I make a call to the edit location endpoint so that my location is changed to {double} and {double}")
    public void iMakeACallToTheEditLocationEndpointSoThatMyLocationIsChangedToAnd(double lat, double lng) throws Exception {


        *//*final Cookie tokenCookie = new Cookie("s_id", "t0k3n");
        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(session);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userService.editLocation(any(String.class), any(String.class), any(String.class) ,any(int.class),
                any(String.class),any(String.class), any(double.class), any(double.class), any(Long.class)))
                .thenReturn(new ResponseEntity(HttpStatus.valueOf(200)));

        result = this.mockMvc.perform(put("/profiles/"+ user.getUserId() + "/location")
                .content(locationJson).cookie(tokenCookie).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200)).andReturn();*//*

        when(userRepository.findById((long)1)).thenReturn(Optional.of(user));
        userService.editLocation("street", "suburb", "city", 8041,
                "state", "country", lat, lng, 1L);
    }

    @When("I retrieve my new location using the get endpoint")
    public void iRetrieveMyNewLocationUsingTheGetEndpoint() {

        user = userRepository.getOne((long)1);
        location = user.getLocation();

        assertNotNull(location);
    }

    @Then("The new lat is equal to {double} and new long is equal to {double}")
    public void theNewLatIsEqualToAndNewLongIsEqualTo(double lat, double lng) {
        assertEquals(lat, location.getLatitude());
        assertEquals(lng, location.getLongitude());
    }*/
}
