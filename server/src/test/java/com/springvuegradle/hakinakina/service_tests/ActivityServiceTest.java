package com.springvuegradle.hakinakina.service_tests;

import com.springvuegradle.hakinakina.dto.ActivityVisibilityDto;
import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.repository.*;
import com.springvuegradle.hakinakina.service.ActivityService;
import com.springvuegradle.hakinakina.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.Cookie;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ActivityServiceTest {
    @InjectMocks
    private ActivityService service;

    @InjectMocks
    private UserService userService;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private ActivityChangeRepository activityChangeRepository;

    @Mock
    private UserActivityRoleRepository userActivityRoleRepository;

    @Mock
    private HomeFeedRepository homeFeedRepository;

    @Captor
    ArgumentCaptor<List<UserActivityRole>> userActivityRoleListCaptor;

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private ResultRepository resultRepository;

    @BeforeAll
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    public void deleteUser() throws Exception {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        activityChangeRepository.deleteAll();
    }

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
    public void getActivitySummariesTest() {
        Date startDate1 = new Date(2021, 10, 10);
        Date endDate1 = new Date(2021, 10, 12);
        Activity activity1 = new Activity("Climb Mount Everest", "Let's climb Mount Everest together",
                true, new Timestamp(startDate1.getTime()), new Timestamp(endDate1.getTime()),
                "Mount Everest");
        activity1.setId((long) 1);

        Date startDate2 = new Date(2021, 10, 11);
        Date endDate2 = new Date(2021, 10, 12);
        Activity activity2 = new Activity("Descend Mount Everest", "Let's descend Mount Everest together",
                true, new Timestamp(startDate2.getTime()), new Timestamp(endDate2.getTime()),
                "Mount Everest");
        List<Activity> activities = new ArrayList<>();
        activity2.setId((long) 2);

        activities.add(activity1);
        activities.add(activity2);

        List<Map<String, String>> summaries = service.getActivitySummaries(activities);
        assertEquals(2, summaries.size());

        assertEquals(3, summaries.get(0).size());
        assertEquals("Climb Mount Everest", summaries.get(0).get("name"));
        assertEquals("Let's climb Mount Everest together", summaries.get(0).get("description"));
        assertEquals("1", summaries.get(0).get("id"));

        assertEquals(3, summaries.get(1).size());
        assertEquals("Descend Mount Everest", summaries.get(1).get("name"));
        assertEquals("Let's descend Mount Everest together", summaries.get(1).get("description"));
        assertEquals("2", summaries.get(1).get("id"));
    }

    @Test
    public void unFollowActivityValidUserTest() {
        final Cookie tokenCookie = new Cookie("s_id", "t0k3n");
        Session testSession = new Session("t0k3n");

        // add test user
        User testUser = new User("John", "Smith", "john@gmail.com", null, Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);

        Activity newActivity = createTestActivity();

        testSession.setUser(testUser);

        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(testSession);
        when(activityRepository.findActivityById((long) 1)).thenReturn(newActivity);
        when(userRepository.getUserById((long) 1)).thenReturn(Optional.of(testUser));

        ResponseEntity<String> response = service.unFollow((long) 1, (long) 1, tokenCookie.getValue());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Unfollowed activity", response.getBody());
    }

    @Test
    public void unFollowActivityInvalidUserTest() {
        Session testSession = new Session("t0k3n");
        Session testSession2 = new Session("t0k3n2");

        // add test user
        User testUser = new User("John", "Smith", "john@gmail.com", null, Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);

        User testUser2 = new User("John", "Smith", "john@gmail.com", null, Gender.MALE, 2, "Password1");
        testUser2.setUserId((long) 2);

        Activity newActivity = createTestActivity();

        testSession.setUser(testUser);
        testSession2.setUser(testUser2);

        when(sessionRepository.findUserIdByToken("t0k3n2")).thenReturn(testSession2);
        when(activityRepository.findActivityById((long) 1)).thenReturn(newActivity);

        ResponseEntity<String> response = service.unFollow((long) 1, (long) 1, "t0k3n2");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Invalid user", response.getBody());
    }

    @Test
    public void unFollowActivityInvalidSessionTest() {
        Session testSession = new Session("t0k3n");

        User testUser = new User("John", "Smith", "john@gmail.com", null, Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);

        Activity newActivity = createTestActivity();

        testSession.setUser(testUser);

        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(testSession);
        when(activityRepository.findActivityById((long) 1)).thenReturn(newActivity);

        ResponseEntity<String> response = service.unFollow((long) 1, (long) 1, null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Session", response.getBody());
    }

    @Test
    public void unFollowActivityNotFoundTest() {
        Session testSession = new Session("t0k3n");

        User testUser = new User("John", "Smith", "john@gmail.com", null, Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);

        Activity newActivity = createTestActivity();

        testSession.setUser(testUser);

        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(testSession);
        when(activityRepository.findActivityById((long) 1)).thenReturn(null);

        ResponseEntity<String> response = service.unFollow((long) 1, (long) 1, "t0k3n");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Activity not found", response.getBody());
    }

    @Test
    public void checkIfFollowingTrueTest() {
        Session testSession = new Session("t0k3n");

        User testUser = new User("John", "Smith", "john@gmail.com", null, Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);
        User testUser2 = new User("John", "Smith", "john@gmail.com", null, Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);

        Activity newActivity = createTestActivity();
        newActivity.setAuthor(testUser2);
        activityRepository.save(newActivity);

        testSession.setUser(testUser);

        Set<User> users = new HashSet<>();
        users.add(testUser);
        newActivity.setUsers(users);

        Set<Activity> activities = new HashSet<>();
        activities.add(newActivity);
        testUser.setActivities(activities);

        UserActivityKey userActivityKey = new UserActivityKey(testUser.getUserId(), newActivity.getId());
        UserActivityRole userActivityRole = new UserActivityRole(userActivityKey, ActivityRole.FOLLOWER);
        activityRepository.save(newActivity);
        userRepository.save(testUser);
        userActivityRoleRepository.save(userActivityRole);

        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(testSession);
        when(activityRepository.findActivityById((long) 1)).thenReturn(newActivity);
        when(userRepository.getUserById((long) 1)).thenReturn(Optional.of(testUser));
        when(userActivityRoleRepository.getByActivityAndUser(newActivity, testUser)).thenReturn(Optional.of(userActivityRole));

        ResponseEntity<String> response = service.checkFollowing((long) 1, (long) 1, "t0k3n");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("true", response.getBody());
    }

    @Test
    public void checkIfFollowingFalseTest() {
        Session testSession = new Session("t0k3n");

        User testUser = new User("John", "Smith", "john@gmail.com", null, Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);
        User testUser2 = new User("John", "Smith", "john@gmail.com", null, Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);

        Activity newActivity = createTestActivity();
        newActivity.setAuthor(testUser2);
        activityRepository.save(newActivity);

        testSession.setUser(testUser);

        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(testSession);
        when(activityRepository.findActivityById((long) 1)).thenReturn(newActivity);
        when(userRepository.getUserById((long) 1)).thenReturn(Optional.of(testUser));

        ResponseEntity<String> response = service.checkFollowing((long) 1, (long) 1, "t0k3n");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("false", response.getBody());
    }

    @Test
    public void addActivityChangesTest() {
        java.util.Date date = new java.util.Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        User testUser = new User("Maurice", "Benson", "jacky@google.com",
                "1985-12-20", Gender.MALE, 3,
                "jacky'sSecuredPwd");
        Activity activity = new Activity("scuba diving", "dive to the bottom of the sea", false, null, null, "Ireland");
        ActivityChange activityChanges = new ActivityChange("Test changes", timestamp, testUser, activity);
        activityChanges.setId(1L);
        List<ActivityChange> activityChangesList = new ArrayList<>();
        activityChangesList.add(activityChanges);
        activityChangeRepository.save(activityChanges);
        when(activityChangeRepository.findAll()).thenReturn(activityChangesList);
        assertEquals(1, activityChangesList.size());
    }

    @Test
    public void removeActivityChangesTest() {
        java.util.Date date = new java.util.Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        User testUser = new User("Maurice", "Benson", "jacky@google.com",
                "1985-12-20", Gender.MALE, 3,
                "jacky'sSecuredPwd");
        Activity activity = new Activity("scuba diving", "dive to the bottom of the sea", false, null, null, "Ireland");
        ActivityChange activityChanges = new ActivityChange("Test changes", timestamp, testUser, activity);
        activityChanges.setId(1L);
        List<ActivityChange> activityChangesList = new ArrayList<>();
        activityChangesList.add(activityChanges);
        activityChangeRepository.save(activityChanges);
        when(activityChangeRepository.findAll()).thenReturn(activityChangesList);
        assertEquals(1, activityChangesList.size());
        activityChangesList.remove(activityChanges);
        when(activityChangeRepository.findAll()).thenReturn(activityChangesList);
        assertEquals(0, activityChangesList.size());
    }

    @Test
    public void addResultTest() {
        Achievement achievement = new Achievement("Test", "Test", ResultType.TIME);
        User user = new User("Maurice", "Benson", "jacky@google.com",
                "1985-12-20", Gender.MALE, 3,
                "jacky'sSecuredPwd");

        Result result = new Result("1.53");

        when(achievementRepository.findAchievementById((long) 1)).thenReturn(achievement);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(user));

        ResponseEntity<String> response = service.addResult(result, (long) 1, (long) 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void updateActivityVisibilityTest() {
        Activity activity = new Activity("scuba diving", "dive to the bottom of the sea",
                false, null, null, "Ireland");
        ActivityVisibilityDto dto = new ActivityVisibilityDto();
        dto.setVisibility(Visibility.PUBLIC);

        activity.setVisibility(Visibility.PRIVATE);
        when(activityRepository.findActivityById(1L)).thenReturn(activity);
        service.updateActivityVisibility(1L, 1L, dto);
        assertEquals(Visibility.PUBLIC, activity.getVisibility());
    }

    @Test
    public void updateActivityVisibilitySetSharedUsersTest() {
        Activity activity = new Activity("scuba diving", "dive to the bottom of the sea",
                false, null, null, "Ireland");
        ActivityVisibilityDto dto = new ActivityVisibilityDto();
        dto.setVisibility(Visibility.RESTRICTED);

        List<Map<String, String>> accessors = new ArrayList<>();
        Map<String, String> accessor1 = new HashMap<>();
        accessor1.put("email", "john@mail.com");
        accessor1.put("role", "participant");
        accessors.add(accessor1);
        User accessor1User = new User("John", "Smith", "john@gmail.com",
                null, Gender.MALE, 2, "Password1");

        Map<String, String> accessor2 = new HashMap<>();
        accessor2.put("email", "jane@mail.com");
        accessor2.put("role", "organiser");
        accessors.add(accessor2);
        User accessor2User = new User("Jane", "Smith", "jane@gmail.com",
                null, Gender.FEMALE, 2, "Password1");
        dto.setAccessors(accessors);

        ActivityService serviceSpy = Mockito.spy(service);

        when(activityRepository.findActivityById(1L)).thenReturn(activity);
        when(userRepository.getIdByAnyEmail("john@mail.com")).thenReturn(1L);
        when(userRepository.getOne(1L)).thenReturn(accessor1User);
        when(userRepository.getIdByAnyEmail("jane@mail.com")).thenReturn(2L);
        when(userRepository.getOne(2L)).thenReturn(accessor2User);
        doNothing().when(userActivityRoleRepository).deleteByActivity(activity);
        Mockito.doNothing().when(serviceSpy).saveRelationships(anyList());
        //Mockito.verify(serviceSpy).saveRelationships(userActivityRoleListCaptor.capture());
        //List<UserActivityRole> relationships = userActivityRoleListCaptor.getValue();

        //service.updateActivityVisibility(1L, 1L, dto);
        //System.out.println(relationships);
    }

    @Test
    public void updateActivityVisibilityInvalidRoleTest() {
        Activity activity = new Activity("scuba diving", "dive to the bottom of the sea",
                false, null, null, "Ireland");
        ActivityVisibilityDto dto = new ActivityVisibilityDto();
        dto.setVisibility(Visibility.RESTRICTED);

        List<Map<String, String>> accessors = new ArrayList<>();
        Map<String, String> accessor1 = new HashMap<>();
        accessor1.put("email", "john@mail.com");
        accessor1.put("role", "owner");
        accessors.add(accessor1);
        dto.setAccessors(accessors);

        when(activityRepository.findActivityById(1L)).thenReturn(activity);
        ResponseEntity response = service.updateActivityVisibility(1L, 1L, dto);
        assertEquals("{\"message\":\"No such role as: owner\"}", response.getBody());
    }

    @Test
    public void updateActivityVisibilityNonExistentUserIdTest() {
        Activity activity = new Activity("scuba diving", "dive to the bottom of the sea",
                false, null, null, "Ireland");
        ActivityVisibilityDto dto = new ActivityVisibilityDto();
        dto.setVisibility(Visibility.RESTRICTED);

        List<Map<String, String>> accessors = new ArrayList<>();
        Map<String, String> accessor1 = new HashMap<>();
        accessor1.put("email", "john@mail.com");
        accessor1.put("role", "participant");
        accessors.add(accessor1);
        dto.setAccessors(accessors);

        when(activityRepository.findActivityById(1L)).thenReturn(activity);
        when(userRepository.getIdByAnyEmail("john@mail.com")).thenReturn(null);
        ResponseEntity response = service.updateActivityVisibility(1L, 1L, dto);
        assertEquals("{\"message\":\"No user with email: john@mail.com\"}", response.getBody());
    }

    @Test
    public void updateActivityVisibilityAddOwnerTest() {
        Activity activity = new Activity("scuba diving", "dive to the bottom of the sea",
                false, null, null, "Ireland");
        User author = new User("John", "Smith", "john@gmail.com",
                null, Gender.MALE, 2, "Password1");
        author.setUserId(1L);
        activity.setAuthor(author);
        ActivityVisibilityDto dto = new ActivityVisibilityDto();
        dto.setVisibility(Visibility.RESTRICTED);

        List<Map<String, String>> accessors = new ArrayList<>();
        Map<String, String> accessor1 = new HashMap<>();
        accessor1.put("email", "john@mail.com");
        accessor1.put("role", "participant");
        accessors.add(accessor1);
        dto.setAccessors(accessors);

        when(activityRepository.findActivityById(1L)).thenReturn(activity);
        when(userRepository.getIdByAnyEmail("john@mail.com")).thenReturn(1L);
        ResponseEntity response = service.updateActivityVisibility(1L, 1L, dto);
        assertEquals("{\"message\":\"Cannot add the activity author as a shared User.\"}", response.getBody());
    }

    @Test
    public void getStats1FollowerTest() throws Exception {
        Session testSession = new Session("t0k3n");

        User testUser = new User("John", "Smith", "john@gmail.com", null, Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);

        Activity newActivity = createTestActivity();

        testSession.setUser(testUser);

        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(testSession);
        when(activityRepository.findActivityById((long) 1)).thenReturn(null);
        when(activityRepository.getNumFollowersForActivity((long) 1)).thenReturn(1);
        when(activityRepository.getNumParticipantsForActivity((long) 1)).thenReturn(0);
        when(activityRepository.getNumOrganisersForActivity((long) 1)).thenReturn(0);

        ResponseEntity<String> response = service.getStats((long) 1);
        assertEquals("{\n" +
                "  \"followers\": " + 1 + ",\n" +
                "  \"participants\": " + 0 + ",\n" +
                "  \"organisers\": " + 0 + "\n" +
                "}", response.getBody());
    }

    @Test
    public void getStats1OrganiserTest() throws Exception {
        Session testSession = new Session("t0k3n");

        User testUser = new User("John", "Smith", "john@gmail.com", null, Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);

        Activity newActivity = createTestActivity();

        testSession.setUser(testUser);

        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(testSession);
        when(activityRepository.findActivityById((long) 1)).thenReturn(null);
        when(activityRepository.getNumFollowersForActivity((long) 1)).thenReturn(0);
        when(activityRepository.getNumParticipantsForActivity((long) 1)).thenReturn(0);
        when(activityRepository.getNumOrganisersForActivity((long) 1)).thenReturn(1);

        ResponseEntity<String> response = service.getStats((long) 1);
        assertEquals("{\n" +
                "  \"followers\": " + 0 + ",\n" +
                "  \"participants\": " + 0 + ",\n" +
                "  \"organisers\": " + 1 + "\n" +
                "}", response.getBody());
    }

    @Test
    public void getStats1ParticipantTest() throws Exception {
        Session testSession = new Session("t0k3n");

        User testUser = new User("John", "Smith", "john@gmail.com", null, Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);

        Activity newActivity = createTestActivity();

        testSession.setUser(testUser);

        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(testSession);
        when(activityRepository.findActivityById((long) 1)).thenReturn(null);
        when(activityRepository.getNumFollowersForActivity((long) 1)).thenReturn(0);
        when(activityRepository.getNumParticipantsForActivity((long) 1)).thenReturn(1);
        when(activityRepository.getNumOrganisersForActivity((long) 1)).thenReturn(0);

        ResponseEntity<String> response = service.getStats((long) 1);
        assertEquals("{\n" +
                "  \"followers\": " + 0 + ",\n" +
                "  \"participants\": " + 1 + ",\n" +
                "  \"organisers\": " + 0 + "\n" +
                "}", response.getBody());
    }

    @Test
    public void getStatsNoValuesTest() throws Exception {
        Session testSession = new Session("t0k3n");

        User testUser = new User("John", "Smith", "john@gmail.com", null, Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);

        Activity newActivity = createTestActivity();

        testSession.setUser(testUser);

        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(testSession);
        when(activityRepository.findActivityById((long) 1)).thenReturn(null);
        when(activityRepository.getNumFollowersForActivity((long) 1)).thenReturn(1);
        when(activityRepository.getNumParticipantsForActivity((long) 1)).thenReturn(0);
        when(activityRepository.getNumOrganisersForActivity((long) 1)).thenReturn(0);

        ResponseEntity<String> response = service.getStats((long) 1);
        assertEquals("{\n" +
                "  \"followers\": " + 1 + ",\n" +
                "  \"participants\": " + 0 + ",\n" +
                "  \"organisers\": " + 0 + "\n" +
                "}", response.getBody());
    }

    @Test
    public void getRoleOfUserForActivityTest() {
        User dummyUser = new User();
        Activity dummyActivity = new Activity();
        UserActivityKey key = new UserActivityKey();
        key.setActivityId(1);
        key.setUserId(1);
        UserActivityRole role = new UserActivityRole(key, ActivityRole.PARTICIPANT);

        when(userActivityRoleRepository.getByActivityAndUser(dummyActivity, dummyUser)).thenReturn(Optional.of(role));
        assertEquals(ActivityRole.PARTICIPANT, service.getRoleOfUserForActivity(dummyActivity, dummyUser));
    }

    @Test
    public void getRoleOfUserForActivityNoRelationshipTest() {
        User dummyUser = new User();
        Activity dummyActivity = new Activity();

        when(userActivityRoleRepository.getByActivityAndUser(dummyActivity, dummyUser)).thenReturn(Optional.empty());
        assertEquals(null, service.getRoleOfUserForActivity(dummyActivity, dummyUser));
    }
}
