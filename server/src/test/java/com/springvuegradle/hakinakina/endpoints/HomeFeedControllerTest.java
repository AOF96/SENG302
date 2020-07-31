package com.springvuegradle.hakinakina.endpoints;

import com.springvuegradle.hakinakina.controller.ActivityController;
import com.springvuegradle.hakinakina.controller.HomeFeedController;
import com.springvuegradle.hakinakina.entity.Activity;
import com.springvuegradle.hakinakina.entity.Gender;
import com.springvuegradle.hakinakina.entity.Session;
import com.springvuegradle.hakinakina.entity.User;
import com.springvuegradle.hakinakina.repository.*;
import com.springvuegradle.hakinakina.service.ActivityService;
import com.springvuegradle.hakinakina.service.HomeFeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.Cookie;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HomeFeedController.class)
public class HomeFeedControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HomeFeedService homeFeedService;

    @MockBean
    private ActivityRepository activityRepository;

    @MockBean
    private ActivityChangeRepository activityChangeRepository;

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

    @BeforeEach
    public void deleteUser() throws Exception {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void getUserHomeFeedTest() throws Exception {
        final Cookie tokenCookie = new Cookie("s_id", "t0k3n");
        Session session = new Session("t0k3n");

        User testUser = new User("John", "Smith", "john2@gmail.com", null, Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);

        session.setUser(testUser);

        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(session);
        when(userRepository.getUserById((long) 1)).thenReturn(Optional.of(testUser));
        when(homeFeedService.getHomeFeed(any(Long.class))).
                thenReturn(new ResponseEntity<String>("", HttpStatus.OK));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/profiles/1/feed").cookie(tokenCookie))
                .andExpect(status().is(200));
    }
}
