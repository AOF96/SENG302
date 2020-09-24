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
import org.springframework.data.web.JsonPath;
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

    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    private ActivityRepository activityRepository;
    private ActivityService activityService;
    private AchievementRepository achievementRepository;

    private ResponseHandler responseHandler;

    /**
     * Contructs an Activity Controller, passing in the repositories and service so that they can be accessed.
     *
     * @param userRepository     The repository containing Users
     * @param sessionRepository  The repository containing Sessions
     * @param activityRepository The repository containing Activities
     * @param activityService    The service for Activities
     */
    public ActivityController(UserRepository userRepository,
                              SessionRepository sessionRepository, ActivityService activityService,
                              ActivityRepository activityRepository, AchievementRepository achievementRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.activityRepository = activityRepository;
        this.activityService = activityService;
        this.achievementRepository = achievementRepository;
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

    /**
     * Handles requests to get list of  achievements for an activiy
     * @param profileId id of user attempting to delete achievement
     * @param activityId id of activity that has the achievement associated with it
     * @param sessionToken session token of the user which is used for validation checks
     * @return response entity with code dependant on success or failure of the request
     */
    @GetMapping("/profiles/{profileId}/activities/{activityId}/achievements")
    public ResponseEntity getAchievement(@PathVariable("profileId") long profileId,
                                            @PathVariable("activityId") long activityId,
                                            @CookieValue(value = "s_id") String sessionToken) {
        return activityService.getAchievement(profileId, activityId,sessionToken);
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
     * Handles the request for activity's visibility and choosing groups of users to keep.
     * @param profileId the logged in user's id.
     * @param activityId the activity id of the activity being managed
     * @param sessionToken the user's token from the cookie for their current session.
     * @param jsonString the json body
     * @return A ResponseEntity stating the results
     */
    @PutMapping("/profiles/{profileId}/activities/{activityid}/visibilityGroups")
    public ResponseEntity<String> updateActivityVisibilityWithGroups(@PathVariable("profileId") Long profileId,
                                                           @PathVariable("activityid") Long activityId,
                                                           @CookieValue(value = "s_id") String sessionToken,
                                                           @RequestBody String jsonString) {
        Map<String, Object> json = new JacksonJsonParser().parseMap(jsonString);
        Visibility visibility;
        try {
            String visibilityString = (String) json.get("visibility");
            visibility = Visibility.valueOf(visibilityString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<String>("Invalid visibility type selected", HttpStatus.valueOf(403));
        }
        boolean keepFollowers = (boolean) json.get("followers");
        boolean keepParticipants = (boolean) json.get("participants");
        boolean keepOrganisers = (boolean) json.get("organisers");

        Activity activity = activityRepository.findActivityById(activityId);
        Session session = sessionRepository.findUserIdByToken(sessionToken);

        if(sessionToken == null){
            return new ResponseEntity<String>("Invalid Session", HttpStatus.valueOf(401));
        }
        if (activity == null) {
            return new ResponseEntity<String>("Activity not found", HttpStatus.NOT_FOUND);
        }
        if(!profileId.equals(session.getUser().getUserId()) &&  session.getUser().getPermissionLevel() == 0) {
            return new ResponseEntity<String>("Invalid User", HttpStatus.valueOf(403));
        }

        return activityService.updateActivityVisibility(activityId, visibility, keepFollowers, keepParticipants,
                keepOrganisers);
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

    /**
     * Controller endpoint that receives requests to remove a users role from an activity and handles session checking.
     * @param activityId Id of the activity from which the users role will be removed
     * @param email Primary email of the user whose role in the activity will be removed
     * @param sessionToken Session token of the user making the request
     * @return Response entity with status depending on the outcome of the request
     */
    @DeleteMapping("/activities/{activityId}/roles/{userEmail}")
    public ResponseEntity optOutOfActivity(@PathVariable("activityId") long activityId, @PathVariable("userEmail") String email,
                                           @CookieValue(value = "s_id") String sessionToken) {
        Session session = sessionRepository.findUserIdByToken(sessionToken);
        ActivityRole role = activityRepository.getUsersRoleForActivity(activityId, session.getUser().getUserId());
        if (session == null || !session.getUser().getUserId().equals(activityRepository.getOne(activityId).getAuthor().getUserId()) && session.getUser().getPermissionLevel() < 1 && role == null) {
            return responseHandler.formatErrorResponseString(401, "Invalid session");
        } else {
            long userId = userRepository.getIdByAnyEmail(email);
            return activityService.optOutOfActivity(activityId, userId);
        }
    }

    /**
     * Gets the ActivityRole of a User who is related to an Activity. Otherwise, sends a value of "none".
     * @param activityId The ID of the Activity
     * @param userId The ID of the User
     * @return A ResponseEntity stating the result
     */
    @GetMapping("activities/{activityId}/role/{userId}")
    public ResponseEntity getRoleOfUserForActivity(@PathVariable("activityId") long activityId,
                                                   @PathVariable("userId") long userId) {
        Activity activity = activityRepository.findActivityById(activityId);
        Optional<User> user = userRepository.getUserById(userId);
        Map<String, String> result = new HashMap<>();
        if (activity == null) {
            return new ResponseEntity<String>("Activity not found", HttpStatus.NOT_FOUND);
        }
        if (user .isEmpty()) {
            return new ResponseEntity<String>("User not found", HttpStatus.NOT_FOUND);
        }
        ActivityRole role = activityService.getRoleOfUserForActivity(activity, user.get());
        if (role != null) {
            result.put("role", role.getRole());
        } else {
            result.put("role", "none");
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * Handles request to add a new result
     * @param result Result to add
     * @param profileId Id of profile to add to
     * @param achievementId Id of achievement to add to
     * @param sessionToken Session of user sending the request
     * @return ResponseEntity of result of the operation
     */
    @PostMapping("/profiles/{profileId}/achievements/{achievementId}/results")
    public ResponseEntity<String> addResult(@Valid @RequestBody Result result,
                                            @PathVariable("profileId") long profileId,
                                            @PathVariable("achievementId") long achievementId,
                                            @CookieValue(value = "s_id") String sessionToken) {
        if (achievementRepository.findAchievementById(achievementId) == null) {
            return new ResponseEntity<>("Achievement not found", HttpStatus.NOT_FOUND);
        } else if (!userRepository.findById(profileId).isPresent()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        } else if (sessionRepository.findUserIdByToken(sessionToken) == null) {
            return new ResponseEntity<>("Session invalid", HttpStatus.UNAUTHORIZED);
        } else {
            return activityService.addResult(result, profileId, achievementId);
        }
    }

    /***
     * Handles the request to retrieve a single result for the given achievement.
     * @param profileId the if of the user making the request.
     * @param achievementId the id of the achievement that contains the result.
     * @param resultId the id of the requested result.
     * @return a 200 response with the requested result if it exists, otherwise a 404 response code.
     */
    @GetMapping("/profiles/{profileId}/achievements/{achievementId}/results/{resultId}")
    public ResponseEntity getOneResult(@PathVariable Long profileId, @PathVariable Long achievementId, @PathVariable Long resultId) {
        return activityService.retrieveOneResult(profileId, achievementId, resultId);
    }

    /***
     * Handles the request to retrieve a single result for the given achievement.
     * @param achievementId the id of the achievement that contains the result.
     * @return a 200 response with the all result if any exists, otherwise a 404 response code.
     */
    @GetMapping("/activities/achievements/{achievementId}/results")
    public ResponseEntity getSAllResult(@PathVariable Long achievementId) {
        return activityService.retrieveAllResult(achievementId);
    }

    /***
     * Handles the request to add a location to an activity and performs session and permission checks.
     * @param location the location to be added to the activity.
     * @param activityId the id of the activity that the location will be added too.
     * @param sessionToken sessionToken of the user that has made the request.
     * @return a response code with a value depending on the operations result, 401 for invalid session,
     * 403 for forbidden user, 200 for success or 500 for internal server error.
     */
    @PostMapping("/activities/{activityId}/location")
    public ResponseEntity addLocationToActivity(@RequestBody @Valid Location location,
                                                @PathVariable Long activityId,
                                                @CookieValue(value = "s_id") String sessionToken) {
        Session session = sessionRepository.findUserIdByToken(sessionToken);
        if (session == null || !session.getUser().getUserId().equals(activityRepository.getOne(activityId).getAuthor().getUserId()) && session.getUser().getPermissionLevel() < 1) {
            return responseHandler.formatErrorResponse(401, "Invalid Session");
        }

        return activityService.addLocationToActivity(activityId, location);
    }

    /***
     * Handles the request to get all the activities that are located within the given bounds.
     * @param latitudeTopRight the given latitude.
     * @param longitudeTopRight the given longitude.
     * @param latitudeBottomLeft the given longitude.
     * @param longitudeBottomLeft the given longitude.
     * @return a 200 response with the list of all the activities if any exist in the given bound, 400 for invalid user input, 401 for invalid session,
     * or 500 for internal server error.
     */
    @GetMapping("/activities/within/range")
    public ResponseEntity<Activity> getActivitiesWithinGivenRange(@RequestParam("latitudeTopRight") Double latitudeTopRight,
                                                        @RequestParam("longitudeTopRight") Double longitudeTopRight,
                                                        @RequestParam("latitudeBottomLeft") Double latitudeBottomLeft,
                                                        @RequestParam("longitudeBottomLeft") Double longitudeBottomLeft,
                                                        @CookieValue(value = "s_id") String sessionToken) {
        ResponseEntity result;
        if (sessionToken == null || sessionRepository.findUserIdByToken(sessionToken) == null) {
            result = responseHandler.formatErrorResponse(401, "Invalid Session");
        } else if (latitudeTopRight == null || longitudeTopRight == null || latitudeBottomLeft == null || longitudeBottomLeft == null) {
            result =  responseHandler.formatErrorResponse(400, "Invalid latitude or longitude values.");
        } else {
            result = activityService.getActivitiesWithinGivenRange(latitudeBottomLeft, latitudeTopRight,
                    longitudeBottomLeft, longitudeTopRight,
                    sessionRepository.findUserIdByToken(sessionToken).getUser().getUserId());
        }
        return result;
    }

    /**
     * Endpoint for retrieving an activities location. Performs session checks as well as 404 checks.
     * @param activityId Location of the activity with this id will be retrieved.
     * @param sessionToken Session token of the user making the request.
     * @return Response entity with code value depending on the outcome of the operation, 401 invalid session,
     * 404 activity not found.
     */
    @GetMapping("/activities/{activityId}/location")
    public ResponseEntity getActivityLocation(@PathVariable Long activityId,
                                              @CookieValue(value = "s_id") String sessionToken) {
        Session userWithSession = sessionRepository.findUserIdByToken(sessionToken);
        if (userWithSession == null) {
            return responseHandler.formatErrorResponse(401, "Invalid Session");
        }

        Optional<Activity> optionalActivity = activityRepository.findById(activityId);
        if (optionalActivity.isEmpty()) {
            return  responseHandler.formatErrorResponse(404, "Activity not found");
        } else {
            return activityService.getActivityLocation(activityId);
        }
    }
}
