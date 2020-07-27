package com.springvuegradle.hakinakina.controller;

import com.springvuegradle.hakinakina.repository.ActivityTypeRepository;
import com.springvuegradle.hakinakina.repository.EmailRepository;
import com.springvuegradle.hakinakina.repository.SessionRepository;
import com.springvuegradle.hakinakina.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Rest controller class for controlling requests related to searching
 */
@RestController
public class SearchController {
    public UserRepository userRepository;
    public EmailRepository emailRepository;
    public SessionRepository sessionRepository;
    public ActivityTypeRepository activityTypeRepository;

    /**
     * Constructs a Search Controller, passing in the repositories and service so that they can be accessed.
     *
     * @param userRepository    The repository containing Users
     * @param emailRepository   The repository containing Emails
     * @param sessionRepository The repository containing Sessions
     * @param activityTypeRepository The repository containing Activities
     */
    public SearchController(UserRepository userRepository,
                            EmailRepository emailRepository,
                            SessionRepository sessionRepository,
                            ActivityTypeRepository activityTypeRepository) {
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
        this.sessionRepository = sessionRepository;
        this.activityTypeRepository = activityTypeRepository;
    }
}
