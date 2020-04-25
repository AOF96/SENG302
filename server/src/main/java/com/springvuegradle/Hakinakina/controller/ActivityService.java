package com.springvuegradle.Hakinakina.controller;

import com.springvuegradle.Hakinakina.entity.*;
import com.springvuegradle.Hakinakina.util.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ActivityService {

    public UserRepository userRepository;
    public ActivityRepository activityRepository;
    public PassportCountryRepository countryRepository;
    public SessionRepository sessionRepository;
    private ResponseHandler responseHandler = new ResponseHandler();

    public ActivityService(UserRepository userRepository, ActivityRepository activityRepository, PassportCountryRepository countryRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.countryRepository = countryRepository;
        this.sessionRepository = sessionRepository;
    }

    /**
     * Adds an activity for the user*
     */
    public ResponseEntity addActivity(Activity activity, long profileId, String sessionToken) {
        activityRepository.save(activity);
        return new ResponseEntity("Activity Successfully Added", HttpStatus.valueOf(200));
    }

}
