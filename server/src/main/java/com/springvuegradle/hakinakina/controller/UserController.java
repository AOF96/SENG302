package com.springvuegradle.hakinakina.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.hakinakina.dto.EditActivityRoleDto;
import com.springvuegradle.hakinakina.entity.ActivityType;
import com.springvuegradle.hakinakina.entity.PassportCountry;
import com.springvuegradle.hakinakina.entity.Session;
import com.springvuegradle.hakinakina.entity.User;
import com.springvuegradle.hakinakina.repository.*;
import com.springvuegradle.hakinakina.service.UserService;
import com.springvuegradle.hakinakina.util.ErrorHandler;
import com.springvuegradle.hakinakina.util.ResponseHandler;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;


/**
 * Rest controller class for controlling requests about Users
 */
@RestController
public class UserController {

    public UserRepository userRepository;
    public PassportCountryRepository countryRepository;
    public EmailRepository emailRepository;
    public SessionRepository sessionRepository;
    public ActivityTypeRepository activityTypeRepository;
    private UserService userService;

    private ResponseHandler responseHandler = new ResponseHandler();


    /**
     * Constructs a UserController, passing in the repositories and service so that they can be accessed.
     *
     * @param userRepository    The repository containing Users
     * @param countryRepository The repository containing PassportCountries
     * @param emailRepository   The repository containing Emails
     * @param sessionRepository The repository containing Sessions
     * @param userService       The service for Users
     */
    public UserController(UserRepository userRepository,
                          PassportCountryRepository countryRepository,
                          EmailRepository emailRepository,
                          SessionRepository sessionRepository,
                          ActivityTypeRepository activityTypeRepository,
                          UserService userService) {
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
        this.emailRepository = emailRepository;
        this.sessionRepository = sessionRepository;
        this.activityTypeRepository = activityTypeRepository;
        this.userService = userService;
    }

    /**
     * Parses a list of activity types
     *
     * @param activitiesNode A JsonNode of the activities key extracted from the JSON
     * @return A JSON list of strings of the activity types
     */
    public static List<String> parseActivityList(JsonNode activitiesNode) {
        List<String> activities = new ArrayList<>();
        for (JsonNode activity : activitiesNode) {
            activities.add(activity.textValue());
        }

        return activities;
    }

    /**
     * Processes create user request and puts user into repository if email is unique and all required fields have
     * been provided
     *
     * @param user The user received from the request
     * @return response entity to inform user if registering a new account was successful or not
     */
    @PostMapping("/profiles")
    public ResponseEntity createProfile(@RequestBody User user,
                                        HttpServletResponse response) {
        return userService.validateCreateProfile(user, response);
    }

    /**
     * Handles requests for editing a user's email
     *
     * @param request      the request that contains the updated emails
     * @param profileId    the user's id
     * @param sessionToken the user's token from the cookie for their current session.
     * @return response entity to inform user if editing their email was successful or not
     */
    @PutMapping("/profiles/{profileId}/emails")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> editEmail(@RequestBody String request,
                                            @PathVariable("profileId") long profileId,
                                            @CookieValue(value = "s_id") String sessionToken) {
        return userService.editEmail(request, profileId, sessionToken);
    }

    /**
     * Endpoint for handling the editing of a user's profile information. Returns appropriate error codes if something
     * is wrong with the request. Checks if the request comes from an admin so it can edit someone else profile.
     *
     * @param user         the JSON object containing the values the user wants to edit.
     * @param profileId    the user's id
     * @param sessionToken the user's token from the cookie for their current session.
     * @return ResponseEntity with a 400 response if the provided ID and token don't match or is not an admin or
     * 200 if it's a valid request.
     */
    @PutMapping("/profiles/{profileId}")
    public ResponseEntity editUser(@RequestBody User user,
                                   @PathVariable("profileId") long profileId,
                                   @CookieValue(value = "s_id") String sessionToken) {
        Session session = sessionRepository.findUserIdByToken(sessionToken);
        if (session == null) {
            return responseHandler.formatErrorResponse(400, "Invalid Session");
        }

        // only a user and an admin can edit the user profile
        boolean isAdmin = java.util.Objects.equals(session.getUser().getPermissionLevel().toString(), "1");
        boolean isDefaultAdmin = java.util.Objects.equals(session.getUser().getPermissionLevel().toString(), "2");
        if (isAdmin || isDefaultAdmin || session.getUser().getUserId() == profileId) {
            User oldUser = userRepository.findById(profileId).get();
            for (PassportCountry country : oldUser.getPassportCountries()) {
                country.removeUser(oldUser);
            }

            // only the default admin can edit permission level
            boolean isEditPermission = !oldUser.getPermissionLevel().equals(user.getPermissionLevel());
            if (isEditPermission && !isDefaultAdmin) {
                return responseHandler.formatErrorResponse(401, "Unauthorized: Only the default admin can edit the user permission level");
            }

            oldUser.resetPassportCountries();
            user.setUserId(profileId);
            //System.out.println(oldUser.getStuff());
            //user.setAuthoredActivities(oldUser.getAuthoredActivities());
            user.setEncryptedPassword(oldUser.getPassword());
            user.setSalt(oldUser.getSalt());
            return userService.validateEditUser(user);
        } else {
            return responseHandler.formatErrorResponse(400, "Session mismatch");
        }
    }


    /**
     * Handles requests for adding emails to a profile
     *
     * @param request
     * @param profileId    the user's id
     * @param sessionToken the user's token from the cookie for their current session.
     * @return response entity to inform user if adding a new email was successful or not
     */
    @PostMapping("/profiles/{profileId}/emails")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity addEmails(@RequestBody String request,
                                    @PathVariable long profileId,
                                    @CookieValue(value = "s_id") String sessionToken) {
        return userService.addEmails(request, profileId, sessionToken);
    }

    /**
     * Validates user login by checking their sessionToken and returns user info
     *
     * @param sessionToken the user's token from the cookie for their current session.
     * @return response entity containing a user json object for user with the matching sessionToken
     */
    @GetMapping("/validateLogin")
    public ResponseEntity validateLogin(@CookieValue(value = "s_id") String sessionToken) {
        Session session = sessionRepository.findUserIdByToken(sessionToken);
        if (session == null) {
            return responseHandler.formatErrorResponse(401, "User not currently logged in");
        }
        User user = session.getUser();
        return new ResponseEntity(user.toJson(), HttpStatus.valueOf(200));
    }

    /**
     * Retrieves searched user id by their email
     * @param sessionToken  the user's token from the cookie for their current session.
     * @param email searched user email
     * @return searched user id
     */
    @GetMapping("/email/id/")
    public ResponseEntity getUserByEmail(@CookieValue(value = "s_id") String sessionToken,
                                         @RequestParam String email) {
        Session session = sessionRepository.findUserIdByToken(sessionToken);
        if (session == null) {
            return responseHandler.formatErrorResponse(401, "User not currently logged in");
        }
        Long userId = userRepository.getIdByEmail(email);
        return new ResponseEntity("{\"id\": \"" + userId + "\"}", HttpStatus.valueOf(200));
    }


    /**
     * Handles requests for retrieving a user
     *
     * @param profileId the user's id to be retrieved
     * @return response entity containing the specific user
     * @throws 404 error if the user with given id doesn't exist or if the given id is that of the default admin
     */
    @GetMapping("/profiles/{profile_id}")
    public ResponseEntity getOneUser(@PathVariable("profile_id") long profileId) {
        Optional<User> optional = userRepository.getUserById(profileId);
        if (optional.isPresent() && optional.get().getPermissionLevel() != 2) {
            User user = optional.get();
            return new ResponseEntity(user.toJson(), HttpStatus.valueOf(200));
        } else {
            return new ResponseEntity("User does not exist", HttpStatus.valueOf(404));
        }
    }

    /**
     * Returns a list of all countries in the database
     *
     * @return List of countries
     */
    @GetMapping("/countries")
    public String getAllCountries() {
        return countryRepository.findAll().toString();
    }

    /**
     * Handles requests for retrieving emails
     *
     * @return a list with all the primary emails.
     */
    @GetMapping("/emails")
    public String getAllEmails() {
        //ToDO use the commented out return statement rather than the current one once the email table has been fixed
        //return emailRepository.getAllEmails();

        return userRepository.getAllPrimaryEmails().toString();
    }

    /**
     * Retrieves the session token of an user. Based on the profile ID that is provided.
     *
     * @param profileId the user's ID
     * @return a list containing all the tokens that belong to the given user.
     */
    @GetMapping("/token/{profile_id}")
    public List<String> getUserSessionToken(@PathVariable("profile_id") long profileId) {
        return sessionRepository.getUserSessionToken(profileId);
    }

    /**
     * Check if the user's login credentials are correct. First finds a user with the same email. Then checks if the
     * entered password matches the actual password. This is done by encrypting the attempt using the same salt as the
     * actual password, then checking for equality.
     *
     * @param jsonString The JSON body passed as a string.
     * @param response   the response servlet
     * @return response entity to inform user if credentials for logging in was successful or not
     */
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity checkLogin(@RequestBody String jsonString,
                                     HttpServletResponse response) {
        Map<String, Object> json = new JacksonJsonParser().parseMap(jsonString);
        String attempt = (String) json.get("password");
        String email = (String) json.get("email");

        return userService.checkLogin(email, attempt, response);
    }

    /**
     * Logs out the current user and deletes the entry in the Session table
     *
     * @param sessionToken token stored in the cookie to identify the user
     * @param response     the response servlet
     * @return response entity to inform user if logging out was successful or not
     */
    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity checkLogout(@CookieValue(value = "s_id") String sessionToken,
                                      HttpServletResponse response) {
        try {
            sessionRepository.removeToken(sessionToken);

            // create a cookie
            Cookie cookie = new Cookie("s_id", null);
            cookie.setMaxAge(0);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            //add cookie to response
            response.addCookie(cookie);

            return new ResponseEntity("User logged out", HttpStatus.OK);
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "couldn't log out");
            return new ResponseEntity("An error occurred", HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Handles request to edit user password
     *
     * @param jsonString   the json as string. Request contains the updated passwords
     * @param profileId    the user's id
     * @param sessionToken the user's token from the cookie for their current session.
     * @return response entity to inform user if editing a password was successful or not
     */
    @PutMapping("/profiles/{profileId}/password")
    public ResponseEntity editPassword(@RequestBody String jsonString,
                                       @PathVariable Long profileId,
                                       @CookieValue(value = "s_id") String sessionToken) {
        Map<String, Object> json = new JacksonJsonParser().parseMap(jsonString);
        String oldPassword = (String) json.get("old_password");
        String newPassword = (String) json.get("new_password");
        String repeatPassword = (String) json.get("repeat_password");

        if (!newPassword.equals(repeatPassword)) {
            return responseHandler.formatErrorResponse(400, "newPassword and repeatPassword do no match");
        }
        return userService.changePassword(profileId, sessionToken, oldPassword, newPassword);
    }

    /**
     * Handles the editing of a user's activity types. Parses the list of activities and calls the service method,
     * returning the result as a ResponseEntity
     *
     * @param jsonString The json as a string. Request contains activity types
     * @param profileId  the user's id that needs updating
     * @return A ResponseEntity stating the result
     * @throws JsonProcessingException If there is an issue while parsing the JSON
     */
    @PutMapping("/profiles/{profileId}/activity-types")
    public ResponseEntity editActivityTypes(@RequestBody String jsonString,
                                            @PathVariable Long profileId) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode activitiesNode = mapper.readTree(jsonString).get("activities");
        List<String> activities;

        if (activitiesNode.isArray()) {
            activities = parseActivityList(activitiesNode);
        } else {
            return new ResponseEntity("Must send a list of activities", HttpStatus.valueOf(400));
        }

        return userService.editActivityTypes(activities, profileId);
    }

    /**
     * Retrieve the names of all the Activity Types in the database
     *
     * @return A JSON list of names of activity types
     */
    @GetMapping("/activity-types")
    public List<String> getActivityTypes() {
        List<ActivityType> activityTypes = activityTypeRepository.findAll();
        List<String> activityTypeStrings = new ArrayList<>();

        for (ActivityType type : activityTypes) {
            activityTypeStrings.add(type.getName());
        }
        return activityTypeStrings;
    }

    /**
     * Handles the updating of user location with city, state and country.
     * @param jsonString JSON request as string
     * @param profileId id of user to update
     * @return ResponseEntity of the result
     * @throws JsonProcessingException If there is an issue parsing JSON
     */
    @PutMapping("/profiles/{profileId}/location")
    public ResponseEntity editLocation(@RequestBody String jsonString,
                                       @PathVariable Long profileId)
            throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode locationNode = mapper.readTree(jsonString).get("location");
        String city = locationNode.get("city").asText();
        String state = locationNode.get("state").asText();
        String country = locationNode.get("country").asText();

        return userService.editLocation(city, state, country, profileId);
    }

    /**
     * Allows the user to delete their account and admins to delete another registered user's account
     * @param profileId    the user's id
     * @param sessionToken the user's token from the cookie for their current session.
     * @return response entity to inform user or admin if deleting the user was successful or not
     */
    @DeleteMapping("/profiles/{profileId}")
    public ResponseEntity deleteUser(@PathVariable Long profileId,
                                     @CookieValue(value = "s_id") String sessionToken,
                                     HttpServletResponse response) {
        return userService.deleteUser(profileId, sessionToken, response);
    }

    // Create Exception Handle
    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Request ID not found.")
    @ExceptionHandler(IllegalArgumentException.class)
    public void badIdExceptionHandler() {
        //Nothing to do
    }

    /***
     * Endpoint to give an user admin rights. Calls userService to perform authentication and grant admin rights.
     * @param jsonString the request body.
     * @param profileId the id of the user being promoted to admin.
     * @param sessionToken the authentication token of the admin performing the request.
     * @return the response status that specifies if the operation was successful or not.
     */
    @PutMapping("/profiles/{profileId}/role")
    public ResponseEntity promoteUser(@RequestBody String jsonString,
                                      @PathVariable Long profileId, @CookieValue(value = "s_id", required = false) String sessionToken) {
        return userService.promoteUser(jsonString, profileId, sessionToken);
    }

    /***
     * Endpoint to edit role of an user in an activity, if role does not exist, create new role and save
     * @param profileId id of person who is changing the role of some user
     * @param sessionToken the user's token from the cookie for their current session.
     * @param activityId id of the activity the user is changing the role of
     * @param dto DTO of subscriber request which contains email of user changing the role and what role to change to
     * @return the response status that specifies if the operation was successful or not.
     */
    @PutMapping("/profiles/{profileId}/activities/{activityId}/subscriber")
    public ResponseEntity<Void> editUserActivityRole(@PathVariable Long profileId,
                                                     @CookieValue(value = "s_id") String sessionToken,
                                                     @PathVariable Long activityId,
                                               @Valid @RequestBody EditActivityRoleDto dto){


        userService.editUserActivityRole(profileId, activityId, dto, sessionToken);
        return ResponseEntity.ok().build();
    }
}
