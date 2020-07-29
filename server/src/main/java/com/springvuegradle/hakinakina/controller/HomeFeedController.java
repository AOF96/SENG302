package com.springvuegradle.hakinakina.controller;

import com.springvuegradle.hakinakina.repository.ActivityRepository;
import com.springvuegradle.hakinakina.repository.UserRepository;
import com.springvuegradle.hakinakina.service.HomeFeedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * Rest controller class for controlling requests about a user's Home Feed
 */
@RestController
public class HomeFeedController {

    public UserRepository userRepository;
    public ActivityRepository activityRepository;
    public HomeFeedService homeFeedService;

    /**
     * Constructs an Activity Controller, passing in the repositories and service so that they can be accessed.
     *
     * @param userRepository     The repository containing Users
     * @param activityRepository The repository containing Activities
     * @param homeFeedService    The service for the Home Feed
     */
    public HomeFeedController(UserRepository userRepository,
                              ActivityRepository activityRepository,
                              HomeFeedService homeFeedService) {
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.homeFeedService = homeFeedService;
    }

}
