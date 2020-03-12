package com.springvuegradle.Hakinakina.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.Hakinakina.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

/** Class for handling server reponses to client using http codes */
public class ResponseHandler {

    public ResponseHandler() {}

    /**
     * Formats a success response to return to the client
     *
     * @param statusCode
     * @param content
     */
    public ResponseEntity formatSuccessResponse(int statusCode, String content) {
        String details =  String.format("{\n" +
                "\"StatusCode\": \"%d\",\n" +
                "\"Content\": \"%s\"\n" +
                "}", statusCode, content);

        return new ResponseEntity(details, HttpStatus.valueOf(statusCode));
    }

    /**
     * Formats an error response to return to the client
     *
     * @param statusCode
     * @param error
     */
    public ResponseEntity formatErrorResponse(int statusCode, String error) {
        String details = String.format("{\n" +
                "\"StatusCode\": \"%d\",\n" +
                "\"Errors\": [\n" +
                "\"%s\"\n" +
                "]\n" +
                "}", statusCode, error);

        return new ResponseEntity(details, HttpStatus.valueOf(statusCode));
    }

    /**
     * Formats an error response to return to the client when multiple errors occur
     *
     * @param statusCode
     * @param errors
     */
    public ResponseEntity formatErrorResponse(int statusCode, ArrayList<String> errors) {
        //TODO Format errors better

        String allErrors = "";
        for (String error: errors){
            allErrors +=  "\n" + "\"" + error + "\"";
        }
        System.out.println(errors);

        String details = String.format("{\n" +
                "\"StatusCode\": \"%d\",\n" +
                "\"Errors\": [" +
                "%s\n" +
                "]\n" +
                "}", statusCode, allErrors);

        return new ResponseEntity(details, HttpStatus.valueOf(statusCode));
    }

    public ResponseEntity formatGetUsers(List<User> users) {
        String userList = "{\n\"Users\": [\n";
        int firstCheck = 0;
        for (User user: users) {
            if (firstCheck == 0) {
                userList += String.format("\"%d %s %s\"", user.getUser_id(), user.getFirstName(), user.getLastName());
                firstCheck = 1;
            } else {
                userList += String.format(",\n\"%d %s %s\"", user.getUser_id(), user.getFirstName(), user.getLastName());
            }
        }
        userList += "\n]\n}";
        return new ResponseEntity(userList, HttpStatus.OK);
    }

    public ResponseEntity formatGetUser(User user) {
        ObjectMapper objectMapper = new ObjectMapper();
        String userStr = null;
        try {
            userStr = objectMapper.writeValueAsString(user);
        } catch (Exception exception) {
            ErrorHandler.printProgramException(exception, "Could not map user to JSON string");
        }
        return new ResponseEntity(userStr, HttpStatus.OK);
    }
}
