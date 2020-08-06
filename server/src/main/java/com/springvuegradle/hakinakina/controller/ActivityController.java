package com.springvuegradle.hakinakina.controller;

import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.repository.*;
import com.springvuegradle.hakinakina.service.ActivityService;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

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
     * Handles requests for setting the visibility of an activity and the shared users if it is being set to restricted.
     *
     * @param profileId The id of the user who owns the activity
     * @param activityId The id of the activity being modified
     * @param jsonString The request body
     * @param sessionToken The user's token from the cookie for their current session.
     * @return A ResponseEntity object passed from the service method.
     */
    @PutMapping("/profiles/{profileId}/activities/{activityid}/visibility")
    public ResponseEntity setVisibility(@PathVariable("profileId") long profileId,
                                        @PathVariable("activityid") long activityId,
                                        @RequestBody String jsonString,
                                        @CookieValue(value = "s_id") String sessionToken) {
        Map<String, Object> json = new JacksonJsonParser().parseMap(jsonString);
        String visibility = (String) json.get("visibility");
        List accessorsObjects = null;
        List accessorPairs = null;
        Set<String> visibilityValues = new HashSet<>(Arrays.asList("public", "private", "restricted"));

        if (!visibilityValues.contains(visibility)) {
            return new ResponseEntity("'" + visibility + "' is not a valid visibility.", HttpStatus.valueOf(400));
        }

        if (visibility.equals("restricted")) {
            accessorsObjects = (List) json.get("accessors");
            if (accessorsObjects == null) {
                return new ResponseEntity("Must include accessors list if setting to restricted.",
                        HttpStatus.valueOf(400));
            }
            accessorPairs = new ArrayList<String>();
        }
        if (accessorsObjects != null) {
            for (Object accessor : accessorsObjects) {
                Map<String, String> pair = (Map<String, String>) accessor;
                accessorPairs.add(pair);
            }
        }

        return activityService.setVisibility(profileId, activityId, sessionToken, visibility, accessorPairs);
    }
}
