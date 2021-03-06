package com.springvuegradle.hakinakina.endpoints;

import com.springvuegradle.hakinakina.controller.SearchController;
import com.springvuegradle.hakinakina.dto.SearchActivityDto;
import com.springvuegradle.hakinakina.dto.SearchActivityLocationDto;
import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.repository.*;
import com.springvuegradle.hakinakina.service.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.Cookie;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;
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
    private UserActivityRoleRepository userActivityRoleRepository;

    @MockBean
    private ResultRepository resultRepository;

    @MockBean
    private LocationRepository locationRepository;

    @MockBean
    private HomeFeedRepository homeFeedRepository;

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
        Activity testActivity = new Activity("Outdoor Chess Tournament", "Come and play", false,
                new Timestamp(startTime.getTime()), new Timestamp(endTime.getTime()));
        testActivity.setId((long) 1);
        Set<ActivityType> activityTypes = new HashSet<>();
        activityTypes.add(new ActivityType("Fun"));
        testActivity.setActivityTypes(activityTypes);
        testActivity.setVisibility(Visibility.PUBLIC);
        return testActivity;
    }

    private Page<SearchActivityDto> createExpectedActivitySearchPage(Activity testActivity, Location testLocation) {
        //Set up to test that the service returns this expected result
        List<SearchActivityDto> searchActivityDtoList = new ArrayList<SearchActivityDto>();
        Pageable pageable = PageRequest.of(0, 10);

        SearchActivityLocationDto searchActivityLocationDto = new SearchActivityLocationDto();
        searchActivityLocationDto.setStreetAddress(testLocation.getStreetAddress());
        searchActivityLocationDto.setCity(testLocation.getCity());
        searchActivityLocationDto.setCountry(testLocation.getCountry());

        SearchActivityDto searchActivityDto = new SearchActivityDto();
        searchActivityDto.setId(testActivity.getId());
        searchActivityDto.setName(testActivity.getName());
        searchActivityDto.setVisibility(testActivity.getVisibility());
        searchActivityDto.setContinuous(testActivity.isContinuous());
        searchActivityDto.setStartTime(testActivity.getStartTime());
        searchActivityDto.setEndTime(testActivity.getEndTime());
        searchActivityDto.setSearchActivityLocationDto(searchActivityLocationDto);

        searchActivityDtoList.add(searchActivityDto);

        Page<SearchActivityDto> searchActivityDtos = new PageImpl<SearchActivityDto>(searchActivityDtoList,
                pageable, searchActivityDtoList.size());

        return searchActivityDtos;
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
        when(searchService.findPaginatedByQuery(anyInt(), anyInt(), anyString(), anyString(), anyString(), anySet(), anySet(), anyString(), anyString(), anyString()))
                .thenReturn(null);
        this.mockMvc.perform(get("/profiles/?searchTerms=\"\"&searchTypes=fullname&searchTermsMethod=single&method=random&activity=Adventurous&page=0&size=10"))
                .andExpect(status().is(400))
                .andExpect(content().string(containsString("Method must either be 'or' or 'and'")));
    }

    @Test
    public void findPaginatedORTest() throws Exception {
        when(searchService.findPaginatedByQuery(anyInt(), anyInt(), anyString(), anyString(), anyString(), anySet(), anySet(), anyString(), anyString(), anyString()))
                .thenReturn(null);
        this.mockMvc.perform(get("/profiles/?searchTerms=\"\"&searchTypes=fullname&searchTermsMethod=single&method=or&activity=Adventurous&page=0&size=10"))
                .andExpect(status().is(200));
    }

    @Test
    public void findPaginatedANDTest() throws Exception {
        when(searchService.findPaginatedByQuery(anyInt(), anyInt(), anyString(), anyString(), anyString(), anySet(), anySet(), anyString(), anyString(), anyString()))
                .thenReturn(null);
        this.mockMvc.perform(get("/profiles/?searchTerms=\"\"&searchTypes=fullname&searchTermsMethod=single&method=or&activity=Adventurous&page=0&size=10"))
                .andExpect(status().is(200));
    }

    @Test
    public void findActivityPaginatedTest() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "token");
        Session testSession = new Session("token");

        User testUser = new User("Maurice", "Benson", "jacky@google.com",
                "1985-12-20", Gender.MALE, 3,
                "jacky'sSecuredPwd");
        testUser.setUserId((long) 1);
        testSession.setUser(testUser);

        Location testLocation = new Location("University of Canterbury", "Ilam",
                "Christchurch", 1234, "Canterbury", "New Zealand",
                123.456, 123.456);
        testLocation.setId((long) 1);

        Activity testActivity = createTestActivity();
        testActivity.setLocation(testLocation);

        when(sessionRepository.findUserIdByToken("token")).thenReturn(testSession);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(testUser));
        when(activityRepository.findActivityById((long) 1)).thenReturn(testActivity);
        when(userRepository.findUserBySessions(any(Session.class))).thenReturn(testUser);
        when(searchService.findActivityPaginated(eq("Chess"), any(int.class), any(int.class), any(User.class)))
                .thenReturn(createExpectedActivitySearchPage(testActivity, testLocation));

        this.mockMvc.perform(get("/activities?activitySearchTerm=Chess&activitySearchTerms=null&method=single&page=0&size=10").cookie(tokenCookie))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("\"name\":\""+ testActivity.getName() +"\"")))
                .andExpect(content().string(containsString("\"visibility\":\""+ testActivity.getVisibility().toString().toLowerCase() +"\"")))
                .andExpect(content().string(containsString("\"continuous\":"+ testActivity.isContinuous() +"")))
                .andExpect(content().string(containsString("\"start_time\":\"1970-01-12T13:46:40.000+0000\"")))
                .andExpect(content().string(containsString("\"end_time\":\"1970-01-12T13:46:41.000+0000\"")))
                .andExpect(content().string(containsString("\"location\"")))
                .andExpect(content().string(containsString("\"city\":\""+ testLocation.getCity() +"\"")))
                .andExpect(content().string(containsString("\"country\":\""+ testLocation.getCountry() +"\"")))
                .andExpect(content().string(containsString("\"street_address\":\""+ testLocation.getStreetAddress() +"\"")));

        assertEquals(1, createExpectedActivitySearchPage(testActivity, testLocation).getNumberOfElements());
    }

    @Test
    public void findActivityPaginatedExactMatchSuccessTest() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "token");
        Session testSession = new Session("token");

        User testUser = new User("Maurice", "Benson", "jacky@google.com",
                "1985-12-20", Gender.MALE, 3,
                "jacky'sSecuredPwd");
        testUser.setUserId((long) 1);
        testSession.setUser(testUser);

        Location testLocation = new Location("University of Canterbury", "Ilam",
                "Christchurch", 1234, "Canterbury", "New Zealand",
                123.456, 123.456);
        testLocation.setId((long) 1);

        Activity testActivity = createTestActivity();
        testActivity.setLocation(testLocation);

        when(sessionRepository.findUserIdByToken("token")).thenReturn(testSession);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(testUser));
        when(userRepository.findUserBySessions(any(Session.class))).thenReturn(testUser);
        when(activityRepository.findActivityById((long) 1)).thenReturn(testActivity);
        when(searchService.findActivityPaginated(eq("'Outdoor Chess Tournament'"), any(int.class), any(int.class), eq(testUser)))
                .thenReturn(createExpectedActivitySearchPage(testActivity, testLocation));

        this.mockMvc.perform(get("/activities?activitySearchTerm='Outdoor Chess Tournament'&activitySearchTerms=null&method=single&page=0&size=10").cookie(tokenCookie))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("\"name\":\""+ testActivity.getName() +"\"")))
                .andExpect(content().string(containsString("\"visibility\":\""+ testActivity.getVisibility().toString().toLowerCase() +"\"")))
                .andExpect(content().string(containsString("\"continuous\":"+ testActivity.isContinuous() +"")))
                .andExpect(content().string(containsString("\"start_time\":\"1970-01-12T13:46:40.000+0000\"")))
                .andExpect(content().string(containsString("\"end_time\":\"1970-01-12T13:46:41.000+0000\"")))
                .andExpect(content().string(containsString("\"location\"")))
                .andExpect(content().string(containsString("\"city\":\""+ testLocation.getCity() +"\"")))
                .andExpect(content().string(containsString("\"country\":\""+ testLocation.getCountry() +"\"")))
                .andExpect(content().string(containsString("\"street_address\":\""+ testLocation.getStreetAddress() +"\"")));

        assertEquals(1, createExpectedActivitySearchPage(testActivity, testLocation).getNumberOfElements());
    }

    @Test
    public void findActivityPaginatedExactMatchFailureTest() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "token");
        Session testSession = new Session("token");

        User testUser = new User("Maurice", "Benson", "jacky@google.com",
                "1985-12-20", Gender.MALE, 3,
                "jacky'sSecuredPwd");
        testUser.setUserId((long) 1);
        testSession.setUser(testUser);

        List<SearchActivityDto> searchActivityDtoList = new ArrayList<SearchActivityDto>();
        Pageable pageable = PageRequest.of(0, 10);
        Page<SearchActivityDto> searchActivityDtos = new PageImpl<SearchActivityDto>(searchActivityDtoList,
                pageable, searchActivityDtoList.size());

        when(sessionRepository.findUserIdByToken("token")).thenReturn(testSession);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(testUser));
        when(searchService.findActivityPaginated(eq("'Indoor Chess Tournament'"), any(int.class), any(int.class), eq(testUser)))
                .thenReturn(searchActivityDtos);

        this.mockMvc.perform(get("/activities?activitySearchTerm=Chess&activitySearchTerms=null&method=single&page=0&size=10").cookie(tokenCookie))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("")));

        assertEquals(0, searchActivityDtos.getNumberOfElements());
    }

    @Test
    public void findActivityPaginatedNoMatchTest() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "token");
        Session testSession = new Session("token");

        User testUser = new User("Maurice", "Benson", "jacky@google.com",
                "1985-12-20", Gender.MALE, 3,
                "jacky'sSecuredPwd");
        testUser.setUserId((long) 1);
        testSession.setUser(testUser);

        List<SearchActivityDto> searchActivityDtoList = new ArrayList<SearchActivityDto>();
        Pageable pageable = PageRequest.of(0, 10);
        Page<SearchActivityDto> searchActivityDtos = new PageImpl<SearchActivityDto>(searchActivityDtoList,
                pageable, searchActivityDtoList.size());

        when(sessionRepository.findUserIdByToken("token")).thenReturn(testSession);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(testUser));
        when(searchService.findActivityPaginated(eq("Kayak"), any(int.class), any(int.class), eq(testUser)))
                .thenReturn(searchActivityDtos);

        this.mockMvc.perform(get("/activities?activitySearchTerm=Chess&activitySearchTerms=null&method=single&page=0&size=10").cookie(tokenCookie))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("")));

        assertEquals(0, searchActivityDtos.getNumberOfElements());
    }
}
