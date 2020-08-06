package com.springvuegradle.hakinakina.service;

import com.springvuegradle.hakinakina.dto.ActivityVisibilityDto;
import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.repository.*;
import com.springvuegradle.hakinakina.util.ErrorHandler;
import com.springvuegradle.hakinakina.util.ResponseHandler;
import net.minidev.json.JSONArray;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;
import java.util.*;

import static java.lang.Long.parseLong;

@Service
public class ActivityService {

    public UserRepository userRepository;
    public ActivityRepository activityRepository;
    public ActivityTypeRepository activityTypeRepository;
    public PassportCountryRepository countryRepository;
    public SessionRepository sessionRepository;
    public SearchRepository searchRepository;
    private ResponseHandler responseHandler = new ResponseHandler();

    public ActivityService(UserRepository userRepository,
                           ActivityRepository activityRepository,
                           ActivityTypeRepository activityTypeRepository,
                           PassportCountryRepository countryRepository,
                           SessionRepository sessionRepository,
                           SearchRepository searchRepository) {
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.activityTypeRepository = activityTypeRepository;
        this.countryRepository = countryRepository;
        this.sessionRepository = sessionRepository;
        this.searchRepository = searchRepository;
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
            if(userRepository.getUserById(profileId).isEmpty()){
                return new ResponseEntity<>("Invalid User ID", HttpStatus.valueOf(403));
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
                Timestamp ts=new Timestamp(date.getTime());

                if (!activity.getStartTime().after(ts)) {
                    return new ResponseEntity<>("Activity start date and time must be in the future", HttpStatus.valueOf(400));
                }

                if (activity.getStartTime().after(activity.getEndTime())) {
                    return new ResponseEntity<>("Activity end date and time must be after the start date and time", HttpStatus.valueOf(400));
                }
            }

            //TODO Validate activity location in U9
            activity.setAuthor(userRepository.getUserById(profileId).get());
            activityRepository.save(activity);

            return new ResponseEntity<>("Activity has been created", HttpStatus.valueOf(201));
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "cannot add activity");
            return new ResponseEntity<>("An error occurred", HttpStatus.valueOf(500));
        }
    }

    /**
     * Edits an activity for the user
     *
     * @param newActivity     the activity the user wants to add
     * @param profileId    the user's id
     * @param sessionToken the user's token from their current session
     * @return response entity to inform user if adding an activity was successful or not
     */
    public ResponseEntity editActivity(Activity newActivity, long profileId, long activityId, String sessionToken) {
        try {
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
                Timestamp ts=new Timestamp(date.getTime());

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
            ErrorHandler.printProgramException(e, "cannot add activity");
            return new ResponseEntity("An error occurred", HttpStatus.valueOf(500));
        }
    }

    /***
     * Removes an activity from the database if the user is authenticated.
     * @param profileId the user's profile id.
     * @param activityId the activity id to be removed.
     * @param sessionToken the user's token from their current session.
     * @return response entity with the result of the operation.
     */
    public ResponseEntity removeActivity(long profileId, long activityId, String sessionToken) {

        ResponseEntity result;
        try {
            Session session = sessionRepository.findUserIdByToken(sessionToken);
            Activity activityToDelete = activityRepository.findActivityById(activityId);

            if (sessionToken == null) {
                result = responseHandler.formatErrorResponse(401, "Invalid Session");

            } else if (activityToDelete == null) {
                result = responseHandler.formatErrorResponse(404, "Activity not found");

            } else if ((profileId != session.getUser().getUserId() || activityRepository.validateAuthor(profileId, activityId) == null) && session.getUser().getPermissionLevel() == 0) {
                result = responseHandler.formatErrorResponse(403, "Invalid user");

            } else {
                activityRepository.deleteActivityForUser(profileId, activityId);
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
     *
     * @param profileId  the logged in user's id.
     * @param activityId the activity id of the activity being managed
     * @param sessionToken the user's token from the cookie for their current session.
     * @param request level of visibility and the set of user's email allowed to access the activity
     * @return
     */
    public ResponseEntity<String> updateActivityVisibility (Long profileId,
                                                            Long activityId,
                                                            String sessionToken,
                                                            ActivityVisibilityDto request) {


        Activity activity = activityRepository.findActivityById(activityId);
        activity.setVisibility(request.getVisibility());

        Set<User> accessors = new HashSet<User>();

        if(request.getVisibility().equals(Visibility.RESTRICTED)){
            for (String email : request.getAccessorsEmails()) {
                String userId = userRepository.getIdByEmail(email);
                Optional<User> optionalUser = userRepository.findById(parseLong(userId));
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    accessors.add(user);
                }
            }
            activity.setUsersShared(accessors);
        }
        else if(request.getVisibility().equals(Visibility.PRIVATE)){
            activity.setUsersShared(accessors);
        }
        activityRepository.save(activity);
        return new ResponseEntity<String>("Activity Visibility Status Updated", HttpStatus.OK);
    }

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
     * Retrieve shared users of an activity
     * @param activityId the activity id to use in the request.
     * @return response entity with the result of the operation.
     */
    public ResponseEntity getSharedUsers(Long activityId, int page, int size) {
        ResponseEntity result;
        try {
            System.out.println(activityId);
            if (activityId == null || activityRepository.findActivityById(activityId) == null) {
                result = responseHandler.formatErrorResponse(404, "Activity not found");
            } else {
                Page<Object> users = searchRepository.getSharedUsers(PageRequest.of(page, size), activityId);
                result = new ResponseEntity(users, HttpStatus.OK);
            }
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "Could not retrieve shared users");
            result = responseHandler.formatErrorResponse(500, "An error occurred");
        }

        return result;
    }

}
