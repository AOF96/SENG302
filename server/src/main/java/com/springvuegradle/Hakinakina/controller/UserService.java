package com.springvuegradle.Hakinakina.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.Hakinakina.entity.*;
import com.springvuegradle.Hakinakina.util.EncryptionUtil;
import com.springvuegradle.Hakinakina.util.ErrorHandler;
import com.springvuegradle.Hakinakina.util.RandomToken;
import com.springvuegradle.Hakinakina.util.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

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
    private ResponseHandler responseHandler = new ResponseHandler();

    public UserService(UserRepository userRepository, EmailRepository emailRepository,
                       PassportCountryRepository countryRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
        this.countryRepository = countryRepository;
        this.sessionRepository = sessionRepository;
    }

    /**
     * Checks whether an email exists by checking the repository and whether a user exists with that email as their primary
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
    public ResponseEntity<String> editEmail(String request, long userId) {
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
        User user = userRepository.findById(userId).get();

        String primaryEmail = node.get("primary_email").asText();
        List<JsonNode> secondaryEmailNodes = node.findValues("additional_email");

        ArrayList<String> secondaryEmails = new ArrayList<>();
        for (JsonNode node1 : secondaryEmailNodes.get(0)) {
            secondaryEmails.add(node1.asText());
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
        if (secondaryEmails.contains(user.getPrimaryEmail()) && emailRepository.findEmailByString(newPrimary).getUser() == user) {
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

        for (Email emailToRemove : emailsToRemove) {
            user.removeEmail(emailToRemove);
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
            return responseHandler.formatErrorResponse(400, "No emails successfully updated, emails either in use or empty");
        } else {
            return responseHandler.formatSuccessResponse(200, "Secondary emails successfully added: " + emailsAdded);
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

    /**
     * Takes a User object and makes several checks before saving them to the repository after a request to create a
     * new user
     * @param user A User object to check
     * @return A ResponseEntity detailing the results
     */
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
        }
        if (user.getBirthDate() == null) {
            messages.add("Please provide a valid date of birth, yyyy-mm-dd.");
        }
        if (user.getGender() == null) {
            messages.add("Please provide a valid gender. male, female or non-binary.");
        }
        if(user.getFitnessLevel() < 0 || user.getFitnessLevel() > 5){
            messages.add("Please select the fitness level in the range 0 and 5");
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

    /**
     * Takes a User object and makes several checks before saving them to the repository after a request to edit a
     * user
     * @param user A User object to check
     * @return A ResponseEntity detailing the results
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
        if(user.getFitnessLevel() < 0 || user.getFitnessLevel() > 5){
            messages.add("You cannot delete the required filed. Please select the fitness level in the range 0 and 5");
        }

        if (!messages.isEmpty()) {
            return responseHandler.formatErrorResponse(403, messages);
        } else {
            userRepository.save(user);
            return responseHandler.formatSuccessResponse(200, "User updated");
        }
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
}
