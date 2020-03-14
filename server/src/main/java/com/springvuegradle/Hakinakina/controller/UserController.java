package com.springvuegradle.Hakinakina.controller;

import com.springvuegradle.Hakinakina.entity.*;
import com.springvuegradle.Hakinakina.util.EncryptionUtil;
import com.springvuegradle.Hakinakina.util.ErrorHandler;
import com.springvuegradle.Hakinakina.util.ResponseHandler;
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
    private ResponseHandler responseHandler = new ResponseHandler();

    private UserService userService;

    /**
     * Contructs a UserController, passing in the repositories so that they can be accessed.
     * @param userRepository The repository containing Users
     * @param countryRepository The repository containing PassportCountries
     * @param emailRepository The repository containing Emails
     */
    public UserController(UserRepository userRepository, PassportCountryRepository countryRepository, EmailRepository emailRepository, UserService userService) {
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
        this.emailRepository = emailRepository;
        this.userService = userService;
    }

    /**
     * Processes create user request and puts user into repository if email is unique
     * @param user
     * @return success or error message
     */
    @PostMapping("/createprofile")
    @ResponseStatus(HttpStatus.OK)
    public String createProfile(@RequestBody User user) {
        if (!userService.emailExists(user.getPrimaryEmail())) {
            userRepository.save(user);
            return responseHandler.formatSuccessResponse(201, "User created");
        } else {
            return responseHandler.formatErrorResponse(400, "Email already exists");
        }
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
     * */
    @PutMapping("/profiles/{profileId}/emails")
    @ResponseStatus(HttpStatus.OK)
    public String editEmail(@RequestBody String request, @PathVariable("profileId") long userId) {
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
     * */
    @PostMapping("/profiles/{profileId}/emails")
    @ResponseStatus(HttpStatus.OK)
    public String addEmails(@RequestBody String request, @PathVariable("profileId") long userId) {
        return userService.addEmails(request, userId);
    }

    @PostMapping("/editprofile")
    @ResponseStatus(HttpStatus.OK)
    public String editUser(@RequestBody User user) {
        userRepository.save(user);
        return responseHandler.formatSuccessResponse(201, "User updated");
    }


    /**
     * Processes get users request
     *
     * @return List of users
     */
    @GetMapping("/users")
    public String getAllUsers() {
        List<User> users = userRepository.findAll();
        return responseHandler.formatGetUsers(users);
    }

    /**
     * Processes request to retrieve certain user and returns
     * @param userId
     * @return Specific user
     */
    @GetMapping("/user/{id}")
    public String getOneUser(@PathVariable("id") long userId) {
        Optional<User> optional = userRepository.findById(userId);
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
     * @param jsonString The JSON body passed as a string.
     * @return isLogin Whether the attempt was correct or not.
     */
    @GetMapping("/checklogin")
    public String checkLogin(@RequestBody String jsonString){
        Map<String, Object> json = new JacksonJsonParser().parseMap(jsonString);
        String attempt = (String) json.get("attempt");
        String email = (String) json.get("email");

        String response = null;

        User user = userRepository.findUserByEmail(email);

        if (user == null) {
            response = responseHandler.formatErrorResponse(400, "Email does not exist");
        }

        try {
            String encryptedPassword = EncryptionUtil.getEncryptedPassword(attempt, user.getSalt());
            if (user.getPassword().equals(encryptedPassword)) {
                response = responseHandler.formatSuccessResponse(200, "Login is correct");
            } else {
                response = responseHandler.formatErrorResponse(400, "Incorrect password");
            }
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "can't check password");
        }

        return response;
    }

    @PostMapping("/editpassword")
    public String editPassword(@RequestBody String jsonString) {
        Map<String, Object> json = new JacksonJsonParser().parseMap(jsonString);
        long id = Long.valueOf((int) json.get("profile_id"));
        String oldPassword = (String) json.get("old_password");
        String newPassword = (String) json.get("new_password");
        String repeatPassword = (String) json.get("repeat_password");
        String response = null;

        Optional<User> getUser = userRepository.findById(id);
        if (getUser.isPresent()) {
            User user = getUser.get();
            try {
                String salt = EncryptionUtil.getNewSalt();
                user.setSalt(salt);
                user.setPassword(EncryptionUtil.getEncryptedPassword(newPassword, user.getSalt()));
                userRepository.save(user);
                response = responseHandler.formatSuccessResponse(200, "Successfully changed the password");
            } catch (Exception e) {
                response = responseHandler.formatErrorResponse(400, "Error while creating new password");
            }
        }
        else {
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
