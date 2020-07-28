package com.springvuegradle.hakinakina.service_tests;

import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.repository.ActivityRepository;
import com.springvuegradle.hakinakina.repository.SessionRepository;
import com.springvuegradle.hakinakina.repository.UserRepository;
import com.springvuegradle.hakinakina.service.ActivityService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ActivityServiceTest {
    @InjectMocks
    private ActivityService service;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionRepository sessionRepository;

    @BeforeAll
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    public void deleteUser() throws Exception {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
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
}
