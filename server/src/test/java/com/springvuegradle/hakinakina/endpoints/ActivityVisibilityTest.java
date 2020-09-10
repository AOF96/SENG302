package com.springvuegradle.hakinakina.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.hakinakina.controller.ActivityController;
import com.springvuegradle.hakinakina.dto.ActivityVisibilityDto;
import com.springvuegradle.hakinakina.service.ActivityService;
import com.springvuegradle.hakinakina.service.UserService;
import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.repository.*;
import io.cucumber.java.an.E;
import net.minidev.json.JSONArray;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.Cookie;
import javax.transaction.Transactional;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ActivityVisibilityTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private ActivityService service;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PassportCountryRepository countryRepository;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ActivityTypeRepository activityTypeRepository;

    @Autowired
    private SearchRepository searchRepository;

    @Autowired
    private ActivityChangeRepository activityChangeRepository;

    @Autowired
    private UserActivityRoleRepository userActivityRoleRepository;

    @Autowired
    private UserService userService;

    private User testUser;
    private User testUser2;
    private User testUser3;
    private List<Map<String, String>> accessors;
    private ActivityVisibilityDto activityVisibilityDto;


    @BeforeEach
    public void deleteUser() throws Exception {
        userActivityRoleRepository.deleteAll();
        activityRepository.removeAllUserActivities();
        activityRepository.deleteAll();
        userRepository.deleteAll(); //this deletes admin from this test file
        sessionRepository.deleteAll();

        testUser = new User("John", "Smith", "john@gmail.com", null, Gender.MALE, 2, "Password1");
        userRepository.save(testUser);
        testUser2 = new User("Shivin", "g", "shivin@gmail.com", null, Gender.FEMALE, 2, "Password2");
        userRepository.save(testUser2);
        testUser3 = new User("Jake", "Smith", "jake@gmail.com", null, Gender.NON_BINARY, 2, "Password3");
        userRepository.save(testUser3);

        accessors = new ArrayList<Map<String, String>>();
        activityVisibilityDto = new ActivityVisibilityDto();
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
                new Timestamp(startTime.getTime()), new Timestamp(endTime.getTime()));

        testActivity.setId((long) 1);
        Set<ActivityType> activityTypes = new HashSet<>();
        activityTypes.add(new ActivityType("Fun"));
        testActivity.setActivityTypes(activityTypes);
        return testActivity;
    }

    @Test @Transactional
    public void updateActivityVisibilityTest() throws Exception {
        Session testSession = new Session("t0k3n");

        activityVisibilityDto.setVisibility(Visibility.PUBLIC);
        Activity newActivity = activityRepository.save(createTestActivity());
        activityRepository.insertActivityForUser(testUser.getUserId(), newActivity.getId());
        testSession.setUser(testUser);
        sessionRepository.save(testSession);

        this.mockMvc.perform(MockMvcRequestBuilders.put("/profiles/"+ testUser.getUserId()+"/activities/" +newActivity.getId()+"/visibility")
                .cookie(new Cookie("s_id", "t0k3n"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityVisibilityDto)))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("Activity Visibility Status Updated")));
    }

}