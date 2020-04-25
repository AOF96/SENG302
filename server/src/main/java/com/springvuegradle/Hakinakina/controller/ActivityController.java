package com.springvuegradle.Hakinakina.controller;

import com.springvuegradle.Hakinakina.entity.*;
import com.springvuegradle.Hakinakina.util.ResponseHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Rest controller class for controlling requests about Activities
 */
@RestController
public class ActivityController {

    public UserRepository userRepository;
    public ActivityTypeRepository activityTypeRepository;
    public PassportCountryRepository countryRepository;
    public SessionRepository sessionRepository;
    private ResponseHandler responseHandler = new ResponseHandler();

    private ActivityService activityService;


    /**
     * Contructs an Activity Controller, passing in the repositories so that they can be accessed.
     *
     * @param userRepository    The repository containing Users
     * @param countryRepository The repository containing PassportCountries
     * @param sessionRepository The repository containing Sessions
     */
    public ActivityController(UserRepository userRepository, PassportCountryRepository countryRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
        this.sessionRepository = sessionRepository;
    }

    @PostMapping("/profiles/{profileId}/activities")
    public ResponseEntity addActivity(@RequestBody Activity activity, @PathVariable("profileId") long profileId, @RequestHeader("token") String sessionToken) {
        return null;
    }

    @PutMapping("/profiles/{profileId}/activities/{activityId}")
    public ResponseEntity editActivity() { return null; }

    @DeleteMapping("/profiles/{profileId}/activities/{activityId}")
    public ResponseEntity deleteActivity() {
        return null;
    }
}