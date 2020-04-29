package com.springvuegradle.Hakinakina.endpoints;

import com.springvuegradle.Hakinakina.controller.ActivityController;
import com.springvuegradle.Hakinakina.controller.ActivityService;
import com.springvuegradle.Hakinakina.controller.UserController;
import com.springvuegradle.Hakinakina.controller.UserService;
import com.springvuegradle.Hakinakina.entity.*;
import com.springvuegradle.Hakinakina.util.RandomToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Date;
import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ActivityController.class)
public class ActivityControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActivityService service;

    @MockBean
    private ActivityRepository activityRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PassportCountryRepository countryRepository;

    @MockBean
    private EmailRepository emailRepository;

    @MockBean
    private SessionRepository sessionRepository;

    @MockBean
    private ActivityTypeRepository activityTypeRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private Activity activity;

    private User user1;
    private User user2;
    private Session session1;
    private Session session2;
    private Activity activity1;
    private Activity activity2;

//    @BeforeEach
//    void createUsers() {
//        user1 = new User("Mayuko", "Williams",
//                "mwi@williams.com", "1970-01-01", Gender.FEMALE,
//                3, "P@ssw0rd!123");
//        User temp = userRepository.findUserByEmail("mwi@williams.com");
//        if (temp != null) {
//            sessionRepository.removeTokenByUserId(String.valueOf(temp.getUserId()));
//            userRepository.deleteById(temp.getUserId());
//        }
//        user1.setUserId((long) 1);
//
//
//        user2 = new User("Kyle", "Lang",
//                "kyle@google.com", "1990-03-04", Gender.MALE,
//                3, "P@ssw0rd!123");
//        User temp2 = userRepository.findUserByEmail("kyle@google.com");
//        if (temp != null) {
//            sessionRepository.removeTokenByUserId(String.valueOf(temp2.getUserId()));
//            userRepository.deleteById(temp2.getUserId());
//        }
//        user2.setUserId((long) 2);
//
//    }
//
//    @BeforeEach
//    void assignTokens() {
//        session1 = new Session("t0k3n1");
//        session2 = new Session("t0k3n2");
//
//        session1.setUser(user1);
//        session2.setUser(user2);
//    }
//
//    @BeforeEach
//    void assignActivities() {
//        activity1 = new Activity("Storm area 51", "Let's unfold the truth together",
//                true, new Date(2021, 10, 10), new Date(2021, 10, 11),
//                "Area 51");
//
//        activity2 = new Activity("Surf at Taylor's mistake", "Let's go for a swim together",
//                true, new Date(2021, 10, 10), new Date(2021, 10, 11),
//                "Sumner");
//
//        service.addActivity(activity1, user1.getUserId(), session1.toString());
//        service.addActivity(activity2, user2.getUserId(), session2.toString());
//    }

    @BeforeEach
    public void deleteUser() throws Exception {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
    }

    private final String INPUT = "{\n" +
            "  \"activity_name\": \"Akaroa Pier\",\n" +
            "  \"description\": \"Awesome scenery and lots of places to eat\",\n" +
            "  \"activity_type\":[ \n" +
            "    \"Fun\",\n" +
            "    \"Relaxing\"\n" +
            "  ],\n" +
            "  \"continous\": false,\n" +
            "  \"start_time\": \"2020-02-20T08:00:00+1300\", \n" +
            "  \"end_time\": \"2020-02-20T08:00:00+1300\",\n" +
            "  \"location\": \"Kaikoura, NZ\"\n" +
            "}";



    private final String EDIT_ACTIVITY_JSON = "{\n" +
            "  \"activity_name\": \"Changed name\",\n" +
            "  \"description\": \"Awesome scenery and lots of places to eat\",\n" +
            "  \"activity_type\":[ \n" +
            "    \"Fun\",\n" +
            "    \"Relaxing\"\n" +
            "  ],\n" +
            "  \"continous\": false,\n" +
            "  \"start_time\": \"2020-02-20T08:00:00+1300\", \n" +
            "  \"end_time\": \"2020-02-20T08:00:00+1300\",\n" +
            "  \"location\": \"Kaikoura, NZ\"\n" +
            "}";

    @Test
    public void addActivityTest() throws Exception {
        Session testSession = new Session("t0k3n");
        User testUser = new User("John", "Smith", "john@gmail.com", null,
                Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);
        testSession.setUser(testUser);

        // and lets have some FUN
        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(testSession);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(testUser));
        when(service.addActivity(any(Activity.class), any(Long.class), any(String.class))).thenReturn(new ResponseEntity("Activity has been created", HttpStatus.CREATED));
        this.mockMvc.perform(post("/profiles/1/activities").header("token", "t0k3n")
                .contentType(MediaType.APPLICATION_JSON)
                .content(INPUT))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Activity has been created")));
    }

    @Test
    public void editActivityTest() throws Exception {
        Session testSession = new Session("t0k3n");

        // add test user
        User testUser = new User("John", "Smith", "john@gmail.com", null, Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);

        // add test activity and connect it to the test user
        java.util.Date date = new java.util.Date();
        java.sql.Date startTime = new java.sql.Date(date.getTime());
        java.sql.Date endTime = new java.sql.Date(date.getTime()+1000);
        Activity testActivity = new Activity("name", "description", false, startTime, endTime, "location");

        testActivity.setId((long) 1);
        Set<ActivityType> activityTypes = new HashSet<ActivityType>();
        activityTypes.add(new ActivityType("Fun"));
        testActivity.setActivityTypes(activityTypes);

        Activity newActivity = activityRepository.save(testActivity);
        activityRepository.insertActivityForUser((long) 1, (long) 1);

        testSession.setUser(testUser);
        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(testSession);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(testUser));
        when(activityRepository.findActivityById((long) 1)).thenReturn(newActivity);
        when(service.editActivity(any(Activity.class), any(Long.class), any(Long.class), any(String.class))).thenReturn(new ResponseEntity("Activity has been updated", HttpStatus.OK));

        this.mockMvc.perform(put("/profiles/1/activities/1").header("token", "t0k3n")
                .contentType(MediaType.APPLICATION_JSON)
                .content(EDIT_ACTIVITY_JSON))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("Activity has been updated")));
    }

    public List<Map<String, String>> createActivitySummariesMap() {
        List<Map<String, String>> summaries = new ArrayList<>();

        Map<String, String> map1 = new HashMap<>();
        map1.put("name", "Activity 1");
        map1.put("description", "An activity called Activity 1");
        map1.put("id", "1");
        summaries.add(map1);

        Map<String, String> map2 = new HashMap<>();
        map2.put("name", "Activity 2");
        map2.put("description", "An activity called Activity 2");
        map2.put("id", "2");
        summaries.add(map2);

        return summaries;
    }

    @Test
    public void getContinuousActivitiesTest() throws Exception {
        List<Activity> dummyList = new ArrayList<>();
        List<Map<String, String>> summaries = createActivitySummariesMap();
        when(activityRepository.getActivitiesForUserOfType(true, (long) 1)).thenReturn(dummyList);
        when(service.getActivitySummaries(dummyList)).thenReturn(summaries);

        this.mockMvc.perform(get("/profiles/1/activities/continuous")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("[{\"name\":\"Activity 1\"," +
                        "\"description\":\"An activity called Activity 1\",\"id\":\"1\"}," +
                        "{\"name\":\"Activity 2\",\"description\":\"An activity called Activity 2\",\"id\":\"2\"}]")));
    }

    @Test
    public void getDurationsActivitiesTest() throws Exception {
        List<Activity> dummyList = new ArrayList<>();
        List<Map<String, String>> summaries = createActivitySummariesMap();
        when(activityRepository.getActivitiesForUserOfType(false, (long) 1)).thenReturn(dummyList);
        when(service.getActivitySummaries(dummyList)).thenReturn(summaries);

        this.mockMvc.perform(get("/profiles/1/activities/duration")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("[{\"name\":\"Activity 1\"," +
                        "\"description\":\"An activity called Activity 1\",\"id\":\"1\"}," +
                        "{\"name\":\"Activity 2\",\"description\":\"An activity called Activity 2\",\"id\":\"2\"}]")));
    }

//    @Test
//    public void deleteActivityErrorHandlingTest() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.delete("/profiles/{userId}/activities/{activityId}", user1.getUserId(), 10)  )
//                .andExpect(status().isNotFound());
//    }
}