package com.springvuegradle.Hakinakina.endpoints;

import com.springvuegradle.Hakinakina.controller.ActivityController;
import com.springvuegradle.Hakinakina.controller.ActivityService;
import com.springvuegradle.Hakinakina.controller.UserService;
import com.springvuegradle.Hakinakina.entity.*;

import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
//@WebMvcTest(ActivityController.class)
public class ActivityControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ActivityService activityService;

    private User user;

    @BeforeEach
    public void deleteUser() throws Exception {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void addActivityTest() throws Exception {

        Session testSession = new Session("t0k3n");
        user = new User("Mayuko", "Williams",
                "mwi@williams.com", "1970-01-01", Gender.FEMALE,
                3, "P@ssw0rd!123");
        user.setUserId((long) 1);
        testSession.setUser(user);

//        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(testSession);
//        when(userRepository.findById((long) 1)).thenReturn(Optional.of(user));
//        when(activityService.addActivity(any(Activity.class)), user.getUserId(), testSession)
//                .thenReturn(new ResponseEntity("Activity has been created", HttpStatus.CREATED));

        String input = "{\n" +
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
        mockMvc.perform(post("/profiles/1/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(input))
                .andExpect(status().isCreated());


        //        user = new User("Mayuko", "Williams",
//                "mwi@williams.com", "1970-01-01", Gender.FEMALE,
//                3, "P@ssw0rd!123");
//        userRepository.save(user);
//        User savedUser = userRepository.findUserByEmail("mwi@williams.com");
//        savedUser.setUserId(1L);
//        sessionRepository.insertToken(token, savedUser.getUserId());
    }

}
