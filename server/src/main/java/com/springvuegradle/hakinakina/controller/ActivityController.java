package com.springvuegradle.hakinakina.controller;

import com.springvuegradle.hakinakina.dto.ActivityVisibilityDto;
import com.springvuegradle.hakinakina.dto.SearchUserDto;
import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.repository.*;
import com.springvuegradle.hakinakina.service.ActivityService;
import org.springframework.boot.json.JacksonJsonParser;
import com.springvuegradle.hakinakina.util.ErrorHandler;
import com.springvuegradle.hakinakina.util.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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

    private ResponseHandler responseHandler;

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
    public ResponseEntity<String> addActivity(@Valid @RequestBody Activity activity,
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
     * Handles requests for retrieving the continuous activities of a user and checks if the user have access to it.
     *
     * @param profileId the user's id
     * @return a response entity that informs the user if retrieving a user's continuous activities was successful or not
     */
    @GetMapping("/profiles/{profileId}/activities/continuous")
    public ResponseEntity getContinuousActivities(@PathVariable("profileId") long profileId,
                                                  @CookieValue(value = "s_id") String sessionToken) {
        List<Activity> activities = activityRepository.getActivitiesForAuthorOfType(true, profileId);
        List<Activity> newActivities = new ArrayList<Activity>();
        Session loggedInUserSession = sessionRepository.findUserIdByToken(sessionToken);
        for(Activity a: activities){
            if(a.getVisibility() == Visibility.PRIVATE) {
                if(loggedInUserSession.getUser().getUserId() == profileId){
                    newActivities.add(a);
                }
            }
            if(a.getVisibility() == Visibility.RESTRICTED){
                //if the current logged in User is the owner of the activity
                if(loggedInUserSession.getUser().getUserId() == profileId){
                    newActivities.add(a);
                }
                else {
                    //if the current logged in user is added to list of people who can access the activities
                    List<Activity> sharedActivity  = activityRepository.getSharedActivitiesForAuthorOfType(a.getId(), loggedInUserSession.getUser().getUserId());
                    if(sharedActivity.size() > 0 ){
                        newActivities.add(a);
                    }
                }
            }
            if(a.getVisibility() == Visibility.PUBLIC){
                newActivities.add(a);
            }
        }
        List<Map<String, String>> result = activityService.getActivitySummaries(newActivities);
        return new ResponseEntity(result, HttpStatus.valueOf(200));
    }

    /**
     * Handles requests for retrieving the duration activities of a user and checks if the user have access to it.
     *
     * @param profileId the user's id
     * @return a response entity that informs the user if retrieving a user's duration activities was successful or not
     */
    @GetMapping("/profiles/{profileId}/activities/duration")
    public ResponseEntity getDurationActivities(@PathVariable("profileId") long profileId,
                                                @CookieValue(value = "s_id") String sessionToken) {
        List<Activity> activities = activityRepository.getActivitiesForAuthorOfType(false, profileId);
        List<Activity> newActivities = new ArrayList<Activity>();
        Session loggedInUserSession = sessionRepository.findUserIdByToken(sessionToken);
        for(Activity a: activities){
            if(a.getVisibility() == Visibility.PRIVATE) {
                if(loggedInUserSession.getUser().getUserId() == profileId){
                    newActivities.add(a);
                }
            }
            if(a.getVisibility() == Visibility.RESTRICTED){
                //if the current logged in User is the owner of the activity
                if(loggedInUserSession.getUser().getUserId() == profileId){
                    newActivities.add(a);
                }
                else {
                    //if the current logged in user is added to list of people who can access the activities
                    List<Activity> sharedActivity  = activityRepository.getSharedActivitiesForAuthorOfType(a.getId(), loggedInUserSession.getUser().getUserId());
                    if(sharedActivity.size() > 0 ){
                        newActivities.add(a);
                    }
                }
            }
            if(a.getVisibility() == Visibility.PUBLIC){
                newActivities.add(a);
            }
        }

        List<Map<String, String>> result = activityService.getActivitySummaries(newActivities);
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
     * Handles requests for retrieving all shared users of a given activity
     *
     * @param activityId the activity id.
     * @return a response entity that informs the user if retrieving an activity was successful or not.
     */
    @GetMapping("/activities/{activityId}/shared")
    public Page<SearchUserDto> getSharedUsers(@PathVariable("activityId") long activityId,
                                         @RequestParam("page") int page, @RequestParam("size") int size, @CookieValue(value = "s_id") String sessionToken) {
        return activityService.getSharedUsers(activityId, page, size);

    }

    /**
     * Handles the request for activity's visibility and its accessors.
     *
     * @param profileId  the logged in user's id.
     * @param activityId the activity id of the activity being managed
     * @param sessionToken the user's token from the cookie for their current session.
     * @param request level of visibility and the set of user's email allowed to access the activity
     * @return a response entity that informs the user that the visibility update was successful
     */
    @PutMapping("/profiles/{profileId}/activities/{activityId}/visibility")
    public ResponseEntity<String> updateActivityVisibility( @CookieValue(value = "s_id") String sessionToken,
                                                            @PathVariable("profileId") Long profileId,
                                                            @PathVariable("activityId") Long activityId,
                                                            @RequestBody ActivityVisibilityDto request){
        try {
            Activity activity = activityRepository.findActivityById(activityId);
            Session session = sessionRepository.findUserIdByToken(sessionToken);
            Optional<User> user = userRepository.findById(profileId);
            EnumSet<Visibility> options = EnumSet.of(Visibility.PUBLIC, Visibility.PRIVATE, Visibility.RESTRICTED);

            if(sessionToken == null){
                return new ResponseEntity<String>("Invalid Session", HttpStatus.valueOf(401));
            }
            if (activity == null) {
                return new ResponseEntity<String>("Activity not found", HttpStatus.NOT_FOUND);
            }
            if(!profileId.equals(session.getUser().getUserId()) &&  session.getUser().getPermissionLevel() == 0) {
                return new ResponseEntity<String>("Invalid User", HttpStatus.valueOf(403));
            }
            if(!options.contains(request.getVisibility())){
                return new ResponseEntity<String>("Invalid visibility type selected", HttpStatus.valueOf(403));
            }

        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "Cannot change the visibility status of the activity");
        }
        return activityService.updateActivityVisibility(profileId, activityId, request);
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

    /***
     * Controller endpoint that receives requests to get activity participants from the database. Calls the service method
     * where all the logic is handled.
     * @param activityId the id of the activity.
     * @param page the requested page to return.
     * @param size the number of result that the page will contain.
     * @return 404 status if the provided activity does not exist, otherwise it returns a 200 code with a list of the
     * participants.
     */
    @GetMapping("/activities/{activityId}/participants/")
    public ResponseEntity getParticipants(@PathVariable("activityId") long activityId, @RequestParam("page") int page, @RequestParam("size") int size) {
        return activityService.getActivityParticipants(activityId, page, size);
    }

    /***
     * Controller endpoint that receives requests to get activity organizers from the database. Calls the service method
     * where all the logic is handled.
     * @param activityId the id of the activity.
     * @param page the requested page to return.
     * @param size the number of result that the page will contain.
     * @return 404 status if the provided activity does not exist, 400 status if pagination parameters are invalid,
     * otherwise it returns a 200 code with a list of the organizers.
     */
    @GetMapping("/activities/{activityId}/organizers/")
    public ResponseEntity getOrganizers(@PathVariable("activityId") long activityId, @RequestParam("page") int page, @RequestParam("size") int size) {
        return activityService.getActivityOrganizers(activityId, page, size);
    }
    /**
     * Handles requests to see the visibility, whether a user is allowed to see an activity.
     *
     * @param activityId the activity id.  :)
     * @param profileId the user id.
     * @return a response entity that contains a page object filled with shared users
     */

    @GetMapping("/activities/{activityId}/profiles/{profileId}/uservisibility")
    public ResponseEntity<Map<String, String>> getUserActivityVisibility(@CookieValue(value = "s_id") String sessionToken,
                                                                         @PathVariable("activityId") Long activityId,
                                                                         @PathVariable("profileId") Long profileId) {

        Session userSession = sessionRepository.findUserIdByToken(sessionToken);
        if (userSession == null) {
            return new ResponseEntity("Invalid Session", HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok().body(activityService.getUserActivityVisibility(activityId, profileId));
    }

    /**
     * Controller endpoint that receives requests to get a list of activity changes.
     * @param activityId the id of the activity.
     * @param page the requested page to return.
     * @param size the number of result that the page will contain.
     * @return 404 status if the provided activity does not exist, 400 status if pagination parameters are invalid,
     * otherwise it returns a 200 code with a list of the changes.
     */
    @GetMapping("/activities/{activityId}/changes/")
    public ResponseEntity getChanges(@PathVariable("activityId") long activityId, @RequestParam("page") int page, @RequestParam("size") int size) {
        return activityService.getActivityChanges(activityId, page, size);
    }

    /**
     * Controller endpoint that receives requests to get the number of followers, participants and organisers for an activity
     * @param activityId the id of the activity for which the numbers are being requested
     * @param sessionToken session token of the user making the request
     * @return response entity with json containing the counts and status of the request
     */
    @GetMapping("/activities/{activityId}/stats")
    public ResponseEntity getActivityStats(@PathVariable("activityId") long activityId, @CookieValue(value = "s_id") String sessionToken) {
        Session session = sessionRepository.findUserIdByToken(sessionToken);
        if (session == null) {
            return responseHandler.formatErrorResponseString(401, "Invalid Session");
        } else {
            return activityService.getStats(activityId);
        }
    }

    @DeleteMapping("/activities/{activityId}/roles/{userEmail}")
    public ResponseEntity optOutOfActivity(@PathVariable("activityId") long activityId, @PathVariable("userEmail") String email,
                                           @CookieValue(value = "s_id") String sessionToken) {
        Session session = sessionRepository.findUserIdByToken(sessionToken);
        if (session == null) {
            return responseHandler.formatErrorResponseString(401, "Invalid session");
        } else {
            long userId = userRepository.getIdByAnyEmail(email);
            return activityService.optOutOfActivity(activityId, userId);
        }
    }
}
