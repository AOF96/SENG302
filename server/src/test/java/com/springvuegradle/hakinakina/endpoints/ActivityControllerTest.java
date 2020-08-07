package com.springvuegradle.hakinakina.endpoints;

import com.springvuegradle.hakinakina.controller.ActivityController;
import com.springvuegradle.hakinakina.service.ActivityService;
import com.springvuegradle.hakinakina.service.UserService;
import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.repository.*;
import io.cucumber.java.en_old.Ac;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.Cookie;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private SearchRepository searchRepository;

    @MockBean
    private UserService userService;


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

    private Activity createTestActivity() {
        // add test activity and connect it to the test user
        // it should prints using toJson method as following
        //{"id":1,"users":[],"activity_name":"name","description":"description","activity_type":[{"name":"Fun","users":[]}],"continuous":false,"start_time":1000000000,"end_time":1000001000,"location":"location"}
        java.util.Date date = new java.util.Date();
        long time = 1000000000;
        java.sql.Date startTime = new java.sql.Date(time);
        java.sql.Date endTime = new java.sql.Date(time+1000);
        Activity testActivity = new Activity("name", "description", false,
                new Timestamp(startTime.getTime()), new Timestamp(endTime.getTime()), "location");

        testActivity.setId((long) 1);
        Set<ActivityType> activityTypes = new HashSet<>();
        activityTypes.add(new ActivityType("Fun"));
        testActivity.setActivityTypes(activityTypes);
        return testActivity;
    }

    @Test
    public void getOneActivitySuccessTest() throws Exception {
        Activity testActivity = createTestActivity();

        String activityStr = "{\"id\":1,\"userActivityRoles\":null,\"usersShared\":[],\"visibility\":null,\"activity_name\":\"name\",\"description\":\"description\",\"activity_type\":[{\"name\":\"Fun\",\"users\":[]}],\"continuous\":false,\"start_time\":1000000000,\"end_time\":1000001000,\"location\":\"location\"}";
        when(activityRepository.findById((long) 1)).thenReturn(Optional.of(testActivity));
        this.mockMvc.perform(get("/activities/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(activityStr)));
    }

    @Test
    public void getOneActivityFailTest() throws Exception {
        when(activityRepository.findById((long) 1)).thenReturn(Optional.empty());
        this.mockMvc.perform(get("/activities/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Activity does not exist")));
    }

    @Test
    public void addActivityTest() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "t0k3n");
        Session testSession = new Session("t0k3n");
        User testUser = new User("John", "Smith", "john@gmail.com", null,
                Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);
        testSession.setUser(testUser);

        // and lets have some FUN
        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(testSession);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(testUser));
        when(service.addActivity(any(Activity.class), any(Long.class), any(String.class))).thenReturn(new ResponseEntity("Activity has been created", HttpStatus.CREATED));
        this.mockMvc.perform(post("/profiles/1/activities").cookie(tokenCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(INPUT))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Activity has been created")));
    }

    @Test
    public void editActivityTest() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "t0k3n");
        Session testSession = new Session("t0k3n");

        // add test user
        User testUser = new User("John", "Smith", "john@gmail.com", null, Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);

        Activity newActivity = activityRepository.save(createTestActivity());
        activityRepository.insertActivityForUser((long) 1, (long) 1);

        testSession.setUser(testUser);
        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(testSession);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(testUser));
        when(activityRepository.findActivityById((long) 1)).thenReturn(newActivity);
        when(service.editActivity(any(Activity.class), any(Long.class), any(Long.class), any(String.class))).thenReturn(new ResponseEntity("Activity has been updated", HttpStatus.OK));

        this.mockMvc.perform(put("/profiles/1/activities/1").cookie(tokenCookie)
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

    @Test
    public void deleteActivityErrorHandlingTest() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "t0k3n");
        Session session1 = new Session("t0k3n");

        User user1 = new User("John", "Smith", "john@gmail.com", null, Gender.MALE, 2, "Password1");
        user1.setUserId((long) 1);
        Date startTime = new Date(2021, 10, 10);
        Date endTime = new Date(2021, 10, 11);
        Activity activity1 = new Activity("Storm area 51", "Let's unfold the truth together",
               true, new Timestamp(startTime.getTime()), new Timestamp(endTime.getTime()),
                "Area 51");

        activity1.setId((long) 1);
        Set<ActivityType> activityTypes = new HashSet<ActivityType>();
        activityTypes.add(new ActivityType("Fun"));
        activity1.setActivityTypes(activityTypes);

        Activity newActivity = activityRepository.save(activity1);
        activityRepository.insertActivityForUser((long) 1, (long) 1);

        session1.setUser(user1);
        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(session1);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(user1));
        when(activityRepository.findActivityById((long) 1)).thenReturn(newActivity);
        when(service.removeActivity(any(Long.class), any(Long.class), any(String.class))).thenReturn(new ResponseEntity("Activity successfully deleted", HttpStatus.OK));

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/profiles/1/activities/1").cookie(tokenCookie))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("Activity successfully deleted")));

    }

    @Test
    public void getSharedUsersTest() throws Exception {
        Activity activity = createTestActivity();

        User user1 = new User("Jack", "Ryan", "jack@gmail.com", null, Gender.MALE, 2, "Password1");
        user1.setUserId((long) 1);

        User user2 = new User("John", "Smith", "john@gmail.com", null, Gender.MALE, 2, "Password1");
        user2.setUserId((long) 2);

        String testResponse = "{\n" +
                "    \"content\": [\n" +
                "        [\n" +
                "            1,\n" +
                "            \"Jack\",\n" +
                "            \"Ryan\"\n" +
                "        ],\n" +
                "        [\n" +
                "            2,\n" +
                "            \"John\",\n" +
                "            \"Smith\"\n" +
                "        ]\n" +
                "    ],\n" +
                "    \"pageable\": {\n" +
                "        \"sort\": {\n" +
                "            \"sorted\": false,\n" +
                "            \"unsorted\": true,\n" +
                "            \"empty\": true\n" +
                "        },\n" +
                "        \"pageNumber\": 0,\n" +
                "        \"pageSize\": 3,\n" +
                "        \"offset\": 0,\n" +
                "        \"paged\": true,\n" +
                "        \"unpaged\": false\n" +
                "    },\n" +
                "    \"totalPages\": 1,\n" +
                "    \"totalElements\": 2,\n" +
                "    \"last\": true,\n" +
                "    \"first\": true,\n" +
                "    \"sort\": {\n" +
                "        \"sorted\": false,\n" +
                "        \"unsorted\": true,\n" +
                "        \"empty\": true\n" +
                "    },\n" +
                "    \"number\": 0,\n" +
                "    \"numberOfElements\": 2,\n" +
                "    \"size\": 3,\n" +
                "    \"empty\": false\n" +
                "}";

        when(userRepository.findById((long) 1)).thenReturn(Optional.of(user1));
        when(userRepository.findById((long) 2)).thenReturn(Optional.of(user2));
        when(activityRepository.getOne(anyLong())).thenReturn(activity);
        when(service.getSharedUsers(any(Long.class), any(int.class), any(int.class))).thenReturn(new ResponseEntity(testResponse, HttpStatus.OK));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/activities/1/shared/?page= +" + 0 + "&size=" + 3))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString(testResponse)));

    }

    @Test
    void testGettingSharedUsersWithInvalidPageSize() throws Exception {
        Activity activity = createTestActivity();
        mockMvc.perform(get("/activities/" + activity.getId() + "/shared/?page= +" + -1 + "&size=" + 3)
                .param("page", "-1")
                .param("size", "3"))
                .andExpect(status().isBadRequest());

    }

    @Test
    void testGettingSharedUsersWithInvalidPageAndSize() throws Exception {
        Activity activity = createTestActivity();
        activity.setId((long) 2);
        mockMvc.perform(get("/activities/" + activity.getId() + "/shared/?page= +" + -1 + "&size=" + -1)
                .param("page", "-1")
                .param("size", "-1"))
                .andExpect(status().isBadRequest());

    }
    @Test
    public void getActivityOrganizersTest() throws Exception {
        Activity activity = createTestActivity();

        User user1 = new User("Jack", "Ryan", "jack@gmail.com", null, Gender.MALE, 2, "Password1");
        user1.setUserId((long) 1);

        User user2 = new User("John", "Smith", "john@gmail.com", null, Gender.MALE, 2, "Password1");
        user2.setUserId((long) 2);

                String testResponse = "{\n" +
                        "    \"content\": [\n" +
                        "        [\n" +
                        "            1,\n" +
                        "            \"Jack\",\n" +
                        "            \"Ryan\"\n" +
                        "        ],\n" +
                        "        [\n" +
                        "            2,\n" +
                        "            \"John\",\n" +
                        "            \"Smith\"\n" +
                        "        ]\n" +
                        "    ],\n" +
                        "    \"pageable\": {\n" +
                        "        \"sort\": {\n" +
                        "            \"sorted\": false,\n" +
                        "            \"unsorted\": true,\n" +
                        "            \"empty\": true\n" +
                        "        },\n" +
                        "        \"pageNumber\": 0,\n" +
                        "        \"pageSize\": 3,\n" +
                        "        \"offset\": 0,\n" +
                        "        \"paged\": true,\n" +
                        "        \"unpaged\": false\n" +
                        "    },\n" +
                        "    \"totalPages\": 1,\n" +
                        "    \"totalElements\": 2,\n" +
                        "    \"last\": true,\n" +
                        "    \"first\": true,\n" +
                        "    \"sort\": {\n" +
                        "        \"sorted\": false,\n" +
                        "        \"unsorted\": true,\n" +
                        "        \"empty\": true\n" +
                        "    },\n" +
                        "    \"number\": 0,\n" +
                        "    \"numberOfElements\": 2,\n" +
                        "    \"size\": 3,\n" +
                        "    \"empty\": false\n" +
                        "}";

        when(userRepository.findById((long) 1)).thenReturn(Optional.of(user1));
        when(userRepository.findById((long) 2)).thenReturn(Optional.of(user2));
        when(activityRepository.findActivityById((long) 1)).thenReturn(activity);
        when(service.getActivityOrganizers(any(Long.class), any(int.class), any(int.class))).thenReturn(new ResponseEntity(testResponse, HttpStatus.OK));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/activities/" + activity.getId() + "/organizers/?page= +" + 0 + "&size=" + 3))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString(testResponse)));

    }

    @Test
    void testGettingOrganizersWithInvalidPageSize() throws Exception {
        Activity activity = createTestActivity();
        mockMvc.perform(get("/activities/" + activity.getId() + "/organizers/?page= +" + -1 + "&size=" + 3)
                .param("page", "-1")
                .param("size", "3"))
                .andExpect(status().isBadRequest());

    }

    @Test
    void testGettingOrganizersWithInvalidPageAndSize() throws Exception {
        Activity activity = createTestActivity();
        activity.setId((long) 2);
        mockMvc.perform(get("/activities/" + activity.getId() + "/organizers/?page= +" + -1 + "&size=" + -1)
                .param("page", "-1")
                .param("size", "-1"))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void getActivityParticipantsTest() throws Exception {
        Activity activity = createTestActivity();

        User user1 = new User("Jack", "Ryan", "jack@gmail.com", null, Gender.MALE, 2, "Password1");
        user1.setUserId((long) 1);

        User user2 = new User("John", "Smith", "john@gmail.com", null, Gender.MALE, 2, "Password1");
        user2.setUserId((long) 2);

        String testResponse = "{\n" +
                "    \"content\": [\n" +
                "        [\n" +
                "            1,\n" +
                "            \"Jack\",\n" +
                "            \"Ryan\"\n" +
                "        ],\n" +
                "        [\n" +
                "            2,\n" +
                "            \"John\",\n" +
                "            \"Smith\"\n" +
                "        ]\n" +
                "    ],\n" +
                "    \"pageable\": {\n" +
                "        \"sort\": {\n" +
                "            \"sorted\": false,\n" +
                "            \"unsorted\": true,\n" +
                "            \"empty\": true\n" +
                "        },\n" +
                "        \"pageNumber\": 0,\n" +
                "        \"pageSize\": 3,\n" +
                "        \"offset\": 0,\n" +
                "        \"paged\": true,\n" +
                "        \"unpaged\": false\n" +
                "    },\n" +
                "    \"totalPages\": 1,\n" +
                "    \"totalElements\": 2,\n" +
                "    \"last\": true,\n" +
                "    \"first\": true,\n" +
                "    \"sort\": {\n" +
                "        \"sorted\": false,\n" +
                "        \"unsorted\": true,\n" +
                "        \"empty\": true\n" +
                "    },\n" +
                "    \"number\": 0,\n" +
                "    \"numberOfElements\": 2,\n" +
                "    \"size\": 3,\n" +
                "    \"empty\": false\n" +
                "}";

        when(userRepository.findById((long) 1)).thenReturn(Optional.of(user1));
        when(userRepository.findById((long) 2)).thenReturn(Optional.of(user2));
        when(activityRepository.findActivityById((long) 1)).thenReturn(activity);
        when(service.getActivityParticipants(any(Long.class), any(int.class), any(int.class))).thenReturn(new ResponseEntity(testResponse, HttpStatus.OK));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/activities/" + activity.getId() + "/participants/?page= +" + 0 + "&size=" + 3))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString(testResponse)));

    }

    @Test
    void testGettingParticipantsWithInvalidPageSize() throws Exception {
        Activity activity = createTestActivity();
        mockMvc.perform(get("/activities/" + activity.getId() + "/participants/?page= +" + -1 + "&size=" + 3)
                .param("page", "-1")
                .param("size", "3"))
                .andExpect(status().isBadRequest());

    }

    @Test
    void testGettingParticipantsWithInvalidPageAndSize() throws Exception {
        Activity activity = createTestActivity();
        activity.setId((long) 2);
        mockMvc.perform(get("/activities/" + activity.getId() + "/participants/?page= +" + -1 + "&size=" + -1)
                .param("page", "-1")
                .param("size", "-1"))
                .andExpect(status().isBadRequest());

    }

}