package com.springvuegradle.hakinakina.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.repository.*;
import com.springvuegradle.hakinakina.util.EncryptionUtil;
import com.springvuegradle.hakinakina.util.ErrorHandler;
import com.springvuegradle.hakinakina.util.RandomToken;
import com.springvuegradle.hakinakina.util.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

/**
 * Class for user profile request actions
 */
@Service
public class UserService {

    private UserRepository userRepository;
    private EmailRepository emailRepository;
    private PassportCountryRepository countryRepository;
    private SessionRepository sessionRepository;
    private ActivityTypeRepository activityTypeRepository;
    private ResponseHandler responseHandler = new ResponseHandler();

    public UserService(UserRepository userRepository, EmailRepository emailRepository,
                       PassportCountryRepository countryRepository, SessionRepository sessionRepository,
                       ActivityTypeRepository activityTypeRepository) {
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
        this.countryRepository = countryRepository;
        this.sessionRepository = sessionRepository;
        this.activityTypeRepository = activityTypeRepository;
    }

    /**
     * Checks whether an email has the correct format
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
     *
     *edits email
     *
     * PUT /profiles/{profileId}/emails
     * {
     *   "primary_email": "triplej@google.com",
     *   "additional_email": [
     *     "triplej@xtra.co.nz",
     *     "triplej@msn.com"
     *   ]
     * }
     * */
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
            if(emailsRemoved.equals("")){
                return responseHandler.formatErrorResponse(400, "No emails added.");
            }else {
                return responseHandler.formatSuccessResponse(200, "Secondary emails successfully removed: " + emailsRemoved);
            }
        } else {
            if(emailsRemoved.equals("")){
                return responseHandler.formatSuccessResponse(200, "Secondary emails successfully added: " + emailsAdded);
            }else {
                return responseHandler.formatSuccessResponse(200, "Secondary emails added: " + emailsAdded + " - Secondary emails removed: " + emailsRemoved);
            }
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

    public ResponseEntity validateCreateProfile(User user) {
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
        } else if (isEmailProperlyFormatted(user.getPrimaryEmail()) != true){
            messages.add("Please provide a valid email.");
        }
        if (user.getBirthDate() == null) {
            messages.add("Please provide a valid date of birth, yyyy-mm-dd.");
        }
        if (user.getGender() == null) {
            messages.add("Please provide a valid gender. male, female or non-binary.");
        }
        if(user.getFitnessLevel() < 0 || user.getFitnessLevel() > 4){
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
                RandomToken randomToken = new RandomToken();
                String sessionToken = randomToken.getToken(40);
                Session session_token = new Session(sessionToken);
                userRepository.save(user);
                sessionRepository.insertToken(sessionToken, user.getUserId());

                return new ResponseEntity("[" + user.toJson() + ", {\"sessionToken\": \"" + sessionToken + "\"}]", HttpStatus.valueOf(201));
            }
        } else {
            return responseHandler.formatErrorResponse(400, messages);
        }
    }

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
        if(user.getFitnessLevel() < 0 || user.getFitnessLevel() > 4){
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
        LocalDate birthDate = LocalDate.of(date.getYear() + 1900, date.getMonth(), date.getDate());
        if ((birthDate != null) && (currentDate != null)) {
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
     * @param email A string of what could be an existing email
     * @param attempt The password attempt
     * @return A ResponseEntity detailing the results
     */
    public ResponseEntity checkLogin(String email, String attempt) {
        User user = userRepository.findUserByEmail(email);

        if (user == null) {
            return new ResponseEntity("Email does not exist", HttpStatus.FORBIDDEN);
        }

        try {
            String encryptedPassword = EncryptionUtil.getEncryptedPassword(attempt, user.getSalt());
            if (user.getPassword().equals(encryptedPassword)) {
                //Generate session token
                RandomToken randomToken = new RandomToken();
                String sessionToken = randomToken.getToken(40);
                Session session_token = new Session(sessionToken);
                sessionRepository.insertToken(sessionToken, user.getUserId());

                return new ResponseEntity("[" + user.toJson() + ", {\"sessionToken\": \"" + sessionToken + "\"}]", HttpStatus.valueOf(201));
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
     * @param profileId The id of the user to be changed
     * @param sessionToken The authentication token
     * @param oldPassword The attempt for Users original password
     * @param newPassword The password to set to
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
                if(session.getUser().getUserId() == profileId || session.getUser().getPermissionLevel() > 0) {
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
     * @param activityTypes An ArrayList of activity type Strings
     * @param id The Long ID of the User to modify
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
}
