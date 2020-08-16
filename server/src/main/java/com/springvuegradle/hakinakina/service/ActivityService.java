package com.springvuegradle.hakinakina.service;

import com.springvuegradle.hakinakina.exception.ActivityNotFoundException;
import com.springvuegradle.hakinakina.dto.ActivityVisibilityDto;
import com.springvuegradle.hakinakina.dto.FeedPostDto;
import com.springvuegradle.hakinakina.dto.SearchUserDto;
import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.exception.UserNotFoundException;
import com.springvuegradle.hakinakina.repository.*;
import com.springvuegradle.hakinakina.util.ErrorHandler;
import com.springvuegradle.hakinakina.util.ResponseHandler;
import net.minidev.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;

@Service
public class ActivityService {

    public UserRepository userRepository;
    public ActivityRepository activityRepository;
    public ActivityTypeRepository activityTypeRepository;
    public PassportCountryRepository countryRepository;
    public SessionRepository sessionRepository;
    public SearchRepository searchRepository;
    public ActivityChangeRepository activityChangeRepository;
    private ResponseHandler responseHandler = new ResponseHandler();
    private UserActivityRoleRepository userActivityRoleRepository;
    private SearchService searchService;

    public ActivityService(UserRepository userRepository,
                           ActivityRepository activityRepository,
                           ActivityTypeRepository activityTypeRepository,
                           PassportCountryRepository countryRepository,
                           SessionRepository sessionRepository,
                           UserActivityRoleRepository userActivityRoleRepository,
                           SearchRepository searchRepository,
                           ActivityChangeRepository activityChangeRepository,
                           SearchService searchService) {
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.activityTypeRepository = activityTypeRepository;
        this.countryRepository = countryRepository;
        this.sessionRepository = sessionRepository;
        this.activityChangeRepository = activityChangeRepository;
        this.searchRepository = searchRepository;
        this.userActivityRoleRepository = userActivityRoleRepository;
        this.searchService = searchService;
    }

    /**
     * Adds an activity for the user*
     *
     * @param activity     the activity the user wants to add
     * @param profileId    the user's id
     * @param sessionToken the user's token from their current session
     * @return response entity to inform user if adding an activity was successful or not
     */
    public ResponseEntity<String> addActivity(Activity activity, long profileId, String sessionToken) {
        try {
            Optional<User> author = userRepository.getUserById(profileId);
            if (author.isEmpty()){
                return new ResponseEntity<>("Invalid User ID", HttpStatus.valueOf(403));
            } else {
                activity.setAuthor(author.get());
            }
            if (activity.getStartTime() != null) {
                activity.getStartTime().setTime((activity.getStartTime().getTime() - (12 * 60 * 60 * 1000)));
            }
            if (activity.getEndTime() != null) {
                activity.getEndTime().setTime((activity.getEndTime().getTime() - (12 * 60 * 60 * 1000)));
            }
            Session session = sessionRepository.findUserIdByToken(sessionToken);
            if (session == null) {
                return new ResponseEntity<>("Invalid Session", HttpStatus.valueOf(401));
            }
            if (profileId != session.getUser().getUserId() && session.getUser().getPermissionLevel() == 0) {
                return new ResponseEntity<>("Invalid User", HttpStatus.valueOf(403));
            }

            //If there are no activity types listed
            if (activity.getActivityTypes().size() == 0) {
                return new ResponseEntity<>("Activity must have at least one activity type", HttpStatus.valueOf(400));
            }

            for (ActivityType activityType : activity.getActivityTypes()) {
                if (activityTypeRepository.findActivityTypeByName(activityType.getName()) == null) {
                    return new ResponseEntity<>("Selected activity type " + activityType.getName() + " does not exist", HttpStatus.valueOf(400));
                }
            }

            //If activity has a duration, start time and end time must be specified
            if (!activity.isContinuous()) {
                if (activity.getStartTime() == null || activity.getEndTime() == null) {
                    return new ResponseEntity<>("Activity with a duration must specify start time and end time", HttpStatus.valueOf(400));
                }

                Date date = new Date();
                Timestamp ts = new Timestamp(date.getTime());

                if (!activity.getStartTime().after(ts)) {
                    return new ResponseEntity<>("Activity start date and time must be in the future", HttpStatus.valueOf(400));
                }

                if (activity.getStartTime().after(activity.getEndTime())) {
                    return new ResponseEntity<>("Activity end date and time must be after the start date and time", HttpStatus.valueOf(400));
                }
            }

            //TODO Validate activity location in U9
            activity.setAuthor(userRepository.getUserById(profileId).get());
            Activity savedActivity = activityRepository.save(activity);
            UserActivityKey userActivityKey = new UserActivityKey(profileId, savedActivity.getId());
            userActivityRoleRepository.save(new UserActivityRole(userActivityKey, ActivityRole.CREATOR));


            return new ResponseEntity<>("Activity has been created", HttpStatus.valueOf(201));
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "cannot add activity");
            return new ResponseEntity<>("An error occurred", HttpStatus.valueOf(500));
        }
    }

    /**
     * Edits an activity for the user
     *
     * @param newActivity  the activity the user wants to add
     * @param profileId    the user's id
     * @param sessionToken the user's token from their current session
     * @return response entity to inform user if adding an activity was successful or not
     */
    public ResponseEntity editActivity(Activity newActivity, long profileId, long activityId, String sessionToken) {
        EnumSet<Visibility> options = EnumSet.of(Visibility.PUBLIC, Visibility.PRIVATE, Visibility.RESTRICTED);
        try {
            addToChangesDatabase(newActivity, activityRepository.getOne(activityId), profileId, activityId);
            Session session = sessionRepository.findUserIdByToken(sessionToken);
            if (session == null) {
                return new ResponseEntity("Invalid Session", HttpStatus.valueOf(401));
            }
            if (profileId != session.getUser().getUserId() && session.getUser().getPermissionLevel() == 0) {
                return new ResponseEntity("Invalid User", HttpStatus.valueOf(403));
            }

            // If activity has a duration, start time and end time must be specified
            if (!newActivity.isContinuous()) {
                if (newActivity.getStartTime() == null || newActivity.getEndTime() == null) {
                    return new ResponseEntity("Activity with a duration must specify start time and end time", HttpStatus.valueOf(400));
                }

                Date date = new Date();
                Timestamp ts = new Timestamp(date.getTime());

                if (!newActivity.getStartTime().after(ts)) {
                    return new ResponseEntity("Activity start date and time must be in the future", HttpStatus.valueOf(400));
                }

                if (newActivity.getStartTime().after(newActivity.getEndTime())) {
                    return new ResponseEntity("Activity end date and time must be after the start date and time", HttpStatus.valueOf(400));
                }
            }

            for (ActivityType activityType : newActivity.getActivityTypes()) {
                if (activityTypeRepository.findActivityTypeByName(activityType.getName()) == null) {
                    return new ResponseEntity("Selected activity type " + activityType.getName() + " does not exist", HttpStatus.valueOf(400));
                }
            }

            //If there are no activity types listed
            if (newActivity.getActivityTypes().size() == 0) {
                return new ResponseEntity("Activity must have at least one activity type", HttpStatus.valueOf(400));
            }
            if(!options.contains(newActivity.getVisibility())){
                return new ResponseEntity("Activity visibility must in the type selected", HttpStatus.valueOf(400));
            }

            // check the user has given activity
            Activity activity = activityRepository.findActivityById(activityId);
            if (activity == null) {
                return new ResponseEntity("Invalid Activity", HttpStatus.valueOf(404));
            }

            //TODO Validate activity location in U9

            activity.setName(newActivity.getName());
            activity.setDescription(newActivity.getDescription());
            activity.setActivityTypes(newActivity.getActivityTypes());
            activity.setContinuous(newActivity.isContinuous());
            activity.setStartTime(newActivity.getStartTime());
            activity.setEndTime(newActivity.getEndTime());
            activity.setLocation(newActivity.getLocation());
            activity.setVisibility(newActivity.getVisibility());

            activityRepository.save(activity);

            return new ResponseEntity("Activity has been updated", HttpStatus.valueOf(200));
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "cannot edit activity");
            return new ResponseEntity("An error occurred", HttpStatus.valueOf(500));
        }
    }

    /***
     * Adds a new field to the activity change database that details the changes made to an activity
     * @param newActivity activity containing all changes being made
     * @param oldActivity activity to be modified
     * @param profileId id of the user making the changes
     * @param activityId id of the activity being changed
     */
    private void addToChangesDatabase(Activity newActivity, Activity oldActivity, Long profileId, Long activityId) {
        Set<ActivityAttribute> activityChanges = oldActivity.findActivityChanges(newActivity);
        StringBuilder description = new StringBuilder();
        for (ActivityAttribute attribute : activityChanges) {
            if (attribute == ActivityAttribute.NAME) {
                description.append("*Activity name was changed.");
            } else if (attribute == ActivityAttribute.DESCRIPTION) {
                description.append("*Description was updated.");
            } else if (attribute == ActivityAttribute.ACTIVITY_TYPES && newActivity.getActivityTypes().size() != 0) {
                description.append("*Activity types were updated.");
            } else if (attribute == ActivityAttribute.CONTINUOUS) {
                if(newActivity.isContinuous()){
                    description.append("*Activity changed to continuous.");
                }else{
                    description.append("*Activity changed to duration.");
                }
            } else if (attribute == ActivityAttribute.START_TIME) {
                description.append("*Start time changed to ").append(newActivity.getStartTime()).append("\n");
            } else if (attribute == ActivityAttribute.VISIBILITY) {
                description.append("*Activity Visibility was changed.");
            }  else if (attribute == ActivityAttribute.END_TIME) {
                description.append("*End time changed to: ").append(newActivity.getEndTime()).append("\n");
            } else if (attribute == ActivityAttribute.LOCATION) {
                description.append("*Location was updated.");
            }
        }
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        ActivityChange activityChangesToAdd = new ActivityChange(description.toString(), timestamp,
                userRepository.getOne(profileId), activityRepository.getOne(activityId));
        activityChangeRepository.save(activityChangesToAdd);
    }

    /**
     * Removes an activity from the database if the user is authenticated.
     * @param profileId the user's profile id.
     * @param activityId the activity id to be removed.
     * @param sessionToken the user's token from their current session.
     * @return response entity with the result of the operation.
     */
    @Transactional
    public ResponseEntity removeActivity(long profileId, long activityId, String sessionToken) {

        ResponseEntity result;
        try {
            Session session = sessionRepository.findUserIdByToken(sessionToken);
            Activity activityToDelete = activityRepository.findActivityById(activityId);

            if (sessionToken == null) {
                result = responseHandler.formatErrorResponse(401, "Invalid Session");

            } else if (activityToDelete == null) {
                result = responseHandler.formatErrorResponse(404, "Activity not found");

            } else if ((profileId != session.getUser().getUserId()
                    || userActivityRoleRepository.findByIdActivityIdAndIdUserIdAndActivityRole(profileId, activityId, ActivityRole.CREATOR).isPresent())
                    && session.getUser().getPermissionLevel() == 0) {
                result = responseHandler.formatErrorResponse(403, "Invalid user");

            } else {
                userActivityRoleRepository.deleteByIdActivityIdAndIdUserId(activityId, profileId);
                activityRepository.delete(activityToDelete);
                result = responseHandler.formatSuccessResponse(200, "Activity successfully deleted");
            }
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "Cannot delete activity");
            result = responseHandler.formatErrorResponse(500, "An error occurred");
        }
        return result;
    }

    /**
     * Updates the Visibility of an Activity. Also sets the Activity's shared users and organisers/participants if the
     * Activity is RESTRICTED and the request contains a list of accessors. If so, then firstly iterates through the request's
     * accessors and creates relationships between Users by ANY email and the Activity along with the associated role.
     * Then deletes all existing relationships and saves the new ones. Returns an error response if an email does not
     * exist or a role is invalid. The shared users is set to an empty set if the Activity is being set to PUBLIC or
     * PRIVATE.
     *
     * @param profileId  the logged in user's id.
     * @param activityId the activity id of the activity being managed.
     * @param request level of visibility and the set of user's email allowed to access the activity.
     * @return A ResponseEntity with a success or error code.
     */
    @Transactional
    public ResponseEntity<String> updateActivityVisibility (Long profileId,
                                                            Long activityId,
                                                            ActivityVisibilityDto request) {
        Activity activity = activityRepository.findActivityById(activityId);
        activity.setVisibility(request.getVisibility());

        Set<User> accessors = new HashSet<User>();
        Map<String, String> errors = new HashMap<>();
        // Store these in a list and save at the end so that we avoid deleting all existing ones if there is an error.
        List<UserActivityRole> newRelationships = new ArrayList<>();

        if (request.getVisibility().equals(Visibility.RESTRICTED) && request.getAccessors() != null){
            for (Map<String, String> accessor : request.getAccessors()) {
                String email = accessor.get("email");
                ActivityRole role;
                try {
                    role = ActivityRole.valueOf(accessor.get("role").toUpperCase());
                } catch (IllegalArgumentException e) {
                    errors.put("message", "No such role as: " + accessor.get("role"));
                    return new ResponseEntity<String>(JSONObject.toJSONString(errors), HttpStatus.valueOf(400));
                }
                Long userId = userRepository.getIdByAnyEmail(accessor.get("email"));
                if (userId == null) {
                    errors.put("message", "No user with email: " + email);
                    return new ResponseEntity<String>(JSONObject.toJSONString(errors), HttpStatus.valueOf(400));
                }
                if (userId.equals(activity.getAuthor().getUserId())) {
                    errors.put("message", "Cannot add the activity author as a shared User.");
                    return new ResponseEntity<String>(JSONObject.toJSONString(errors), HttpStatus.valueOf(400));
                }

                User user = userRepository.getOne(userId);
                accessors.add(user);
                UserActivityKey newKey = new UserActivityKey(userId, activityId);
                UserActivityRole newRelationship = new UserActivityRole(newKey, role);
                newRelationships.add(newRelationship);
            }
            userActivityRoleRepository.deleteByActivity(activity);
        }

        saveRelationships(newRelationships);
        activity.setUsersShared(accessors);
        return new ResponseEntity<String>("Activity Visibility Status Updated", HttpStatus.OK);
    }

    /**
     * A helper method to save each UserActivityRole object to the UserActivityRoleRepository
     * @param newRelationships A List of UserActivityRoles
     */
    public void saveRelationships(List<UserActivityRole> newRelationships) {
        for (UserActivityRole relationship : newRelationships) {
            userActivityRoleRepository.save(relationship);
        }
    }

    /**
     * The function maps the activity to its id, name and description.
     * @param activities list of activities
     * @return a mapped list of activities to its id, name and description
     */

    public List<Map<String, String>> getActivitySummaries(List<Activity> activities) {
        List<Map<String, String>> result = new ArrayList<>();
        for (Activity activity : activities) {
            Map<String, String> activityMap = new HashMap<>();
            activityMap.put("id", activity.getId().toString());
            activityMap.put("name", activity.getName());
            activityMap.put("description", activity.getDescription());
            result.add(activityMap);
        }

        return result;
    }

    /***
     * Retrieves a list of shared users from the given activity with paginated results.
     * @param activityId the id of the activity.
     * @param page the requested page to return.
     * @param size the number of result that the page will contain.
     * @return 404 status if the provided activity does not exist, 400 status if pagination parameters are invalid,
     * otherwise it returns a 200 code with a list of the shared users.
     */
    public Page<SearchUserDto> getSharedUsers(Long activityId, int page, int size) {
        Page<User> users = searchRepository.getSharedUsers(PageRequest.of(page, size), activityId);
        return responseHandler.userPageToSearchResponsePage(users);
    }

    /**
     * Retrieves a list of participants from the given activity with paginated results.
     * @param activityId the id of the activity.
     * @param page the requested page to return.
     * @param size the number of result that the page will contain.
     * @return 404 status if the provided activity does not exist, 400 status if pagination parameters are invalid,
     * otherwise it returns a 200 code with a list of the organizers.
     */
    public ResponseEntity getActivityParticipants(Long activityId, int page, int size) {
        ResponseEntity result;
        try {
            if (page < 0 || size < 0) {
                result = responseHandler.formatErrorResponse(400, "Invalid pagination parameters");
            }
            else if (activityRepository.findActivityById(activityId) == null) {
                result = responseHandler.formatErrorResponse(404, "Activity not found");
            } else {
                Page<User> userPage = userRepository.getParticipantsOROrganisers(PageRequest.of(page, size), activityId, ActivityRole.PARTICIPANT);
                Page<SearchUserDto> users = responseHandler.userPageToSearchResponsePage(userPage);
                result = new ResponseEntity(users, HttpStatus.OK);
            }
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "Could not retrieve participants");
            result = responseHandler.formatErrorResponse(500, "An error occurred");
        }

        return result;
    }

    /***
     * Retrieves a list of organizers from the given activity with paginated results.
     * @param activityId the id of the activity.
     * @param page the requested page to return.
     * @param size the number of result that the page will contain.
     * @return 404 status if the provided activity does not exist, 400 status if pagination parameters are invalid,
     * otherwise it returns a 200 code with a list of the organizers.
     */
    public ResponseEntity getActivityOrganizers(Long activityId, int page, int size) {
        ResponseEntity result;
        try {
            if (page < 0 || size < 0) {
                result = responseHandler.formatErrorResponse(400, "Invalid pagination parameters");
            }
            else if (activityId == null || activityRepository.findActivityById(activityId) == null) {
                result = responseHandler.formatErrorResponse(404, "Activity not found");
            } else {
                Page<User> userPage = userRepository.getParticipantsOROrganisers(PageRequest.of(page, size), activityId, ActivityRole.ORGANISER);
                Page<SearchUserDto> users = responseHandler.userPageToSearchResponsePage(userPage);
                result = new ResponseEntity(users, HttpStatus.OK);
            }
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "Could not retrieve organizers");
            result = responseHandler.formatErrorResponse(500, "An error occurred");
        }

        return result;
    }

    /***
     * Retrieves a list of changes from the given activity with paginated results.
     * @param activityId the id of the activity.
     * @param page the requested page to return.
     * @param size the number of result that the page will contain.
     * @return 404 status if the provided activity does not exist, 400 status if pagination parameters are invalid,
     * otherwise it returns a 200 code with a list of the changes.
     */
    public ResponseEntity getActivityChanges(Long activityId, int page, int size) {
        ResponseEntity result;
        try {
            if (page < 0 || size < 0) {
                result = responseHandler.formatErrorResponse(400, "Invalid pagination parameters");
            }
            else if (activityId == null || activityRepository.findActivityById(activityId) == null) {
                result = responseHandler.formatErrorResponse(404, "Activity not found");
            } else {
                Page<ActivityChange> activityChanges = activityChangeRepository.getChangesForActivity(PageRequest.of(page, size), activityId);
                List<ActivityChange> changesList = activityChanges.toList();
                List<FeedPostDto> posts = new ArrayList<>();
                for (ActivityChange activityChange : changesList) {

                    FeedPostDto newPost = new FeedPostDto();
                    newPost.setContent(activityChange);
                    posts.add(newPost);
                }
                result = new ResponseEntity(posts, HttpStatus.OK);
            }
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "Could not retrieve changes");
            result = responseHandler.formatErrorResponse(500, "An error occurred");
        }

        return result;
    }

    /**
     * Removes a user from following an activity.
     * @param profileId id of user that is unfollowing
     * @param activityId activity to unfollow
     * @param sessionToken session id of the user
     * @return response entity with the result of the operation.
     */
    public ResponseEntity<String> unFollow(Long profileId, Long activityId, String sessionToken) {
        ResponseEntity<String> result;
        try {
            Session session = sessionRepository.findUserIdByToken(sessionToken);
            Activity activity = activityRepository.findActivityById(activityId);
            if (sessionToken == null) {
                result = responseHandler.formatErrorResponseString(401, "Invalid Session");
            } else if (activity == null) {
                result = responseHandler.formatErrorResponseString(404, "Activity not found");
            } else if (!profileId.equals(session.getUser().getUserId()) && session.getUser().getPermissionLevel() == 0) {
                result = responseHandler.formatErrorResponseString(403, "Invalid user");
            } else {
                User user = userRepository.getUserById(profileId).get();
                activity.removeUser(user);
                activityRepository.save(activity);
                userRepository.save(user);
                result = responseHandler.formatSuccessResponseString(200, "Unfollowed activity");
            }
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "Cannot unfollow");
            result = responseHandler.formatErrorResponseString(500, "An error occurred");
        }
        return result;
    }

    /**
     * Returns if the given user is following the given activity
     * @param profileId user requested
     * @param activityId activity to check
     * @param sessionToken session token of the requesting user
     * @return formatted response with result
     */
    public ResponseEntity<String> checkFollowing(Long profileId, Long activityId, String sessionToken) {
        ResponseEntity<String> result;
        try {
            Session session = sessionRepository.findUserIdByToken(sessionToken);
            Activity activity = activityRepository.findActivityById(activityId);
            if (sessionToken == null) {
                result = responseHandler.formatErrorResponseString(401, "Invalid Session");
            } else if (activity == null) {
                result = responseHandler.formatErrorResponseString(404, "Activity not found");
            } else if (!profileId.equals(session.getUser().getUserId()) && session.getUser().getPermissionLevel() == 0) {
                result = responseHandler.formatErrorResponseString(403, "Invalid user");
            } else {
                User user = userRepository.getUserById(profileId).get();
                boolean following = activity.getUsers().contains(user);
                result = responseHandler.formatSuccessResponseString(200, Boolean.toString(following));
            }
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "Cannot check following");
            result = responseHandler.formatErrorResponseString(500, "An error occurred");
        }
        return result;
    }

    /**
     * Service method that processes requests to get the number of followers, participants and organisers for an activity
     * @param activityId the id of the activity for which the numbers are being requested
     * @return response entity with json containing the counts and status of the request
     */
    public ResponseEntity getStats(long activityId) {
        try {
            int numFollowers = activityRepository.getNumFollowersForActivity(activityId);
            int numOrganizers = activityRepository.getNumOrganisersForActivity(activityId);
            int numParticipants = activityRepository.getNumParticipantsForActivity(activityId);
            String jsonToReturn = "{\n" +
                    "  \"followers\": " + numFollowers + ",\n" +
                    "  \"participants\": " + numParticipants + ",\n" +
                    "  \"organisers\": " + numOrganizers + "\n" +
                    "}";
            return new ResponseEntity(jsonToReturn, HttpStatus.OK);
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "Cannot get stats");
            return responseHandler.formatErrorResponseString(500, "An error occurred");
        }
    }

    public ResponseEntity optOutOfActivity(long activityId, long userId) {
        try {
            userActivityRoleRepository.deleteUserFromActivityRoles(activityId, userId);
            return responseHandler.formatErrorResponseString(200, "Success");
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "Cannot delete user role");
            return responseHandler.formatErrorResponseString(500, "An error occurred");
        }
    }

    /**
     * Handles requests to retrieve the visibility, whether a user is allowed to see an activity.
     *
     * @param activityId the activity id of activity user is trying to view.
     * @param profileId the user id of user who is trying to view an activity.
     * @return response text of user 'allowed' or 'not allowed' to see a certain activity
     */
    public Map<String, String> getUserActivityVisibility(long activityId, long profileId) {

        // check if activity and user exist on the database
        Activity activity = activityRepository.findById(activityId).orElseThrow(ActivityNotFoundException::new);
        User user = userRepository.findById(profileId).orElseThrow(UserNotFoundException::new);

        // check the visibility of the activity
        Visibility visibility = activity.getVisibility();

        // returning object
        Map<String, String> userVisibility = new HashMap<>();

        // if public everyone is allowed to view the activity
        if (visibility == Visibility.PUBLIC) {
            userVisibility.put("visibility","allowed");
        }

        // if private, only creator can see the activity
        else if (visibility == Visibility.PRIVATE) {
            if(user.getUserId().equals(activity.getAuthor().getUserId())){
                userVisibility.put("visibility","allowed");
            } else {
                userVisibility.put("visibility","not allowed");
            }
        }

        // if restricted, only specific users chosen by the creator can see it
        // (meaning creator and user in user_activity_shared table)
        else if (visibility == Visibility.RESTRICTED) {
            if(user.getUserId().equals(activity.getAuthor().getUserId())){
                userVisibility.put("visibility","allowed");
            } else {
                int counter = activityRepository.findUserActivityVisibility(profileId, activityId);
                userVisibility.put("visibility", counter > 0 ? "allowed" : "not allowed");
            }
        }
        return userVisibility;
    }
}
