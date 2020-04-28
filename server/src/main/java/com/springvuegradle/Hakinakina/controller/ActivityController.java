package com.springvuegradle.Hakinakina.controller;

import com.springvuegradle.Hakinakina.entity.*;
import com.springvuegradle.Hakinakina.util.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Rest controller class for controlling requests about Activities
 */
@RestController
public class ActivityController {

    public UserRepository userRepository;
    public ActivityTypeRepository activityTypeRepository;
    public PassportCountryRepository countryRepository;
    public SessionRepository sessionRepository;
    public ActivityRepository activityRepository;
    private ResponseHandler responseHandler = new ResponseHandler();

    private ActivityService activityService;


    /**
     * Contructs an Activity Controller, passing in the repositories so that they can be accessed.
     *
     * @param userRepository    The repository containing Users
     * @param countryRepository The repository containing PassportCountries
     * @param sessionRepository The repository containing Sessions
     */
    public ActivityController(UserRepository userRepository, PassportCountryRepository countryRepository,
                              SessionRepository sessionRepository, ActivityService activityService,
                              ActivityRepository activityRepository) {
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
        this.sessionRepository = sessionRepository;
        this.activityRepository = activityRepository;
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

    /***
     * Handles requests for deleting an activity.
     * @param profileId the user's id.
     * @param activityId the activity id.
     * @param sessionToken the user's token from their current session.
     * @return a response entity that informs the user if deleting the given activity was successful or not.
     */
    @DeleteMapping("/profiles/{profileId}/activities/{activityId}")
    public ResponseEntity deleteActivity(@PathVariable("profileId") long profileId, @PathVariable("activityId") long activityId,
                                         @RequestHeader("token") String sessionToken) {

        return activityService.removeActivity(profileId, activityId, sessionToken);
    }

    @GetMapping("/profiles/{profileId}/activities/continuous")
    public ResponseEntity getContinuousActivities(@PathVariable("profileId") long profileId) {
        List<Activity> activities = activityRepository.getActivitiesForUserOfType(true, profileId);
        List<Map<String, String>> result = activityService.getActivitySummaries(activities);
        return new ResponseEntity(result, HttpStatus.valueOf(200));
    }

    @GetMapping("/profiles/{profileId}/activities/duration")
    public ResponseEntity getDurationActivities(@PathVariable("profileId") long profileId) {
        List<Activity> activities = activityRepository.getActivitiesForUserOfType(false, profileId);
        List<Map<String, String>> result = activityService.getActivitySummaries(activities);
        return new ResponseEntity(result, HttpStatus.valueOf(200));
    }

}
