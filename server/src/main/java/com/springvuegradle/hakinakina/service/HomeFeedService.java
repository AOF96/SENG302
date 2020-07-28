package com.springvuegradle.hakinakina.service;

import com.springvuegradle.hakinakina.repository.ActivityRepository;
import com.springvuegradle.hakinakina.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * Service class containing logic related to Home Feed
 */
@Service
public class HomeFeedService {

    public UserRepository userRepository;
    public ActivityRepository activityRepository;

    /**
     * Constructs a Home Feed Service, passing in the repositories so that they can be accessed.
     *
     * @param userRepository     The repository containing Users
     * @param activityRepository The repository containing Activities
     */
    public HomeFeedService(UserRepository userRepository,
                           ActivityRepository activityRepository) {
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
    }
}
