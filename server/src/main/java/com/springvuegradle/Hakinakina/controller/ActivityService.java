package com.springvuegradle.Hakinakina.controller;

import com.springvuegradle.Hakinakina.entity.*;
import com.springvuegradle.Hakinakina.util.ErrorHandler;
import com.springvuegradle.Hakinakina.util.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivityService {

    public UserRepository userRepository;
    public ActivityRepository activityRepository;
    public ActivityTypeRepository activityTypeRepository;
    public PassportCountryRepository countryRepository;
    public SessionRepository sessionRepository;
    private ResponseHandler responseHandler = new ResponseHandler();

    public ActivityService(UserRepository userRepository, ActivityRepository activityRepository, ActivityTypeRepository activityTypeRepository, PassportCountryRepository countryRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.activityTypeRepository = activityTypeRepository;
        this.countryRepository = countryRepository;
        this.sessionRepository = sessionRepository;
    }

    /**
     * Adds an activity for the user*
     *
     * @param activity     the activity the user wants to add
     * @param profileId    the user's id
     * @param sessionToken the user's token from their current session
     * @return response entity to inform user if adding an activity was successful or not
     */
    public ResponseEntity addActivity(Activity activity, long profileId, String sessionToken) {
        try {
            System.out.println(activity);
            Session session = sessionRepository.findUserIdByToken(sessionToken);
            if (session == null) {
                return new ResponseEntity("Invalid Session", HttpStatus.valueOf(400));
            }
            if (profileId != session.getUser().getUserId()) {
                return new ResponseEntity("Invalid User", HttpStatus.valueOf(403));
            }

            //If there are no activity types listed
            if (activity.getActivityTypes().size() == 0) {
                return new ResponseEntity("Activity must have atleast one activity type", HttpStatus.valueOf(400));
            }

            for (ActivityType activityType : activity.getActivityTypes()) {
                if (activityTypeRepository.findActivityTypeByName(activityType.getName()) == null) {
                    return new ResponseEntity("Selected activity type " + activityType.getName() + " does not exist", HttpStatus.valueOf(400));
                }
            }

            //If activity has a duration, start time and end time must be specified
            if (!activity.isContinuous()) {
                if (activity.getStartTime() == null || activity.getEndTime() == null) {
                    return new ResponseEntity("Activity with a duration must specify start time and end time", HttpStatus.valueOf(400));
                }
            }

            //TODO Validate activity location

            Activity newActivity = activityRepository.save(activity);
            activityRepository.insertActivityForUser(profileId, newActivity.getId());
            return new ResponseEntity("Activity has been created", HttpStatus.valueOf(201));
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
                result = responseHandler.formatErrorResponse(400, "Invalid data");

            } else if (activityToDelete == null) {
                result = responseHandler.formatErrorResponse(404, "Activity not found");

            } else if (profileId != session.getUser().getUserId() || activityRepository.validateAuthor(profileId, activityId) == null) {
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

}
