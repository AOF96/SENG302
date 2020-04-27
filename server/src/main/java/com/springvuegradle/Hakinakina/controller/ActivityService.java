package com.springvuegradle.Hakinakina.controller;

import com.springvuegradle.Hakinakina.entity.*;
import com.springvuegradle.Hakinakina.util.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
     * @param activity the activity the user wants to add
     * @param profileId the user's id
     * @param sessionToken the user's token from their current session
     * @return response entity to inform user if adding an activity was successful or not
     */
    public ResponseEntity addActivity(Activity activity, long profileId, String sessionToken) {
        Session session = sessionRepository.findUserIdByToken(sessionToken);
        if (session == null) {
            return new ResponseEntity("Invalid Session", HttpStatus.valueOf(400));
        }

        //TODO Loop through the activity types provided
//        for (ActivityType activityType : activity.getActivityTypes)

        activityRepository.save(activity);
        return new ResponseEntity("Activity Successfully Added", HttpStatus.valueOf(200));
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
        Session session = sessionRepository.findUserIdByToken(sessionToken);

        if (sessionToken == null) {
            result = responseHandler.formatErrorResponse(400, "Invalid data");

        } else if (activityRepository.findActivityById(activityId) == null) {
            result = responseHandler.formatErrorResponse(404, "Activity not found");

        } else if (profileId != session.getUser().getUserId()) {
            result = responseHandler.formatErrorResponse(403, "Invalid user");

        } else {
            activityRepository.deleteUser_ActivitiesValue(profileId ,activityId);
            activityRepository.deleteActivity_ActivityTypeValue(activityId);
            activityRepository.deleteActivityById(activityId);
            result = responseHandler.formatSuccessResponse(200, "Activity successfully deleted");
        }

        return result;
    }

}
