package com.springvuegradle.Hakinakina.controller;

import com.springvuegradle.Hakinakina.entity.*;
import com.springvuegradle.Hakinakina.util.EncryptionUtil;
import com.springvuegradle.Hakinakina.util.ErrorHandler;
import com.springvuegradle.Hakinakina.util.ResponseHandler;
import com.springvuegradle.Hakinakina.util.RandomToken;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletResponse;
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
    public UserController(UserRepository userRepository, PassportCountryRepository countryRepository, EmailRepository emailRepository, SessionRepository sessionRepository, UserService userService) {
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
        this.emailRepository = emailRepository;
        this.sessionRepository = sessionRepository;
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
    public ResponseEntity<String> editEmail(@RequestBody String request, @PathVariable("profileId") long userId) {
        return userService.editEmail(request, userId);
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
    public ResponseEntity addEmails(@RequestBody String request, @PathVariable("profileId") long userId, @RequestHeader("Authorization") String sessionToken) {
        return userService.addEmails(request, userId, sessionToken);
    }

    @PostMapping("/editprofile")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity editUser(@RequestBody User user) {
        userRepository.save(user);
        return responseHandler.formatSuccessResponse(201, "User updated");
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
     * Processes request to retrieve certain user and returns
     *
     * @param profileId
     * @return Specific user
     */
    @GetMapping("/profiles/{profile_id}")
    public ResponseEntity getOneUser(@PathVariable("profile_id") long profileId) {
        Optional<User> optional = userRepository.findById(profileId);
        if (optional.isPresent()) {
            User user = optional.get();
            return responseHandler.formatGetUser(user);
        } else {
            return responseHandler.formatErrorResponse(400, "User does not exist");
        }
    }

    /**
     * Processes get users request
     *
     * @return List of users
     */
    @GetMapping("/countries")
    public String getAllCountries() {
        return countryRepository.findAll().toString();
    }

    @GetMapping("/emails")
    public List<Email> getAllEmails() {
        return emailRepository.findAll();
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
    public ResponseEntity checkLogin(@RequestBody String jsonString, HttpServletResponse response) {
        Map<String, Object> json = new JacksonJsonParser().parseMap(jsonString);
        String attempt = (String) json.get("password");
        String email = (String) json.get("email");

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
                user.addSession(session_token);
                sessionRepository.insertToken(sessionToken, user.getUser_id());

                return new ResponseEntity("[" + user.toJson() + ", {\"sessionToken\": \"" + sessionToken + "\"}]", HttpStatus.valueOf(201));
            } else {
                return new ResponseEntity("Incorrect password", HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "can't check password");
            return new ResponseEntity("An error occurred", HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/profiles/{profileId}/password")
    public Object editPassword(@RequestBody String jsonString, @PathVariable Long profileId, @RequestHeader("Authorization") String sessionToken) {
        Map<String, Object> json = new JacksonJsonParser().parseMap(jsonString);
        String oldPassword = (String) json.get("old_password");
        String newPassword = (String) json.get("new_password");
        String repeatPassword = (String) json.get("repeat_password");
        ResponseEntity response;

        if (!newPassword.equals(repeatPassword)) {
            return responseHandler.formatErrorResponse(400, "newPassword and repeatPassword do no match");
        }

        Optional<User> getUser = userRepository.findById(profileId);
        if (getUser.isPresent()) {
            User user = getUser.get();
            if(user.getSessions().contains(sessionToken)){
                try {
                    String encryptedPassword = EncryptionUtil.getEncryptedPassword(oldPassword, user.getSalt());
                    if (!user.getPassword().equals(encryptedPassword)) {
                        return responseHandler.formatErrorResponse(400, "oldPassword is incorrect");
                    }
                } catch (Exception e) {
                    return responseHandler.formatErrorResponse(400, "Failed to compare oldPassword to the User's current password");
                }

                try {
                    String salt = EncryptionUtil.getNewSalt();
                    user.setSalt(salt);
                    user.setPassword(EncryptionUtil.getEncryptedPassword(newPassword, user.getSalt()));
                    userRepository.save(user);
                    response = responseHandler.formatSuccessResponse(200, "Successfully changed the password");
                } catch (Exception e) {
                    response = responseHandler.formatErrorResponse(400, "Error while creating new password");
                }
            }else{
                response = responseHandler.formatErrorResponse(400, "Error while creating new password");
            }
        } else {
            response = responseHandler.formatErrorResponse(400, "No user with that ID");
        }
        return response;
    }

    // Create Exception Handle
    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Request ID not found.")
    @ExceptionHandler(IllegalArgumentException.class)
    public void badIdExceptionHandler() {
        //Nothing to do
    }
}
