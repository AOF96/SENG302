package com.springvuegradle.hakinakina.service;

import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.repository.*;
import com.springvuegradle.hakinakina.util.ErrorHandler;
import com.springvuegradle.hakinakina.util.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;
import java.util.*;

@Service
public class ActivityService {

    public UserRepository userRepository;
    public ActivityRepository activityRepository;
    public ActivityTypeRepository activityTypeRepository;
    public PassportCountryRepository countryRepository;
    public SessionRepository sessionRepository;
    public ActivityChangeRepository activityChangeRepository;
    public AchievementRepository achievementRepository;
    public ResultRepository resultRepository;
    private ResponseHandler responseHandler = new ResponseHandler();

    public ActivityService(UserRepository userRepository,
                           ActivityRepository activityRepository,
                           ActivityTypeRepository activityTypeRepository,
                           PassportCountryRepository countryRepository,
                           SessionRepository sessionRepository,
                           AchievementRepository achievementRepository,
                           ActivityChangeRepository activityChangeRepository,
                           ResultRepository resultRepository) {
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.activityTypeRepository = activityTypeRepository;
        this.countryRepository = countryRepository;
        this.sessionRepository = sessionRepository;
        this.achievementRepository = achievementRepository;
        this.activityChangeRepository = activityChangeRepository;
        this.resultRepository = resultRepository;
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
            if(userRepository.getUserById(profileId).isEmpty()){
                return new ResponseEntity("Invalid User ID", HttpStatus.valueOf(403));
            }
            if (activity.getStartTime() != null) {
                activity.getStartTime().setTime((activity.getStartTime().getTime() - (12 * 60 * 60 * 1000)));
            }
            if (activity.getEndTime() != null) {
                activity.getEndTime().setTime((activity.getEndTime().getTime() - (12 * 60 * 60 * 1000)));
            }
            Session session = sessionRepository.findUserIdByToken(sessionToken);
            if (session == null) {
                return new ResponseEntity("Invalid Session", HttpStatus.valueOf(401));
            }
            if (profileId != session.getUser().getUserId() && session.getUser().getPermissionLevel() == 0) {
                return new ResponseEntity("Invalid User", HttpStatus.valueOf(403));
            }

            //If there are no activity types listed
            if (activity.getActivityTypes().isEmpty()) {
                return new ResponseEntity("Activity must have at least one activity type", HttpStatus.valueOf(400));
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

                Date date = new Date();
                Timestamp ts=new Timestamp(date.getTime());

                if (!activity.getStartTime().after(ts)) {
                    return new ResponseEntity("Activity start date and time must be in the future", HttpStatus.valueOf(400));
                }

                if (activity.getStartTime().after(activity.getEndTime())) {
                    return new ResponseEntity("Activity end date and time must be after the start date and time", HttpStatus.valueOf(400));
                }
            }

            //TODO Validate activity location in U9
            activity.setAuthor(userRepository.getUserById(profileId).get());
            activityRepository.save(activity);

            return new ResponseEntity(activity.getId(), HttpStatus.valueOf(201));
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "cannot add activity");
            return new ResponseEntity("An error occurred", HttpStatus.valueOf(500));
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
        System.out.println(activityChanges);
        StringBuilder description = new StringBuilder();
        for (ActivityAttribute attribute : activityChanges) {
            if (attribute == ActivityAttribute.NAME) {
                description.append("Activity name changed to: \"").append(newActivity.getName()).append("\"\n");
            } else if (attribute == ActivityAttribute.DESCRIPTION) {
                description.append("Activity description changed to: \"").append(newActivity.getDescription()).append("\"\n");
            } else if (attribute == ActivityAttribute.ACTIVITY_TYPES) {
                description.append("Activity activity types changed to: ").append(newActivity.getActivityTypes().toString()).append("\n");
            } else if (attribute == ActivityAttribute.CONTINUOUS) {
                description.append("Activity continuity changed to: ").append(newActivity.isContinuous()).append("\n");
            } else if (attribute == ActivityAttribute.START_TIME) {
                description.append("Activity starting time changed to: ").append(newActivity.getStartTime()).append("\n");
            } else if (attribute == ActivityAttribute.END_TIME) {
                description.append("Activity ending time changed to: ").append(newActivity.getEndTime()).append("\n");
            } else if (attribute == ActivityAttribute.LOCATION) {
                description.append("Activity Location changed to: \"").append(newActivity.getLocation()).append("\"\n");
            } else if (attribute == ActivityAttribute.USERS) {
                description.append("Activity users changed to: \"").append(newActivity.getUsers().toString()).append("\"\n");
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

    /**
     * Handles requests for adding achievements to an activity
     * @param achievement valid json containing achievement data
     * @param profileId id of the user that is adding the achievement
     * @param activityId id of the activity that the achievement is being added too
     * @param sessionToken users session token used for verification
     * @return response entity with status code dependent on the success or failure of the addition
     */
    public ResponseEntity addAchievement(Achievement achievement, long profileId, long activityId, String sessionToken) {
        ResponseEntity result;
        try {
            Session session = sessionRepository.findUserIdByToken(sessionToken);
            if (sessionToken == null) {
                result = responseHandler.formatErrorResponse(401, "Invalid Session");
            } else if ((profileId != session.getUser().getUserId()
                    || activityRepository.validateAuthor(profileId, activityId) == null)
                    && session.getUser().getPermissionLevel() == 0) {
                result = responseHandler.formatErrorResponse(403, "Invalid user");
            } else {
                Achievement achievementToAdd = new Achievement(achievement.getName(),
                                                               achievement.getDescription(),
                                                               achievement.getResultType());
                Activity activityToUpdate = activityRepository.getOne(activityId);
                activityToUpdate.addAchievement(achievementToAdd);
                activityRepository.save(activityToUpdate);
                achievementRepository.save(achievementToAdd);

                result = responseHandler.formatSuccessResponse(201, "Achievement added successfully");
            }
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "Cannot add achievement");
            result = responseHandler.formatErrorResponse(500, "An error occurred");
        }
        return result;
    }

    /**
     * Handles requests to edit achievements
     * @param newAchievement valid json containing new values for an existing achievement
     * @param profileId id of the user performing the edit
     * @param activityId id of the activity that the achievement being edited belongs too
     * @param achievementId id of the achievement that is being edited
     * @param sessionToken session token of the user which is used for validation checks
     * @return response entity with code dependant on success or failure of the request
     */
    public ResponseEntity editAchievement(Achievement newAchievement, long profileId, long activityId, long achievementId, String sessionToken) {
        try {
            Session session = sessionRepository.findUserIdByToken(sessionToken);
            if (session == null) {
                return new ResponseEntity("Invalid Session", HttpStatus.valueOf(401));
            }
            if ((profileId != session.getUser().getUserId() || activityRepository.validateAuthor(profileId, activityId) == null)
                            && session.getUser().getPermissionLevel() == 0) {
                return new ResponseEntity("Invalid User", HttpStatus.valueOf(403));
            }
            if (newAchievement.getName() == null) {
                return new ResponseEntity("Achievement name must be provided", HttpStatus.valueOf(400));
            }
            if (newAchievement.getDescription() == null) {
                return new ResponseEntity("Achievement description must be provided", HttpStatus.valueOf(400));
            }
            if (newAchievement.getResultType() == null) {
                return new ResponseEntity("Achievement result type must be provided", HttpStatus.valueOf(400));
            }

            Achievement achievement = achievementRepository.findAchievementById(achievementId);
            if (achievement == null) {
                return new ResponseEntity("Achievement couldn't be found", HttpStatus.valueOf(404));
            }

            achievement.setName(newAchievement.getName());
            achievement.setDescription(newAchievement.getDescription());
            achievement.setResultType(newAchievement.getResultType());

            achievementRepository.save(achievement);

            return new ResponseEntity("Achievement has been successfully updated", HttpStatus.valueOf(200));
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "Cannot edit achievement");
            return new ResponseEntity("An error occurred", HttpStatus.valueOf(500));
        }
    }

    /**
     * Handles requests to delete an achievement
     * @param profileId id of user attempting to delete achievement
     * @param activityId id of activity that has the achievement associated with it
     * @param achievementId id of the achievement that is to be deleted
     * @param sessionToken session token of the user which is used for validation checks
     * @return response entity with code dependant on success or failure of the request
     */
    public ResponseEntity deleteAchievement(long profileId, long activityId, long achievementId, String sessionToken) {
        ResponseEntity result;
        try {
            Session session = sessionRepository.findUserIdByToken(sessionToken);
            Achievement achievementToDelete = achievementRepository.findAchievementById(achievementId);

            if (sessionToken == null) {
                result = responseHandler.formatErrorResponse(401, "Invalid Session");

            } else if (achievementToDelete == null) {
                result = responseHandler.formatErrorResponse(404, "Achievement not found");

            } else if ((profileId != session.getUser().getUserId() || activityRepository.validateAuthor(profileId, activityId) == null) && session.getUser().getPermissionLevel() == 0) {
                result = responseHandler.formatErrorResponse(403, "Invalid user");

            } else {
                Activity activity = activityRepository.getOne(activityId);
                activity.removeAchievement(achievementToDelete);
                achievementRepository.delete(achievementToDelete);
                activityRepository.save(activity);
                result = responseHandler.formatSuccessResponse(200, "Achievement successfully deleted");
            }
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "Cannot delete achievement");
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
     * Adds a new result to the repository
     * @param result Result to add
     * @param profileId Id of user setting result
     * @param achievementId Id of achievement to add to
     * @return ResponseEntity containing result of the operation
     */
    public ResponseEntity<String> addResult(Result result, Long profileId, Long achievementId) {
        Achievement achievement = achievementRepository.findAchievementById(achievementId);
        User user = userRepository.findById(profileId).get();
        resultRepository.save(result);
        achievement.addResult(result);
        user.addResult(result);
        achievementRepository.save(achievement);
        userRepository.save(user);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }
}
