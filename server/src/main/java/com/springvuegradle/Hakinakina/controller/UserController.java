package com.springvuegradle.Hakinakina.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.Hakinakina.entity.*;
import com.springvuegradle.Hakinakina.util.ErrorHandler;
import com.springvuegradle.Hakinakina.util.ResponseHandler;
import com.springvuegradle.Hakinakina.util.RandomToken;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


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
    private ResponseHandler responseHandler = new ResponseHandler();

    private UserService userService;

    /**
     * Contructs a UserController, passing in the repositories so that they can be accessed.
     *
     * @param userRepository    The repository containing Users
     * @param countryRepository The repository containing PassportCountries
     * @param emailRepository   The repository containing Emails
     * @param sessionRepository The repository containing Sessions
     */
    public UserController(UserRepository userRepository, PassportCountryRepository countryRepository,
                          EmailRepository emailRepository, SessionRepository sessionRepository,
                          ActivityTypeRepository activityTypeRepository, UserService userService) {
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
        this.emailRepository = emailRepository;
        this.sessionRepository = sessionRepository;
        this.activityTypeRepository = activityTypeRepository;
        this.userService = userService;
    }

    /**
     * Processes create user request and puts user into repository if email is unique and all required fields have
     * been provided
     *
     * @param user
     * @return success or error message
     */
    @PostMapping("/profiles")
    public ResponseEntity createProfile(@RequestBody User user) {
        return userService.validateCreateProfile(user);
    }

    /**edits email
     *
     * PUT /profiles/{profileId}/emails
     * {
     *   "primary_email": "triplej@google.com",
     *   "additional_email": [
     *     "triplej@xtra.co.nz",
     *     "triplej@msn.com"
     *   ]
     * }
     *
     * @return*/
    @PutMapping("/profiles/{profileId}/emails")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> editEmail(@RequestBody String request, @PathVariable("profileId") long profileId, @RequestHeader("token") String sessionToken) {
        return userService.editEmail(request, profileId, sessionToken);
    }

    /***
     * Endpoint for handling the editing of a user's profile information. Returns appropriate error codes if something
     * is wrong with the request. Checks if the request comes from an admin so it can edit someone else profile.
     * @param user the JSON object containing the values the user wants to edit.
     * @param profileId the ID of the given user.
     * @param sessionToken authentication token to validate the user sending the request.
     * @return a 400 response if the provided ID and token don't match or is not an admin. 200 if it's a valid request.
     */
    @PutMapping("/profiles/{profileId}")
    public ResponseEntity editUser(@RequestBody User user, @PathVariable("profileId") long profileId, @RequestHeader("token") String sessionToken) {
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
            oldUser.resetPassportCountries();
            user.setUserId(profileId);
            user.setEncryptedPassword(oldUser.getPassword());
            user.setSalt(oldUser.getSalt());
            return userService.validateEditUser(user);
        } else {
            return responseHandler.formatErrorResponse(400, "Session mismatch");
        }
    }

    /** adds email
     * POST /profiles/{profileId}/emails
     * {
     *   "additional_email": [
     *     "triplej@xtra.co.nz",
     *     "triplej@msn.com"
     *     ]
     * }
     *
     *
     * @return*/
    @PostMapping("/profiles/{profileId}/emails")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity addEmails(@RequestBody String request, @PathVariable long profileId, @RequestHeader("token") String sessionToken) {
        return userService.addEmails(request, profileId, sessionToken);
    }

    /**
     * Processes get users request
     *
     * @return List of profiles
     */
    @GetMapping("/profiles")
    public ResponseEntity getAllUsers() {
        List<User> users = userRepository.findAll();
        return responseHandler.formatGetUsers(users);
    }


    /**
     * Validates user login by checking their sessionToken and returns user info
     *
     * @return User json object for user with matching sessionToken
     */
    @GetMapping("/validateLogin")
    public ResponseEntity validateLogin(@RequestHeader("token") String sessionToken) {
        Session session = sessionRepository.findUserIdByToken(sessionToken);
        if (session == null) {
            return responseHandler.formatErrorResponse(401, "User not currently logged in");
        }
        User user = session.getUser();
        return new ResponseEntity(user.toJson(), HttpStatus.valueOf(200));
    }

    /**
     * Processes request to retrieve certain user and returns
     *
     * @param profileId
     * @return Specific user
     */
    @GetMapping("/profiles/{profile_id}")
    public ResponseEntity getOneUser(@PathVariable("profile_id") long profileId, @RequestHeader("token") String sessionToken) {
        Session session = sessionRepository.findUserIdByToken(sessionToken);
        if (session == null) {
            return responseHandler.formatErrorResponse(400, "Invalid Session");
        }
        // only a user and an admin can edit the user profile
        boolean isAdmin = java.util.Objects.equals(session.getUser().getPermissionLevel().toString(), "1");
        boolean isDefaultAdmin = java.util.Objects.equals(session.getUser().getPermissionLevel().toString(), "2");
        if (isAdmin || isDefaultAdmin || session.getUser().getUserId() == profileId) {
            Optional<User> optional = userRepository.getUserById(profileId);
            if (optional.isPresent()) {
                User user = optional.get();
                return new ResponseEntity(user.toJson(), HttpStatus.valueOf(200));
            } else {
                return new ResponseEntity("User does not exist", HttpStatus.valueOf(404));
            }
        } else {
            return responseHandler.formatErrorResponse(400, "Session mismatch");
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

    /***
     * Endpoint for retrieving all the primary emails that are stored in the database.
     * @return a list with all the primary emails.
     */
    @GetMapping("/emails")
    public String getAllEmails() {
        //ToDO use the commented out return statement rather than the current one once the email table has been fixed
        /*
//        return emailRepository.getAllEmails();
         */
        return userRepository.getAllPrimaryEmails().toString();
    }

    /***
     * Retrieves the session token of an user. Based on the profile ID that is provided.
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
     * @return isLogin Whether the attempt was correct or not.
     */
    @PostMapping("/login")
    public ResponseEntity checkLogin(@RequestBody String jsonString) {
        Map<String, Object> json = new JacksonJsonParser().parseMap(jsonString);
        String attempt = (String) json.get("password");
        String email = (String) json.get("email");

        return userService.checkLogin(email, attempt);
    }

    /**
     * Logs out the current user and deletes the entry in the Session table
     *
     * @param sessionToken token stored in the cookie to identify the user
     * @return message and status to notify if log out was successful
     * */
    @PostMapping("/logout")
    public ResponseEntity checkLogout(@RequestHeader("token") String sessionToken) {
        try {
            sessionRepository.removeToken(sessionToken);
            return new ResponseEntity("User logged out", HttpStatus.OK);
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "couldn't log out");
            return new ResponseEntity("An error occurred", HttpStatus.FORBIDDEN);
        }
    }

    /**
     *  Processes to edit user password
     *
     * @param sessionToken token stored in the cookie to identify the user
     * */
    @PutMapping("/profiles/{profileId}/password")
    public ResponseEntity editPassword(@RequestBody String jsonString, @PathVariable Long profileId, @RequestHeader("token") String sessionToken) {
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
     * @param jsonString The json as a string
     * @param profileId The ID of the user to update
     * @return A ResponseEntity stating the result
     * @throws JsonProcessingException If there is an issue while parsing the JSON
     */
    @PutMapping("/profiles/{profileId}/activity-types")
    public ResponseEntity editActivityTypes(@RequestBody String jsonString, @PathVariable Long profileId) throws JsonProcessingException {
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
     * Parses a list of activity types
     * @param activitiesNode A JsonNode of the activities key extracted from the JSON
     * @return A List of Strings of the activity types
     */
    public static List<String> parseActivityList(JsonNode activitiesNode) {
        List<String> activities = new ArrayList<>();
        for (JsonNode activity : activitiesNode) {
            activities.add(activity.textValue());
        }

        return activities;
    }

    // Create Exception Handle
    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Request ID not found.")
    @ExceptionHandler(IllegalArgumentException.class)
    public void badIdExceptionHandler() {
        //Nothing to do
    }
}
