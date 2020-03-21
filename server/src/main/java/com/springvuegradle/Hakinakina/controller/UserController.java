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

    @PutMapping("/profiles/{profileId}")
    public ResponseEntity editUser(@RequestBody User user, @PathVariable("profileId") long profileId, @CookieValue("s_id") String sessionToken) {
        Session session = sessionRepository.findUserIdByToken(sessionToken);
        if (session != null) {
            if (session.getUser().getUserId() == profileId) {
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
        } else {
            return responseHandler.formatErrorResponse(400, "Invalid Session");
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
    public ResponseEntity addEmails(@RequestBody String request, @PathVariable long profileId, @CookieValue("s_id") String sessionToken) {
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
            return new ResponseEntity(user.toJson(), HttpStatus.valueOf(200));
        } else {
            return new ResponseEntity("User does not exist", HttpStatus.valueOf(403));
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
    public List<String> getAllEmails() {
        //ToDO use the commented out return statement rather than the current one once the email table has been fixed
        /*
        return emailRepository.getAllEmails();
         */
        return userRepository.getAllPrimaryEmails();
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
    public ResponseEntity checkLogout(@CookieValue("s_id") String sessionToken) {
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
    public ResponseEntity editPassword(@RequestBody String jsonString, @PathVariable Long profileId, @CookieValue("s_id") String sessionToken) {
        Map<String, Object> json = new JacksonJsonParser().parseMap(jsonString);
        String oldPassword = (String) json.get("old_password");
        String newPassword = (String) json.get("new_password");
        String repeatPassword = (String) json.get("repeat_password");

        if (!newPassword.equals(repeatPassword)) {
            return responseHandler.formatErrorResponse(400, "newPassword and repeatPassword do no match");
        }
        return userService.changePassword(profileId, sessionToken, oldPassword, newPassword);
    }

    // Create Exception Handle
    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Request ID not found.")
    @ExceptionHandler(IllegalArgumentException.class)
    public void badIdExceptionHandler() {
        //Nothing to do
    }
}
