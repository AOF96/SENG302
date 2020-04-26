package com.springvuegradle.Hakinakina.controller;

import com.springvuegradle.Hakinakina.entity.*;
import com.springvuegradle.Hakinakina.util.ResponseHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    public ActivityController(UserRepository userRepository, PassportCountryRepository countryRepository, SessionRepository sessionRepository, ActivityService activityService) {
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
        this.sessionRepository = sessionRepository;
        this.activityService = activityService;
    }

    /**
     * Handles requests for adding an activity
     *
     * @param activity the activity the user wants to add
     * @param profileId the user's id
     * @param sessionToken the user's token from their current session
     * @return response entity to inform user if adding an activity was successful or not
     */
    @PostMapping("/profiles/{profileId}/activities")
    public ResponseEntity addActivity(@Valid @RequestBody Activity activity, @PathVariable("profileId") long profileId, @RequestHeader("token") String sessionToken) {
        return activityService.addActivity(activity, profileId, sessionToken);
    }

    /**
     * Handles requests for editing an activity
     */
    @PutMapping("/profiles/{profileId}/activities/{activityId}")
    public ResponseEntity editActivity() { return null; }

    /**
     * Handles requests for deleting an activity
     */
    @DeleteMapping("/profiles/{profileId}/activities/{activityId}")
    public ResponseEntity deleteActivity() {
        return null;
    }
}
