package com.springvuegradle.Hakinakina.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.Hakinakina.entity.User;

/**
 * Class for parsing JSON to the correct object
 */
public class JSONParser {

    /**
     * Constructor for the JSON parser
     */
    public JSONParser() {

    }

    /**
     * Parses a JSON user creation request into a User object
     * @param request
     * @return User object
     */
    public User createProfile(String request) {
        ObjectMapper mapper = new ObjectMapper();
        User user;
        try {
            user = mapper.readValue(request, User.class);
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "Error mapping JSON to user");
            user = null;
        }
        return user;
    }
}
