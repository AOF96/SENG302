package com.springvuegradle.hakinakina.service;

import com.springvuegradle.hakinakina.repository.ActivityTypeRepository;
import com.springvuegradle.hakinakina.repository.EmailRepository;
import com.springvuegradle.hakinakina.repository.SessionRepository;
import com.springvuegradle.hakinakina.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * Service class for search request actions
 */
@Service
public class SearchService {
    public UserRepository userRepository;
    public EmailRepository emailRepository;
    public SessionRepository sessionRepository;
    public ActivityTypeRepository activityTypeRepository;

    /**
     * Constructs a Search Service, passing in the repositories and service so that they can be accessed.
     *
     * @param userRepository         The repository containing Users
     * @param emailRepository        The repository containing Emails
     * @param sessionRepository      The repository containing Sessions
     * @param activityTypeRepository The repository containing Activities
     */
    public SearchService(UserRepository userRepository,
                         EmailRepository emailRepository,
                         SessionRepository sessionRepository,
                         ActivityTypeRepository activityTypeRepository) {
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
        this.sessionRepository = sessionRepository;
        this.activityTypeRepository = activityTypeRepository;
    }
}
