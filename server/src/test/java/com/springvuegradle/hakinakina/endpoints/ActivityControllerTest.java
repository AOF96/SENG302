package com.springvuegradle.hakinakina.endpoints;

import com.springvuegradle.hakinakina.controller.ActivityController;
import com.springvuegradle.hakinakina.dto.SearchUserDto;
import com.springvuegradle.hakinakina.service.ActivityService;
import com.springvuegradle.hakinakina.service.SearchService;
import com.springvuegradle.hakinakina.service.UserService;
import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.repository.*;
import com.springvuegradle.hakinakina.util.ResponseHandler;
import org.hibernate.sql.Delete;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.Cookie;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private HomeFeedRepository homeFeedRepository;

    @MockBean
    private UserActivityRoleRepository userActivityRoleRepository;

    @MockBean
    private AchievementRepository achievementRepository;

    @MockBean
    private ResultRepository resultRepository;

    @MockBean
    private LocationRepository locationRepository;

    @MockBean
    private UserService userService;

    @Mock
    private SearchService searchService;

    @BeforeEach
    public void deleteUser() throws Exception {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
    }

    //TODO: Add the location fields back to these String inputs where the deserializer is updated
    private final String INPUT = "{\n" +
            "  \"activity_name\": \"Akaroa Pier\",\n" +
            "  \"description\": \"Awesome scenery and lots of places to eat\",\n" +
            "  \"activity_type\":[ \n" +
            "    \"Fun\",\n" +
            "    \"Relaxing\"\n" +
            "  ],\n" +
            "  \"continous\": false,\n" +
            "  \"start_time\": \"2020-02-20T08:00:00+1300\", \n" +
            "  \"end_time\": \"2020-02-20T08:00:00+1300\"" +
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
            "  \"end_time\": \"2020-02-20T08:00:00+1300\"" +
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
                new Timestamp(startTime.getTime()), new Timestamp(endTime.getTime()));

        testActivity.setId((long) 1);
        Set<ActivityType> activityTypes = new HashSet<>();
        activityTypes.add(new ActivityType("Fun"));
        testActivity.setActivityTypes(activityTypes);
        return testActivity;
    }

    @Test
    public void getOneActivitySuccessTest() throws Exception {
        Activity testActivity = createTestActivity();

        String activityStr = "{\"id\":1,\"achievements\":[],\"author\":null,\"visibility\":null,\"location\":null,\"activity_name\":\"name\",\"description\":\"description\",\"activity_type\":[{\"name\":\"Fun\",\"users\":[]}],\"continuous\":false,\"start_time\":1000000000,\"end_time\":1000001000";
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
        when(service.addActivity(any(Activity.class), any(Long.class), any(String.class))).thenReturn(new ResponseEntity("1", HttpStatus.CREATED));
        this.mockMvc.perform(post("/profiles/1/activities").cookie(tokenCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(INPUT))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("1")));
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
        final Cookie tokenCookie = new Cookie("s_id", "t0k3n");
        Session testSession = new Session("t0k3n");
        User testUser = new User("John", "Smith", "john@gmail.com", null, Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);
        testSession.setUser(testUser);
        List<Activity> dummyList = new ArrayList<>();
        List<Map<String, String>> summaries = createActivitySummariesMap();
        when(activityRepository.getActivitiesForUserOfType(true, (long) 1)).thenReturn(dummyList);
        when(service.getActivitySummaries(dummyList)).thenReturn(summaries);

        this.mockMvc.perform(get("/profiles/1/activities/continuous").cookie(tokenCookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("[{\"name\":\"Activity 1\"," +
                        "\"description\":\"An activity called Activity 1\",\"id\":\"1\"}," +
                        "{\"name\":\"Activity 2\",\"description\":\"An activity called Activity 2\",\"id\":\"2\"}]")));
    }

    @Test
    public void getDurationsActivitiesTest() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "t0k3n");
        Session testSession = new Session("t0k3n");
        User testUser = new User("John", "Smith", "john@gmail.com", null, Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);
        testSession.setUser(testUser);

        List<Activity> dummyList = new ArrayList<>();
        List<Map<String, String>> summaries = createActivitySummariesMap();
        when(activityRepository.getActivitiesForUserOfType(false, (long) 1)).thenReturn(dummyList);
        when(service.getActivitySummaries(dummyList)).thenReturn(summaries);

        this.mockMvc.perform(get("/profiles/1/activities/duration").cookie(tokenCookie)
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
               true, new Timestamp(startTime.getTime()), new Timestamp(endTime.getTime()));

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
    public void unFollowActivityEndpointTest() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "t0k3n");
        Session session1 = new Session("t0k3n");

        User testUser2 = new User("John", "Smith", "john2@gmail.com", null, Gender.MALE, 2, "Password1");
        testUser2.setUserId((long) 2);

        Activity newActivity = createTestActivity();

        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(session1);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(testUser2));
        when(activityRepository.findActivityById((long) 1)).thenReturn(newActivity);
        when(service.unFollow(any(Long.class), any(Long.class), any(String.class))).
                thenReturn(new ResponseEntity<String>("Unfollowed activity", HttpStatus.OK));

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/profiles/2/subscriptions/activities/1").cookie(tokenCookie))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("Unfollowed activity")));
    }

    @Test
    public void isFollowingActivityEndpointTest() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "t0k3n");
        Session session1 = new Session("t0k3n");

        User testUser2 = new User("John", "Smith", "john2@gmail.com", null, Gender.MALE, 2, "Password1");
        testUser2.setUserId((long) 2);

        Activity newActivity = createTestActivity();

        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(session1);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(testUser2));
        when(activityRepository.findActivityById((long) 1)).thenReturn(newActivity);
        when(service.checkFollowing(any(Long.class), any(Long.class), any(String.class))).
                thenReturn(new ResponseEntity<String>(Boolean.toString(true), HttpStatus.OK));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/profiles/2/subscriptions/activities/1").cookie(tokenCookie))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("true")));
    }

    @Test
    public void addAchievement() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "t0k3n");
        Session testSession = new Session("t0k3n");
        User testUser = new User("John", "Smith", "john@gmail.com", null,
                Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);
        testSession.setUser(testUser);
        Activity testActivity = createTestActivity();
        testActivity.setId((long) 1);

        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(testSession);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(testUser));
        when(activityRepository.findActivityById((long) 1)).thenReturn(testActivity);
        when(service.addAchievement(any(Achievement.class), any(Long.class), any(Long.class), any(String.class))).thenReturn(new ResponseEntity("Achievement added successfully", HttpStatus.CREATED));
        this.mockMvc.perform(post("/profiles/1/activities/1/achievements").cookie(tokenCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(INPUT))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Achievement added successfully")));
    }

    @Test
    public void editAchievementTest() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "t0k3n");
        Session testSession = new Session("t0k3n");
        User testUser = new User("John", "Smith", "john@gmail.com", null,
                Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);
        testSession.setUser(testUser);
        Activity testActivity = createTestActivity();
        testActivity.setId((long) 1);
        Achievement testAchievement = new Achievement("test", "test", ResultType.TIME);
        testAchievement.setId((long) 1);

        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(testSession);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(testUser));
        when(activityRepository.findActivityById((long) 1)).thenReturn(testActivity);
        when(service.editAchievement(any(Achievement.class), any(Long.class), any(Long.class), any(Long.class), any(String.class))).thenReturn(new ResponseEntity("Achievement has been successfully updated", HttpStatus.OK));
        this.mockMvc.perform(put("/profiles/1/activities/1/achievements/1").cookie(tokenCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(INPUT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Achievement has been successfully updated")));
    }

    @Test
    public void deleteAchievementTest() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "t0k3n");
        Session testSession = new Session("t0k3n");
        User testUser = new User("John", "Smith", "john@gmail.com", null,
                Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);
        testSession.setUser(testUser);
        Activity testActivity = createTestActivity();
        testActivity.setId((long) 1);
        Achievement testAchievement = new Achievement("test", "test", ResultType.TIME);
        testAchievement.setId((long) 1);

        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(testSession);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(testUser));
        when(activityRepository.findActivityById((long) 1)).thenReturn(testActivity);
        when(service.deleteAchievement(any(Long.class), any(Long.class), any(Long.class), any(String.class))).thenReturn(new ResponseEntity("Achievement successfully deleted", HttpStatus.OK));
        this.mockMvc.perform(delete("/profiles/1/activities/1/achievements/1").cookie(tokenCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(INPUT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Achievement successfully deleted")));
    }

    @Test
    public void addResultSuccessTest() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "token");
        Session session = new Session("token");
        Achievement achievement = new Achievement("Test", "Test", ResultType.TIME);
        User user = new User("Maurice", "Benson", "jacky@google.com",
                "1985-12-20", Gender.MALE, 3,
                "jacky'sSecuredPwd");

        Result result = new Result("1.53");

        when(service.addResult(any(Result.class), any(Long.class), any(Long.class))).thenReturn(new ResponseEntity<>("Success", HttpStatus.OK));
        when(achievementRepository.findAchievementById((long) 1)).thenReturn(achievement);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(user));
        when(sessionRepository.findUserIdByToken("token")).thenReturn(session);

        this.mockMvc.perform(post("/profiles/1/achievements/1/results").cookie(tokenCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(INPUT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Success")));
    }

    @Test
    public void addResultBadSession() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "tokfn");
        Session session = new Session("token");
        Achievement achievement = new Achievement("Test", "Test", ResultType.TIME);
        User user = new User("Maurice", "Benson", "jacky@google.com",
                "1985-12-20", Gender.MALE, 3,
                "jacky'sSecuredPwd");

        Result result = new Result("1.53");

        when(service.addResult(any(Result.class), any(Long.class), any(Long.class))).thenReturn(new ResponseEntity<>("Success", HttpStatus.OK));
        when(achievementRepository.findAchievementById((long) 1)).thenReturn(achievement);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(user));
        when(sessionRepository.findUserIdByToken("token")).thenReturn(session);

        this.mockMvc.perform(post("/profiles/1/achievements/1/results").cookie(tokenCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(INPUT))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Session invalid")));
    }

    @Test
    public void addResultBadUser() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "token");
        Session session = new Session("token");
        Achievement achievement = new Achievement("Test", "Test", ResultType.TIME);
        User user = new User("Maurice", "Benson", "jacky@google.com",
                "1985-12-20", Gender.MALE, 3,
                "jacky'sSecuredPwd");

        Result result = new Result("1.53");

        when(service.addResult(any(Result.class), any(Long.class), any(Long.class))).thenReturn(new ResponseEntity<>("Success", HttpStatus.OK));
        when(achievementRepository.findAchievementById((long) 1)).thenReturn(achievement);
        when(userRepository.findById((long) 1)).thenReturn(Optional.empty());
        when(sessionRepository.findUserIdByToken("token")).thenReturn(session);

        this.mockMvc.perform(post("/profiles/1/achievements/1/results").cookie(tokenCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(INPUT))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("User not found")));
    }

    @Test
    public void addResultBadAchievement() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "token");
        Session session = new Session("token");
        Achievement achievement = new Achievement("Test", "Test", ResultType.TIME);
        User user = new User("Maurice", "Benson", "jacky@google.com",
                "1985-12-20", Gender.MALE, 3,
                "jacky'sSecuredPwd");

        Result result = new Result("1.53");

        when(service.addResult(any(Result.class), any(Long.class), any(Long.class))).thenReturn(new ResponseEntity<>("Success", HttpStatus.OK));
        when(achievementRepository.findAchievementById((long) 1)).thenReturn(null);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(user));
        when(sessionRepository.findUserIdByToken("token")).thenReturn(session);

        this.mockMvc.perform(post("/profiles/1/achievements/1/results").cookie(tokenCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(INPUT))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Achievement not found")));
    }

    @Test
    public void getSharedUsersTest() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "t0k3n");

        Activity activity = createTestActivity();
        List<User> users = new ArrayList<>();

        User user1 = new User("Jack", "Ryan", "jack@gmail.com", null, Gender.MALE, 2, "Password1");
        user1.setUserId((long) 1);
        users.add(user1);

        User user2 = new User("John", "Smith", "john@gmail.com", null, Gender.MALE, 2, "Password1");
        user2.setUserId((long) 2);
        users.add(user2);

        Pageable pageable = PageRequest.of(0, 2);
        Page<User> usersAsPage = new PageImpl<User>(users, pageable, users.size());

        String testResponse = "{\"content\":[{\"lastname\":\"Ryan\",\"firstname\":\"Jack\",\"middlename\":null," +
        "\"nickname\":null,\"email\":\"jack@gmail.com\",\"activityTypes\":[]},{\"lastname\":\"Smith\"," +
        "\"firstname\":\"John\",\"middlename\":null,\"nickname\":null,\"email\":\"john@gmail.com\"," +
        "\"activityTypes\":[]}]";

        ResponseHandler handler = new ResponseHandler();
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(user1));
        when(userRepository.findById((long) 2)).thenReturn(Optional.of(user2));
        when(activityRepository.getOne(anyLong())).thenReturn(activity);
        when(service.getSharedUsers(any(Long.class), any(int.class), any(int.class))).thenReturn(handler.userPageToSearchResponsePage(usersAsPage));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/activities/1/shared/?page= +" + 0 + "&size=" + 3).cookie(tokenCookie))
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

    @Test
    public void getStats1FollowerTest() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "t0k3n");
        Session session1 = new Session("t0k3n");

        User testUser2 = new User("John", "Smith", "john2@gmail.com", null, Gender.MALE, 2, "Password1");
        testUser2.setUserId((long) 2);

        Activity newActivity = createTestActivity();

        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(session1);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(testUser2));
        when(activityRepository.findActivityById((long) 1)).thenReturn(newActivity);
        when(service.getStats(any(Long.class))).
                thenReturn(new ResponseEntity("{\n" +
                        "  \"followers\": " + 1 + ",\n" +
                        "  \"participants\": " + 0 + ",\n" +
                        "  \"organisers\": " + 0 + "\n" +
                        "}", HttpStatus.OK));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/activities/1/stats").cookie(tokenCookie))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("{\n" +
                        "  \"followers\": " + 1 + ",\n" +
                        "  \"participants\": " + 0 + ",\n" +
                        "  \"organisers\": " + 0 + "\n" +
                        "}")));
    }

    @Test
    public void getStats1OrganiserTest() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "t0k3n");
        Session session1 = new Session("t0k3n");

        User testUser2 = new User("John", "Smith", "john2@gmail.com", null, Gender.MALE, 2, "Password1");
        testUser2.setUserId((long) 2);

        Activity newActivity = createTestActivity();

        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(session1);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(testUser2));
        when(activityRepository.findActivityById((long) 1)).thenReturn(newActivity);
        when(service.getStats(any(Long.class))).
                thenReturn(new ResponseEntity("{\n" +
                        "  \"followers\": " + 0 + ",\n" +
                        "  \"participants\": " + 0 + ",\n" +
                        "  \"organisers\": " + 1 + "\n" +
                        "}", HttpStatus.OK));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/activities/1/stats").cookie(tokenCookie))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("{\n" +
                        "  \"followers\": " + 0 + ",\n" +
                        "  \"participants\": " + 0 + ",\n" +
                        "  \"organisers\": " + 1 + "\n" +
                        "}")));
    }

    @Test
    public void getStats1ParticipantTest() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "t0k3n");
        Session session1 = new Session("t0k3n");

        User testUser2 = new User("John", "Smith", "john2@gmail.com", null, Gender.MALE, 2, "Password1");
        testUser2.setUserId((long) 2);

        Activity newActivity = createTestActivity();

        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(session1);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(testUser2));
        when(activityRepository.findActivityById((long) 1)).thenReturn(newActivity);
        when(service.getStats(any(Long.class))).
                thenReturn(new ResponseEntity("{\n" +
                        "  \"followers\": " + 0 + ",\n" +
                        "  \"participants\": " + 1 + ",\n" +
                        "  \"organisers\": " + 0 + "\n" +
                        "}", HttpStatus.OK));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/activities/1/stats").cookie(tokenCookie))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("{\n" +
                        "  \"followers\": " + 0 + ",\n" +
                        "  \"participants\": " + 1 + ",\n" +
                        "  \"organisers\": " + 0 + "\n" +
                        "}")));
    }

    @Test
    public void getStatsNoValuesTest() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "t0k3n");
        Session session1 = new Session("t0k3n");

        User testUser2 = new User("John", "Smith", "john2@gmail.com", null, Gender.MALE, 2, "Password1");
        testUser2.setUserId((long) 2);

        Activity newActivity = createTestActivity();

        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(session1);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(testUser2));
        when(activityRepository.findActivityById((long) 1)).thenReturn(newActivity);
        when(service.getStats(any(Long.class))).
                thenReturn(new ResponseEntity("{\n" +
                        "  \"followers\": " + 0 + ",\n" +
                        "  \"participants\": " + 0 + ",\n" +
                        "  \"organisers\": " + 0 + "\n" +
                        "}", HttpStatus.OK));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/activities/1/stats").cookie(tokenCookie))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("{\n" +
                        "  \"followers\": " + 0 + ",\n" +
                        "  \"participants\": " + 0 + ",\n" +
                        "  \"organisers\": " + 0 + "\n" +
                        "}")));
    }

    @Test
    public void getRoleOfUserForActivityTest() throws Exception {
        User dummyUser = new User();
        Activity dummyActivity = new Activity();
        when(userRepository.getUserById((long) 1)).thenReturn(Optional.of(dummyUser));
        when(activityRepository.findActivityById((long) 1)).thenReturn(dummyActivity);
        when(service.getRoleOfUserForActivity(dummyActivity, dummyUser)).thenReturn(ActivityRole.PARTICIPANT);
        this.mockMvc.perform(get("/activities/1/role/1"))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("{\"role\":\"participant\"}")));
    }

    @Test
    public void getRoleOfUserForActivityNoRelationshipTest() throws Exception {
        User dummyUser = new User();
        Activity dummyActivity = new Activity();
        when(userRepository.getUserById((long) 1)).thenReturn(Optional.of(dummyUser));
        when(activityRepository.findActivityById((long) 1)).thenReturn(dummyActivity);
        when(service.getRoleOfUserForActivity(dummyActivity, dummyUser)).thenReturn(null);
        this.mockMvc.perform(get("/activities/1/role/1"))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("{\"role\":\"none\"}")));
    }

    @Test
    public void getRoleOfUserForActivityInvalidActivity() throws Exception {
        when(activityRepository.findActivityById((long) 1)).thenReturn(null);
        this.mockMvc.perform(get("/activities/1/role/1"))
                .andExpect(status().is(404))
                .andExpect(content().string(containsString("Activity not found")));
    }

    @Test
    public void getRoleOfUserForActivityInvalidUser() throws Exception {
        Activity dummyActivity = new Activity();
        when(activityRepository.findActivityById((long) 1)).thenReturn(dummyActivity);
        when(userRepository.getUserById((long) 1)).thenReturn(Optional.empty());
        this.mockMvc.perform(get("/activities/1/role/1"))
                .andExpect(status().is(404))
                .andExpect(content().string(containsString("User not found")));
    }

    @Test
    public void getLocationForActivityTest() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "t0k3n");
        Session testSession = new Session("t0k3n");
        Activity testActivity = createTestActivity();
        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(testSession);
        when(activityRepository.findById((long) 1)).thenReturn(Optional.of(testActivity));
        /*when(service.getActivityLocation(any(Long.class)))
                .thenReturn(new ResponseEntity(any(Location.class), HttpStatus.valueOf(200)));*/
        this.mockMvc.perform(get("/activities/1/location").cookie(tokenCookie))
                .andExpect(status().is(200));
    }
}