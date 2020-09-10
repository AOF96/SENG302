package com.springvuegradle.hakinakina.endpoints;

import com.springvuegradle.hakinakina.controller.SearchController;
import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.repository.*;
import com.springvuegradle.hakinakina.service.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
public class SearchControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ActivityRepository activityRepository;

    @MockBean
    private ActivityTypeRepository activityTypeRepository;

    @MockBean
    private EmailRepository emailRepository;

    @MockBean
    private SessionRepository sessionRepository;

    @MockBean
    private PassportCountryRepository passportCountryRepository;

    @MockBean
    private SearchRepository searchRepository;

    @MockBean
    private AchievementRepository achievementRepository;

    @MockBean
    private ActivityChangeRepository activityChangeRepository;

    @MockBean
    private UserActivityRoleRepository userActivityRoleRepository;

    @MockBean
    private ResultRepository resultRepository;

    @MockBean
    private LocationRepository locationRepository;

    @MockBean
    private SearchService searchService;

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
    public void getActivityTypesSetTest() {
        SearchController searchController = new SearchController(null, null,
                null, activityTypeRepository, null);

        ActivityType fun = new ActivityType("Fun");
        ActivityType adventurous = new ActivityType("Adventurous");
        Set<ActivityType> expectedSet = new HashSet<>();
        expectedSet.add(fun);
        expectedSet.add(adventurous);

        when(activityTypeRepository.findActivityTypeByName("Adventurous"))
                .thenReturn(adventurous);
        when(activityTypeRepository.findActivityTypeByName("Fun"))
                .thenReturn(fun);

        assertEquals(expectedSet, searchController.getActivityTypesSet("Adventurous Fun"));
    }

    @Test
    public void getActivityTypesSetOneItemTest() {
        SearchController searchController = new SearchController(null, null,
                null, activityTypeRepository, null);

        ActivityType fun = new ActivityType("Fun");
        Set<ActivityType> expectedSet = new HashSet<>();
        expectedSet.add(fun);

        when(activityTypeRepository.findActivityTypeByName("Fun"))
                .thenReturn(fun);

        assertEquals(expectedSet, searchController.getActivityTypesSet("Fun"));
    }

    @Test
    public void getActivityTypesSetRedundantSpacesTest() {
        SearchController searchController = new SearchController(null, null,
                null, activityTypeRepository, null);

        ActivityType fun = new ActivityType("Fun");
        ActivityType adventurous = new ActivityType("Adventurous");
        Set<ActivityType> expectedSet = new HashSet<>();
        expectedSet.add(fun);
        expectedSet.add(adventurous);

        when(activityTypeRepository.findActivityTypeByName("Adventurous"))
                .thenReturn(adventurous);
        when(activityTypeRepository.findActivityTypeByName("Fun"))
                .thenReturn(fun);

        assertEquals(expectedSet, searchController.getActivityTypesSet(" Adventurous  Fun "));
    }

    @Test
    public void getActivityTypesSetNullParameterTest() {
        SearchController searchController = new SearchController(null, null,
                null, activityTypeRepository, null);

        Set<ActivityType> expectedSet = new HashSet<>();
        assertEquals(expectedSet, searchController.getActivityTypesSet(null));
    }

    @Test
    public void findPaginatedInvalidMethodTest() throws Exception {
        when(searchService.findPaginatedByQuery(anyInt(), anyInt(), anyString(), anyString(), anyString(), anySet(), anyString()))
                .thenReturn(null);
        this.mockMvc.perform(get("/profiles/?method=random&activity=Adventurous&page=0&size=10"))
                .andExpect(status().is(400))
                .andExpect(content().string(containsString("Method must either be 'or' or 'and'")));
    }

    @Test
    public void findPaginatedORTest() throws Exception {
        when(searchService.findPaginatedByQuery(anyInt(), anyInt(), anyString(), anyString(), anyString(), anySet(), anyString()))
                .thenReturn(null);
        this.mockMvc.perform(get("/profiles/?method=or&activity=Adventurous&page=0&size=10"))
                .andExpect(status().is(200));
    }

    @Test
    public void findPaginatedANDTest() throws Exception {
        when(searchService.findPaginatedByQuery(anyInt(), anyInt(), anyString(), anyString(), anyString(), anySet(), anyString()))
                .thenReturn(null);
        this.mockMvc.perform(get("/profiles/?method=and&activity=Adventurous&page=0&size=10"))
                .andExpect(status().is(200));
    }

    @Test
    public void findActivityPaginatedTest() throws Exception {
        Session testSession = new Session("t0k3n");

        User testUser = new User("John", "Smith", "john@gmail.com", null, Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);

        testSession.setUser(testUser);

        Activity testActivity = createTestActivity();
        activityRepository.save(testActivity);

        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(testSession);
        when(activityRepository.findActivityById((long) 1)).thenReturn(null);
    }
}
