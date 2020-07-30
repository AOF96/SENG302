package com.springvuegradle.hakinakina.controller;

import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.repository.*;
import com.springvuegradle.hakinakina.service.ActivityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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
     * Endpoint for unfollowing an activity
     * @param profileId id of user that is unfollowing
     * @param activityId activity to unfollow
     * @param sessionToken session id of the user
     * @return response entity with the result of the operation.
     */
    @DeleteMapping("/profiles/{profileId}/subscriptions/activities/{activityId}")
    public ResponseEntity<String> unFollow(@PathVariable Long profileId, @PathVariable Long activityId,
                                   @CookieValue(value = "s_id") String sessionToken) {
        return activityService.unFollow(profileId, activityId, sessionToken);
    }

    /**
     * Returns if the given user is following the given activity
     * @param profileId user requested
     * @param activityId activity to check
     * @param sessionToken session token of the requesting user
     * @return formatted response with result
     */
    @GetMapping("/profiles/{profileId}/subscriptions/activities/{activityId}")
    public ResponseEntity<String> getIfFollowing(@PathVariable Long profileId, @PathVariable Long activityId,
                                                 @CookieValue(value = "s_id") String sessionToken) {
        return activityService.checkFollowing(profileId, activityId, sessionToken);
    }
}
