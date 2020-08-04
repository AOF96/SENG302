package com.springvuegradle.hakinakina.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.hakinakina.dto.SearchUserDto;
import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.repository.*;
import com.springvuegradle.hakinakina.specification.UserSpecification;
import com.springvuegradle.hakinakina.util.EncryptionUtil;
import com.springvuegradle.hakinakina.util.ErrorHandler;
import com.springvuegradle.hakinakina.util.RandomToken;
import com.springvuegradle.hakinakina.util.ResponseHandler;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

/**
 * Class for user profile request actions
 */
@Service
public class UserService {

    private UserRepository userRepository;
    public ActivityRepository activityRepository;
    private EmailRepository emailRepository;
    private PassportCountryRepository countryRepository;
    private SessionRepository sessionRepository;
    private ActivityTypeRepository activityTypeRepository;
    private SearchRepository searchRepository;
    private ResponseHandler responseHandler = new ResponseHandler();

    public UserService(UserRepository userRepository,
                       EmailRepository emailRepository,
                       PassportCountryRepository countryRepository,
                       SessionRepository sessionRepository,
                       ActivityTypeRepository activityTypeRepository,
                       ActivityRepository activityRepository,
                       SearchRepository searchRepository) {
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.emailRepository = emailRepository;
        this.countryRepository = countryRepository;
        this.sessionRepository = sessionRepository;
        this.activityTypeRepository = activityTypeRepository;
        this.searchRepository = searchRepository;
    }

    /**
     * Checks whether an email has the correct format
     *
     * @param email Email String in question
     * @return True if correct, False otherwise
     */
    public boolean isEmailProperlyFormatted(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    /**
     * Checks whether an email exists by checking the repository and whether a user exists with that email as their primary
     *
     * @param email A string email to search for
     * @return
     */
    public boolean emailExists(String email) {
        return !(emailRepository.findEmailByString(email) == null && userRepository.findUserByEmail(email) == null);
    }

    /**
     * Takes an edit email request and updates the repositories appropriately
     *
     * @param request
     * @return reply to client
     * <p>
     * edits email
     * <p>
     * PUT /profiles/{profileId}/emails
     * {
     * "primary_email": "triplej@google.com",
     * "additional_email": [
     * "triplej@xtra.co.nz",
     * "triplej@msn.com"
     * ]
     * }
     */
    public ResponseEntity<String> editEmail(String request, long profileId, String sessionToken) {
        ResponseEntity<String> response = null;

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = objectMapper.readValue(request, JsonNode.class);
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "Error parsing email edit request");
            response = responseHandler.formatErrorResponse(400, "Incorrect request format");
        }

        //long userId = node.get("profile_id").asLong();
        User user = userRepository.findById(profileId).get();

        String primaryEmail = node.get("primary_email").asText();
        List<JsonNode> secondaryEmailNodes = node.findValues("additional_email");

        ArrayList<String> secondaryEmails = new ArrayList<>();
        for (JsonNode node1 : secondaryEmailNodes.get(0)) {
            secondaryEmails.add(node1.textValue());
        }

        String currentPrimary = user.getPrimaryEmail();

        if (!primaryEmail.equals(currentPrimary) && response == null) {
            response = switchPrimaryEmail(user, primaryEmail, currentPrimary, secondaryEmails);
        } else if (response == null) {
            response = updateSecondaryEmails(user, secondaryEmails);
        }

        return response;
    }

    /**
     * Switches primary email with secondary email
     *
     * @param user
     * @param newPrimary
     * @param currentPrimary
     * @param secondaryEmails
     */
    public ResponseEntity<String> switchPrimaryEmail(User user, String newPrimary, String currentPrimary, ArrayList<String> secondaryEmails) {
        if (secondaryEmails.contains(user.getPrimaryEmail()) && emailRepository.findEmailByString(newPrimary).getUser().getUserId() == user.getUserId()) {
            user.setPrimaryEmail(newPrimary);
            user.removeEmail(emailRepository.findEmailByString(newPrimary));
            emailRepository.delete(emailRepository.findEmailByString(newPrimary));

            Email currentPrimaryEmail = new Email(currentPrimary);
            emailRepository.save(currentPrimaryEmail);
            user.addEmail(currentPrimaryEmail);
            userRepository.save(user);

            return responseHandler.formatSuccessResponse(200, "Primary email switched successfully");
        } else {
            return responseHandler.formatSuccessResponse(400, "Could not switch email");
        }
    }

    /**
     * Updates the users secondary emails to match the new set
     *
     * @param user
     * @param secondaryEmails
     */
    public ResponseEntity updateSecondaryEmails(User user, ArrayList<String> secondaryEmails) {
        Set<Email> emailsToRemove = new HashSet<>();
        for (Email currentEmail : user.getEmails()) {
            if (!secondaryEmails.contains(currentEmail.getEmail())) {
                emailsToRemove.add(currentEmail);
            }
        }

        String emailsRemoved = "";
        for (Email emailToRemove : emailsToRemove) {
            user.removeEmail(emailToRemove);
            emailsRemoved += emailToRemove.getEmail() + ", ";
            emailRepository.delete(emailToRemove);
        }

        String emailsAdded = "";
        for (String newEmail : secondaryEmails) {
            if (emailRepository.findEmailByString(newEmail) == null && !newEmail.equals("")) {
                Email emailToAdd = new Email(newEmail);
                emailRepository.save(emailToAdd);
                user.addEmail(emailToAdd);
                emailsAdded += newEmail + ", ";
            }
        }

        userRepository.save(user);

        if (emailsAdded.equals("")) {
            if (emailsRemoved.equals("")) {
                return responseHandler.formatErrorResponse(400, "No emails added.");
            } else {
                return responseHandler.formatSuccessResponse(200, "Secondary emails successfully removed: " + emailsRemoved);
            }
        } else {
            if (emailsRemoved.equals("")) {
                return responseHandler.formatSuccessResponse(200, "Secondary emails successfully added: " + emailsAdded);
            } else {
                return responseHandler.formatSuccessResponse(200, "Secondary emails added: " + emailsAdded + " - Secondary emails removed: " + emailsRemoved);
            }
        }
    }

    /***
     * Handles adding emails for the given user. Returns appropriate error messages if the request format is invalid or
     * if the provided emails are invalid. Saves the given emails if the request is correct.
     * @param request a JSON object containing the emails to be processed.
     * @param userId the ID of the user that wants to add emails.
     * @param sessionToken the authorization token of user adding emails.
     * @return 400 response if the JSON format is incorrect or if the user exceeded the allowed number of additional
     * emails. 403 if an existing email is tried to be added again. 201 if the request succeeded.
     */
    public ResponseEntity addEmails(String request, long userId, String sessionToken) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = null;

        try {
            node = objectMapper.readValue(request, JsonNode.class);
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "Error parsing add email request");
            return responseHandler.formatErrorResponse(400, "Incorrect request format");
        }

        User user = userRepository.findById(userId).get();
        int currentNumEmails = user.getEmails().size();
        List<JsonNode> secondaryEmailNodes = node.findValues("additional_email");

        ArrayList<String> secondaryEmails = new ArrayList<>();
        for (JsonNode node1 : secondaryEmailNodes.get(0)) {
            if (emailRepository.findEmailByString(node1.asText()) != null) {
                return responseHandler.formatErrorResponse(403, "A user already has that email");
            }
            secondaryEmails.add(node1.asText());
        }

        if (currentNumEmails + secondaryEmails.size() > 4) {
            return responseHandler.formatErrorResponse(400, "Cannot add more than 4 secondary emails");
        } else {
            for (String email : secondaryEmails) {
                Email emailToAdd = new Email(email);
                emailRepository.save(emailToAdd);
                user.addEmail(emailToAdd);
            }
        }
        userRepository.save(user);
        return responseHandler.formatErrorResponse(201, "New emails successfully added");
    }

    /***
     * Validates the data provided when creating a new profile. Provides error messages if any of the required inputs are
     * invalid or the given email already exits in the database.
     * @param user the user to be created.
     * @return An error response 400 if the provided data is invalid. 403 response if the given email already exists.
     * 201 created response if the request was succesful, with the JSON object of the created user and a session token.
     */
    public ResponseEntity validateCreateProfile(User user,
                                                HttpServletResponse response) {
        ArrayList<String> messages = new ArrayList<String>();

        if (user.getLastName() == null || user.getFirstName() == null) {
            messages.add("Please provide your full name. First and last names are required.");
        } else if (user.getLastName().isBlank() || user.getFirstName().isBlank()) {
            messages.add("Please provide your full name. First and last names are required.");
        }
        if (user.getPrimaryEmail() == null) {
            messages.add("Please provide a valid email.");
        } else if (user.getPrimaryEmail().isBlank()) {
            messages.add("Please provide a valid email.");
        } else if (isEmailProperlyFormatted(user.getPrimaryEmail()) != true) {
            messages.add("Please provide a valid email.");
        }
        if (user.getBirthDate() == null) {
            messages.add("Please provide a valid date of birth, yyyy-mm-dd.");
        }
        if (user.getGender() == null) {
            messages.add("Please provide a valid gender. male, female or non-binary.");
        }
        if (user.getFitnessLevel() < 0 || user.getFitnessLevel() > 4) {
            messages.add("Please select the fitness level in the range 0 and 5");
        }
        if (user.getBirthDate().after(new Date())) {
            messages.add("Birth date must be in the past");
        }

        if (user.getPassword() == null) {
            messages.add("Password cannot be empty");
        }

        if (messages.isEmpty()) {
            if (emailExists(user.getPrimaryEmail())) {
                return responseHandler.formatErrorResponse(403, "Email already exists");
            } else {
                //Generate session token
                String sessionToken = RandomToken.getToken(40);
                Session session = new Session(sessionToken);
                user.addSession(session);
                userRepository.save(user);

                // create a cookie
                Cookie cookie = new Cookie("s_id", sessionToken);
                // expires in 7 days
                cookie.setMaxAge(7 * 24 * 60 * 60);
                cookie.setHttpOnly(true);
                // add cookie to response
                response.addCookie(cookie);

                System.out.println(cookie.getMaxAge());


                return new ResponseEntity("[" + user.toJson() + ", {\"sessionToken\": \"" + sessionToken + "\"}]", HttpStatus.valueOf(201));
            }
        } else {
            return responseHandler.formatErrorResponse(400, messages);
        }
    }

    /***
     * Checks that all the information provided to edit a user is valid. Provides detailed information to the user if
     * any of the input fields contain invalid data. If all the inputs are valid, updated the details on the database.
     * @param user the user to be edited.
     * @return a 403 error response if any of the inputs are incorrect. A 200 response if all the details are correct.
     */
    public ResponseEntity validateEditUser(User user) {
        ArrayList<String> messages = new ArrayList<String>();

        if (user.getLastName() == null || user.getFirstName() == null) {
            messages.add("Please provide your full name. First and last names are required.");
        } else if (user.getLastName().isBlank() || user.getFirstName().isBlank()) {
            messages.add("Please provide your full name. First and last names are required.");
        }
        if (user.getPrimaryEmail() == null) {
            messages.add("Please provide a valid email.");
        } else if (user.getPrimaryEmail().isBlank()) {
            messages.add("Please provide a valid email.");
        }
        if (user.getBirthDate() == null) {
            messages.add("You cannot delete required fields. Please provide a valid date of birth, yyyy-mm-dd.");
        }
        if (user.getGender() == null) {
            messages.add("You cannot delete required fields. Please provide a valid gender. male, female or non-binary.");
        }
        if (user.getFitnessLevel() < 0 || user.getFitnessLevel() > 4) {
            messages.add("You cannot delete the required filed. Please select the fitness level in the range 0 and 5");
        }
        if (!checkAge(user.getBirthDate(), LocalDate.now())) {
            messages.add("Birth date must be in the past and age must be between 13 and 140");
        }
        if (!messages.isEmpty()) {
            return responseHandler.formatErrorResponse(403, messages);
        } else {
            userRepository.save(user);
            return responseHandler.formatSuccessResponse(200, "User updated");
        }
    }

    /**
     * Checks that someone is between 13 and 140 years of age inclusive
     * @param birthDate The user's date of birth
     * @param currentDate Today's date
     * @return A boolean of whether the user is of a valid age
     */
    public static boolean checkAge(Date birthDate, LocalDate currentDate) {
        boolean result = true;
        int age = calculateAge(birthDate, currentDate);
        if (birthDate.after(new Date()) || age < 13 || age > 140) {
            result = false;
        }
        return result;
    }

    /**
     * Calculates someone's age. Most of the code taken from
     * https://stackoverflow.com/questions/1116123/how-do-i-calculate-someones-age-in-java
     * @param date The person's birthdate
     * @return Their age as an int
     */
    public static int calculateAge(Date date, LocalDate currentDate) {
        LocalDate birthDate = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        if (currentDate != null) {
            return Period.between(birthDate, currentDate).getYears();
        } else {
            return 0;
        }
    }

    public ResponseEntity getAllEmails() {
        return responseHandler.formatSuccessResponse(200, "Emails found");
    }

    /**
     * Checks whether a login attempt was successful. First checks if there exists a user with that primary email.
     * Then checks that the password is correct. If it is, a token is created for that user and is stored for future
     * actions.
     *
     * @param email   A string of what could be an existing email
     * @param attempt The password attempt
     * @return A ResponseEntity detailing the results
     */
    public ResponseEntity checkLogin(String email, String attempt, HttpServletResponse response) {
        User user = userRepository.findUserByEmail(email);

        if (user == null) {
            return new ResponseEntity("Email does not exist", HttpStatus.FORBIDDEN);
        }

        try {
            String encryptedPassword = EncryptionUtil.getEncryptedPassword(attempt, user.getSalt());
            if (user.getPassword().equals(encryptedPassword)) {

                //Generate session token
                String sessionToken = RandomToken.getToken(40);
                Session session = new Session(sessionToken);
                user.addSession(session);
                userRepository.save(user);

                // create a cookie
                Cookie cookie = new Cookie("s_id", sessionToken);
                // expires in 7 days
                cookie.setMaxAge(7 * 24 * 60 * 60);
                cookie.setHttpOnly(true);
                // add cookie to response
                response.addCookie(cookie);
                return new ResponseEntity(user.toJson(), HttpStatus.OK);
            } else {
                return new ResponseEntity("Incorrect password", HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "can't check password");
            return new ResponseEntity("An error occurred", HttpStatus.FORBIDDEN);
        }
    }


    /**
     * Firstly checks that the user is authenticated and the old password matches the Users current password. Then
     * updates the users password and salt if successful
     *
     * @param profileId    The id of the user to be changed
     * @param sessionToken The authentication token
     * @param oldPassword  The attempt for Users original password
     * @param newPassword  The password to set to
     * @return
     */
    public ResponseEntity changePassword(long profileId, String sessionToken, String oldPassword, String newPassword) {
        ResponseEntity response;
        Optional<User> getUser = userRepository.findById(profileId);
        if (getUser.isPresent()) {
            User user = getUser.get();
            Session session = sessionRepository.findUserIdByToken(sessionToken);
            if (session != null) {

                //If the user associated with the session matches the user to be edited, or if the user is an admin
                if (session.getUser().getUserId() == profileId || session.getUser().getPermissionLevel() > 0) {
                    if (session.getUser().getPermissionLevel() == 0) {
                        try {
                            String encryptedPassword = EncryptionUtil.getEncryptedPassword(oldPassword, user.getSalt());
                            if (!user.getPassword().equals(encryptedPassword)) {
                                return responseHandler.formatErrorResponse(400, "Current password is incorrect");
                            }
                        } catch (Exception e) {
                            return responseHandler.formatErrorResponse(400, "Failed to compare oldPassword to the User's current password");
                        }
                    }
                    try {
                        String salt = EncryptionUtil.getNewSalt();
                        user.setSalt(salt);
                        user.setEncryptedPassword(EncryptionUtil.getEncryptedPassword(newPassword, user.getSalt()));
                        userRepository.save(user);
                        response = responseHandler.formatSuccessResponse(200, "Successfully changed the password");
                    } catch (Exception e) {
                        response = responseHandler.formatErrorResponse(400, "Error while creating new password");
                    }
                } else {
                    response = responseHandler.formatErrorResponse(400, "Error while creating new password");
                }
            } else {
                return responseHandler.formatErrorResponse(400, "Invalid Session");
            }
        } else {
            response = responseHandler.formatErrorResponse(400, "No user with that ID");
        }
        return response;
    }

    /**
     * Updates a user's activity types. If a supplied activity type doesn't actually exist, it is skipped.
     *
     * @param activityTypes An ArrayList of activity type Strings
     * @param id            The Long ID of the User to modify
     * @return ResponseEntity of result
     */
    public ResponseEntity editActivityTypes(List<String> activityTypes, long id) {
        boolean result = false;
        HashSet<ActivityType> newActivityTypes = new HashSet<>();

        for (String name : activityTypes) {
            ActivityType type = activityTypeRepository.findActivityTypeByName(name);
            if (type != null) {
                newActivityTypes.add(type);
            } else {
                return new ResponseEntity("Activity type doesn't exist", HttpStatus.valueOf(400));
            }
        }

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setActivityTypes(newActivityTypes);
            userRepository.save(user);
            result = true;
        }

        if (result) {
            return new ResponseEntity("Successfully updated activity types", HttpStatus.valueOf(200));
        } else {
            return new ResponseEntity("No user with that ID", HttpStatus.valueOf(401));
        }
    }

    /**
     * Updates a uses city, state, and country.
     * @param city String city name
     * @param state String state name
     * @param country String country name
     * @param userId Long id of user to update
     * @return ResponseEntity of result
     */
    public ResponseEntity editLocation(String city, String state, String country, Long userId) {
        boolean result = false;

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setCity(city);
            user.setState(state);
            user.setCountry(country);
            userRepository.save(user);
            result = true;
        }

        if (result) {
            return new ResponseEntity("Successfully updated location", HttpStatus.valueOf(200));
        } else {
            return new ResponseEntity("No user with that ID", HttpStatus.valueOf(401));
        }
    }

    /**
     * Allows the user to delete their account and admins to delete another registered user's account
     * @param profileId    the user's id
     * @param sessionToken the user's token from the cookie for their current session.
     * @return response entity to inform user or admin if deleting the user was successful or not
     */
    public ResponseEntity deleteUser(Long profileId,
                                     String sessionToken,
                                     HttpServletResponse response) {
        try {
            Session session = sessionRepository.findUserIdByToken(sessionToken);
            if (session == null) {
                return new ResponseEntity("Invalid Session", HttpStatus.UNAUTHORIZED);
            }

            //If the session matches the user or the user has admin privileges
            boolean isAdmin = java.util.Objects.equals(session.getUser().getPermissionLevel().toString(), "1");
            boolean isDefaultAdmin = java.util.Objects.equals(session.getUser().getPermissionLevel().toString(), "2");
            if (isAdmin || isDefaultAdmin || session.getUser().getUserId().equals(profileId)) {
                if (userRepository.findById(profileId).isPresent()) {
                    if (session.getUser().getUserId().equals(profileId)) {
                        // create a cookie
                        Cookie cookie = new Cookie("s_id", null);
                        cookie.setMaxAge(0);
                        cookie.setHttpOnly(true);
                        cookie.setPath("/");
                        //add cookie to response
                        response.addCookie(cookie);
                    }
                    userRepository.deleteById(profileId);
                } else {
                    return new ResponseEntity("User Not Found", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity("Unauthorised User", HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity("Successfully Deleted User", HttpStatus.OK);
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "Could not delete user");
            return new ResponseEntity("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Deals with pagination where you can search users with email, full name and last name
     *
     * @param page     number of a page you want to be at
     * @param size     how many results you want on a page
     * @param email    email of the user you want to search
     * @param fullname full name of the user you want to search
     * @param lastname last name of the user you want to search
     * @param activityTypes activityTypes of the user you want to search
     * @param method
     * @return Page object with list SearchUserResponse object with user's email, full name, nickname
     */
    public Page<SearchUserDto> findPaginatedByQuery(int page, int size, String email, String fullname, String lastname, Set<ActivityType> activityTypes, String method) {
        Page<User> userPage;
        if (activityTypes != null) {
            if (method.equals("or")) {
                userPage = userRepository.findAllByActivityTypesOR(PageRequest.of(page, size), email, fullname, lastname, activityTypes);
            } else {
                 userPage = userRepository.getUsersWithActivityTypeAnd(PageRequest.of(page, size), email, fullname, lastname, activityTypes);
                }
        } else {
            userPage = userRepository.findAll(generateSpecification(lastname, fullname, email), PageRequest.of(page, size));
        }
        return userPageToSearchResponsePage(userPage);
    }

    /**
     * Finds the intersection of a List of Sets of Users. Much of this code was adapted from
     * https://stackoverflow.com/questions/37749559/conversion-of-list-to-page-in-spring
     * @param listOfUserSets A List of Sets of User objects
     * @return The intersection of these Sets as a List
     */
    public List<User> getIntersectionOfListOfSetsOfUsers(List<Set<User>> listOfUserSets) {
        List<User> result = new ArrayList<>();
        if (!listOfUserSets.isEmpty()) {
            Set<User> userCross = listOfUserSets.get(0);
            for (int i = 1; i < listOfUserSets.size(); i++) {
                userCross.retainAll(listOfUserSets.get(i));
            }
            for (User user : userCross) {
                result.add(user);
            }
        }
        return result;
    }

    /**
     * Helper function used in findPaginated and findPaginatedByQuery,
     * creates SearchUserResponse object which has user email, full name and nickname details
     * from the list of users in page object returned by the query
     *
     * @param users Page object that contains list of users found by the query
     * @return Page object with list of SearchUserResponse object
     */
    private Page<SearchUserDto> userPageToSearchResponsePage(Page<User> users) {
        List<SearchUserDto> userResponses = new ArrayList<>();
        for (User user : users) {
            SearchUserDto searchUserDto = new SearchUserDto();
            searchUserDto.setEmail(user.getPrimaryEmail());
            searchUserDto.setFirstname(user.getFirstName());
            searchUserDto.setLastname(user.getLastName());
            searchUserDto.setMiddlename(user.getMiddleName());
            searchUserDto.setNickname(user.getNickName());
            searchUserDto.setActivityTypes(user.getActivityTypes());
            userResponses.add(searchUserDto);
        }
        return new PageImpl<>(userResponses);
    }

    /***
     * Gives a normal user admin rights if the requesting user is authenticated and is an admin.
     * @param jsonString the request body.
     * @param profileID the id of the user being promoted to admin.
     * @param sessionToken the authentication token of the admin performing the request.
     * @return the response status that specifies if the operation was successful or not.
     */
    public ResponseEntity promoteUser(String jsonString, Long profileID, String sessionToken) {
        ResponseEntity result;

        try {
            if (sessionToken == null) {
                result = responseHandler.formatErrorResponse(401, "Invalid Session");
            } else {
                Session session = sessionRepository.findUserIdByToken(sessionToken);
                int userPermissionLevel = session.getUser().getPermissionLevel();
                Optional<User> userToPromote = userRepository.getUserById(profileID);
                Map<String, Object> json = new JacksonJsonParser().parseMap(jsonString);
                String role = (String) json.get("role");
                if (!role.equals("admin")) {
                    result = responseHandler.formatErrorResponse(400, "Bad request");

                }
                else if (userToPromote.get().getPermissionLevel() > 0) {
                    result = responseHandler.formatErrorResponse(400, "User to promote is already an admin");
                }
                else if (userPermissionLevel == 0) {
                    result = responseHandler.formatErrorResponse(403, "Unauthorized user");
                } else {
                    userRepository.grantAdminRights(profileID);
                    result = responseHandler.formatSuccessResponse(200, "User successfully promoted");

                }
            }

        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "Could not promote user");
            result = responseHandler.formatErrorResponse(500, "An error occurred");
        }

        return result;
    }

    /***
     * Checks that both user and activity exist and checks if the user follows the activity already or not before
     * allowing the user to follow the activity
     * @param profileId id of the user that wishes to follow the activity
     * @param activityId id of the activity that the user wishes to follow
     * @param sessionToken session token of user that wishes to follow activity
     * @return response entity with status code depending on wither it was successful or not
     */
    public ResponseEntity subscribeToActivity(Long profileId, Long activityId, String sessionToken) {
        ResponseEntity result;

        try {
            Session session = sessionRepository.findUserIdByToken(sessionToken);
            Activity activity = activityRepository.findActivityById(activityId);
            if (sessionToken == null) {
                result = responseHandler.formatErrorResponse(401, "Invalid Session");
            } else if (!profileId.equals(session.getUser().getUserId())
                    && session.getUser().getPermissionLevel() == 0) {
                result = responseHandler.formatErrorResponseString(403, "Invalid user");
            } else if (activity == null) {
                result = responseHandler.formatErrorResponseString(404, "Activity not found");
            } else {
                Optional<User> user = userRepository.getUserById(profileId);
                if (user.isPresent()) {
                    User validUser = user.get();
                    Set<Activity> validUserFollowingList = validUser.getActivities();
                    if (validUserFollowingList.contains(activity)) {
                        result = responseHandler.formatErrorResponse(403,
                                "User already follows this activity");
                    } else {
                        activity.addUsers(validUser);
                        userRepository.save(validUser);
                        activityRepository.save(activity);
                        result = responseHandler.formatSuccessResponse(201, "User " + profileId
                                + " now follows activity " + activityId);
                    }
                } else {
                    result = responseHandler.formatErrorResponse(404, "No user with id "
                            + profileId);
                }
            }
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "Could not subscribe to activity");
            result = responseHandler.formatErrorResponse(500, "An error occurred");
        }
        return result;
    }


    /***
     * Gives a normal user admin rights if the requesting user is authenticated and is an admin.
     * @param lastName last name of the user you are searching
     * @param fullName full name of the user you are searching
     * @param email email of the user you are searching
     * @return specification object with User search request (WHERE part of a query)
     */
    private Specification<User> generateSpecification(String lastName, String fullName, String email) {
        return Specification.where(UserSpecification.searchByLastName(lastName))
                .and(
                        UserSpecification.searchByFullName(fullName))
                .and(
                        UserSpecification.searchByEmail(email))
                .and(
                        UserSpecification.searchIsNotAdmin()
                );
    }
}