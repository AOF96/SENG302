package com.springvuegradle.hakinakina.controller;

import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.repository.*;
import com.springvuegradle.hakinakina.service.ActivityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Rest controller class for controlling requests about Activities
 */
@RestController
public class ActivityController {

    public UserRepository userRepository;
    public PassportCountryRepository countryRepository;
    public SessionRepository sessionRepository;
    public ActivityRepository activityRepository;
    private ActivityService activityService;

    /**
     * Contructs an Activity Controller, passing in the repositories and service so that they can be accessed.
     *
     * @param userRepository     The repository containing Users
     * @param countryRepository  The repository containing PassportCountries
     * @param sessionRepository  The repository containing Sessions
     * @param activityRepository The repository containing Activities
     * @param activityService    The service for Activities
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
     * @param activity     the activity the user wants to add
     * @param profileId    the user's id
     * @param sessionToken the user's token from the cookie for their current session.
     * @return response entity to inform user if adding an activity was successful or not
     */
    @PostMapping("/profiles/{profileId}/activities")
    public ResponseEntity addActivity(@Valid @RequestBody Activity activity,
                                      @PathVariable("profileId") long profileId,
                                      @CookieValue(value = "s_id") String sessionToken) {
        return activityService.addActivity(activity, profileId, sessionToken);
    }

    /**
     * Handles requests for editing an activity
     *
     * @param activity     the activity the user wants to add
     * @param profileId    the user's id
     * @param activityId   the activity id.
     * @param sessionToken the user's token from the cookie for their current session.
     * @return response entity to inform user if editing an activity was successful or not
     */
    @PutMapping("/profiles/{profileId}/activities/{activityId}")
    public ResponseEntity editActivity(@Valid @RequestBody Activity activity,
                                       @PathVariable("profileId") long profileId,
                                       @PathVariable("activityId") long activityId,
                                       @CookieValue(value = "s_id") String sessionToken) {
        return activityService.editActivity(activity, profileId, activityId, sessionToken);
    }

    /**
     * Handles requests for deleting an activity.
     *
     * @param profileId    the user's id.
     * @param activityId   the activity id.
     * @param sessionToken the user's token from the cookie for their current session.
     * @return a response entity that informs the user if deleting the given activity was successful or not.
     */
    @DeleteMapping("/profiles/{profileId}/activities/{activityId}")
    public ResponseEntity deleteActivity(@PathVariable("profileId") long profileId,
                                         @PathVariable("activityId") long activityId,
                                         @CookieValue(value = "s_id") String sessionToken) {
        return activityService.removeActivity(profileId, activityId, sessionToken);
    }

    /**
     * Handles requests for retrieving the details of an activity
     *
     * @param activityId the activity id.
     * @return a response entity that informs the user if retrieving an activity was successful or not.
     */
    @GetMapping("/activities/{activityId}")
    public ResponseEntity getOneActivity(@PathVariable("activityId") long activityId) {
        Optional<Activity> optional = activityRepository.findById(activityId);
        if (optional.isPresent()) {
            Activity activity = optional.get();
            return new ResponseEntity(activity.toJson(), HttpStatus.valueOf(200));
        } else {
            return new ResponseEntity("Activity does not exist", HttpStatus.valueOf(404));
        }
    }

    /**
     * Handles requests for retrieving the continuous activities of a user
     *
     * @param profileId the user's id
     * @return a response entity that informs the user if retrieving a user's continuous activities was successful or not
     */
    @GetMapping("/profiles/{profileId}/activities/continuous")
    public ResponseEntity getContinuousActivities(@PathVariable("profileId") long profileId) {
        List<Activity> activities = activityRepository.getActivitiesForAuthorOfType(true, profileId);
        List<Map<String, String>> result = activityService.getActivitySummaries(activities);
        return new ResponseEntity(result, HttpStatus.valueOf(200));
    }

    /**
     * Handles requests for retrieving the duration activities of a user
     *
     * @param profileId the user's id
     * @return a response entity that informs the user if retrieving a user's duration activities was successful or not
     */
    @GetMapping("/profiles/{profileId}/activities/duration")
    public ResponseEntity getDurationActivities(@PathVariable("profileId") long profileId) {
        List<Activity> activities = activityRepository.getActivitiesForAuthorOfType(false, profileId);
        List<Map<String, String>> result = activityService.getActivitySummaries(activities);
        return new ResponseEntity(result, HttpStatus.valueOf(200));
    }

    /**
     * Handles requests for adding achievements to an activity
     * @param achievement valid json containing achievement data
     * @param profileId id of the user that is adding the achievement
     * @param activityId id of the activity that the achievement is being added too
     * @param sessionToken users session token used for verification
     * @return response entity with status code dependent on the success or failure of the addition
     */
    @PostMapping("/profiles/{profileId}/activities/{activityId}/achievements")
    public ResponseEntity addAchievement(@Valid @RequestBody Achievement achievement,
                                         @PathVariable("profileId") long profileId,
                                         @PathVariable("activityId") long activityId,
                                         @CookieValue(value = "s_id") String sessionToken) {
        return activityService.addAchievement(achievement, profileId, activityId, sessionToken);
    }

    /**
     * Handles requests to edit achievements
     * @param achievement valid json containing new values for an existing achievement
     * @param profileId id of the user performing the edit
     * @param activityId id of the activity that the achievement being edited belongs too
     * @param achievementId id of the achievement that is being edited
     * @param sessionToken session token of the user which is used for validation checks
     * @return response entity with code dependant on success or failure of the request
     */
    @PutMapping("/profiles/{profileId}/activities/{activityId}/achievements/{achievementId}")
    public ResponseEntity editAchievement(@Valid @RequestBody Achievement achievement,
                                          @PathVariable("profileId") long profileId,
                                          @PathVariable("activityId") long activityId,
                                          @PathVariable("achievementId") long achievementId,
                                          @CookieValue(value = "s_id") String sessionToken) {
        return activityService.editAchievement(achievement, profileId, activityId, achievementId, sessionToken);
    }

    /**
     * Handles requests to delete an achievement
     * @param profileId id of user attempting to delete achievement
     * @param activityId id of activity that has the achievement associated with it
     * @param achievementId id of the achievement that is to be deleted
     * @param sessionToken session token of the user which is used for validation checks
     * @return response entity with code dependant on success or failure of the request
     */
    @DeleteMapping("/profiles/{profileId}/activities/{activityId}/achievements/{achievementId}")
    public ResponseEntity deleteAchievement(@PathVariable("profileId") long profileId,
                                            @PathVariable("activityId") long activityId,
                                            @PathVariable("achievementId") long achievementId,
                                            @CookieValue(value = "s_id") String sessionToken) {
        return activityService.deleteAchievement(profileId, activityId, achievementId, sessionToken);
    }

//     This code will be used when we have users subscribing to activities
//    /**
//     * Retrieves all of the continuous activities that a user is subscribed to
//     * @param profileId The ID of the user
//     * @return A response entity with the result and a status code
//     */
//    @GetMapping("/profiles/{profileId}/activities/continuous")
//    public ResponseEntity getContinuousActivities(@PathVariable("profileId") long profileId) {
//        List<Activity> activities = activityRepository.getActivitiesForUserOfType(true, profileId);
//        List<Map<String, String>> result = activityService.getActivitySummaries(activities);
//        return new ResponseEntity(result, HttpStatus.valueOf(200));
//    }
//
//    /**
//     * Retrieves all of the duration activities that a user is subscribed to
//     * @param profileId The ID of the user
//     * @return A response entity with the result and a status code
//     */
//    @GetMapping("/profiles/{profileId}/activities/duration")
//    public ResponseEntity getDurationActivities(@PathVariable("profileId") long profileId) {
//        List<Activity> activities = activityRepository.getActivitiesForUserOfType(false, profileId);
//        List<Map<String, String>> result = activityService.getActivitySummaries(activities);
//        return new ResponseEntity(result, HttpStatus.valueOf(200));
//    }

}
