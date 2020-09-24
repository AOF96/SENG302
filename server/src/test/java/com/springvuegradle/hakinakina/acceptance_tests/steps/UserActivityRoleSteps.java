package com.springvuegradle.hakinakina.acceptance_tests.steps;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.hakinakina.controller.ActivityController;
import com.springvuegradle.hakinakina.controller.UserController;
import com.springvuegradle.hakinakina.dto.EditActivityRoleDto;
import com.springvuegradle.hakinakina.dto.EditSubscriberRoleDto;
import com.springvuegradle.hakinakina.repository.*;
import com.springvuegradle.hakinakina.service.ActivityService;
import com.springvuegradle.hakinakina.service.UserService;
import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.util.ResponseHandler;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.coyote.Response;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
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
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@WebMvcTest(User.class)
@AutoConfigureWebMvc
public class UserActivityRoleSteps {
    @Autowired
    private MockMvc mockMvc;

    private MvcResult result;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserActivityRoleRepository userActivityRoleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private ActivityService activityService;

    @InjectMocks
    private ActivityController activityController;

    @InjectMocks
    private UserController userController;

    private User mayuko = new User();
    private User fabian = new User();
    private Activity mayukosActivity = new Activity();
    private UserActivityKey activityKey = new UserActivityKey();

    private User testUser;
    private Activity testActivity;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(userController).build();
    }

    @BeforeEach
    public void clear() {
        userActivityRoleRepository.deleteAll();
        activityRepository.deleteAll();
        userRepository.deleteAll();
    }

    void setupUser(User user, String fName, String mName, String lName, String email, int pLevel) {
        user.setFirstName(fName);
        user.setMiddleName(mName);
        user.setLastName(lName);
        user.setPrimaryEmail(email);
        user.setPermissionLevel(pLevel);
        user.setUserId((long) 1);
        userRepository.save(user);

        Session testSession = new Session(fName); // your first name is your token
        testSession.setUser(user);
        sessionRepository.save(testSession);
    }

    void setupActivity(Activity activity) {
        Date startDate1 = new Date(2021, 10, 10);
        Date endDate1 = new Date(2021, 10, 12);
        activity.setName("Climb Mount Everest");
        activity.setDescription("Let's climb Mount Everest together");
        activity.setContinuous(true);
        activity.setStartTime(new Timestamp(startDate1.getTime()));
        activity.setEndTime(new Timestamp(endDate1.getTime()));
        Set<ActivityType> activityTypes = Set.of(new ActivityType("Extreme"));
        activity.setActivityTypes(activityTypes);
        activity.setId((long) 1);

        activityRepository.save(mayukosActivity);
    }

    public User createUser(String fName, String lName, String email) {
        User user = new User();
        user.setFirstName(fName);
        user.setLastName(lName);
        user.setPrimaryEmail(email);
        return user;
    }

    public Activity createActivity(String name) {
        Activity activity = new Activity();
        Date startDate1 = new Date(2021, 10, 10);
        Date endDate1 = new Date(2021, 10, 12);
        activity.setName("Climb Mount Everest");
        activity.setDescription("Let's climb Mount Everest together");
        activity.setContinuous(true);
        activity.setStartTime(new Timestamp(startDate1.getTime()));
        activity.setEndTime(new Timestamp(endDate1.getTime()));
        Set<ActivityType> activityTypes = Set.of(new ActivityType("Extreme"));
        activity.setActivityTypes(activityTypes);
        return activity;
    }

    @Given("I am a participant of an activity")
    public void iAmTheParticipantOfAnActivity() {
        setupUser(mayuko, "Mayuko", null, "Williams", "mayuko@acnh.com", 0);
        setupUser(fabian, "Fabian", null, "Gibson", "gibson@acnh.com", 0);
        setupActivity(mayukosActivity);
        activityService.addActivity(mayukosActivity, mayuko.getUserId(), "Mayuko");

        Session testSession = new Session("Mayuko");
        testSession.setUser(mayuko);

        UserActivityRole activityRole = new UserActivityRole();

        activityKey.setActivityId(mayukosActivity.getId());
        activityKey.setUserId(mayuko.getUserId());

        activityRole.setId(activityKey);
        activityRole.setActivity(mayukosActivity);
        activityRole.setUser(mayuko);
        activityRole.setActivityRole(ActivityRole.PARTICIPANT);
        Set<UserActivityRole> userActivityRoleSet = new HashSet<>();
        userActivityRoleSet.add(activityRole);

        mayukosActivity.setUserActivityRoles(userActivityRoleSet);
        mayuko.setUserActivityRoles(userActivityRoleSet);

        userRepository.save(mayuko);
        activityRepository.save(mayukosActivity);
        userActivityRoleRepository.save(activityRole);
    }

    @When("I opt out of that activity role")
    public void IOptOutOfThatActivityRole() throws Exception {
        activityService.optOutOfActivity(mayukosActivity.getId(), mayuko.getUserId());
    }

    @Then("I am no longer a participant of that activity")
    public void IAmNoLongerAParticipantOfThatActivity() {
        Optional<UserActivityRole> activityRole = userActivityRoleRepository.getByActivityAndUser(mayukosActivity, mayuko);
        assertTrue(activityRole.isEmpty());
    }

    @Given("I have an account with the email {string}")
    public void iHaveAnAccount(String email) {
        testUser = createUser("Test", "Test", email);
    }

    @And("I am looking at another user's activity")
    public void iAmLookingAtAnotherUserSActivity() {
        testActivity = createActivity("Test");
    }

    @When("I choose to become a participant of that activity")
    public void iChooseToBecomeAParticipantOfThatActivity() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "token");

        EditActivityRoleDto editActivityRoleDto = new EditActivityRoleDto(new EditSubscriberRoleDto(testUser.getPrimaryEmail(), ActivityRole.PARTICIPANT));

        doNothing().when(userService).editUserActivityRole(any(Long.class), any(Long.class), any(EditActivityRoleDto.class), any(String.class));
        result = mockMvc.perform(put("/profiles/1/activities/1/subscriber")
                .cookie(tokenCookie)
                .content(editActivityRoleDto.toString()).contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("I am a participant of the activity")
    public void iAmAParticipantOfTheActivity() throws UnsupportedEncodingException {
        assertEquals(200, result.getResponse().getStatus());
    }

    @When("I choose to follow that activity")
    public void iChooseToFollowThatActivity() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "token");

        EditActivityRoleDto editActivityRoleDto = new EditActivityRoleDto(new EditSubscriberRoleDto(testUser.getPrimaryEmail(), ActivityRole.FOLLOWER));

        doNothing().when(userService).editUserActivityRole(any(Long.class), any(Long.class), any(EditActivityRoleDto.class), any(String.class));
        result = mockMvc.perform(put("/profiles/1/activities/1/subscriber")
                .cookie(tokenCookie)
                .content(editActivityRoleDto.toString()).contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("I am a follower of the activity")
    public void iAmAFollowerOfTheActivity() {
        assertEquals(200, result.getResponse().getStatus());
    }

    @And("I have an activity")
    public void iHaveAnActivity() {
        testActivity = createActivity("Test");
    }

    @When("I change a participant to be an organiser")
    public void iChangeAParticipantToBeAnOrganiser() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "token");

        EditActivityRoleDto editActivityRoleDto = new EditActivityRoleDto(new EditSubscriberRoleDto(testUser.getPrimaryEmail(), ActivityRole.ORGANISER));

        doNothing().when(userService).editUserActivityRole(any(Long.class), any(Long.class), any(EditActivityRoleDto.class), any(String.class));
        result = mockMvc.perform(put("/profiles/1/activities/1/subscriber")
                .cookie(tokenCookie)
                .content(editActivityRoleDto.toString()).contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("That user is an organiser")
    public void thatUserIsAnOrganiser() {
        assertEquals(200, result.getResponse().getStatus());
    }
}
