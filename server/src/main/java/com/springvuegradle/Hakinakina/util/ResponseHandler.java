package com.springvuegradle.Hakinakina.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.Hakinakina.entity.User;

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
    public String formatSuccessResponse(int statusCode, String content) {
        return String.format("{\n" +
                "\"StatusCode\": \"%d\",\n" +
                "\"Content\": \"%s\"\n" +
                "}", statusCode, content);
    }

    /**
     * Formats an error response to return to the client
     *
     * @param statusCode
     * @param error
     */
    public String formatErrorResponse(int statusCode, String error) {
        return String.format("{\n" +
                "\"StatusCode\": \"%d\",\n" +
                "\"Errors\": [\n" +
                "\"%s\"\n" +
                "]\n" +
                "}", statusCode, error);
    }

    /**
     * Formats an error response to return to the client when multiple errors occur
     *
     * @param statusCode
     * @param errors
     */
    public String formatErrorResponse(int statusCode, ArrayList<String> errors) {
        return String.format("{\n" +
                "\"StatusCode\": \"%d\",\n" +
                "\"Errors\": [\n" +
                "\"%s\"\n" +
                "]\n" +
                "}", statusCode, errors);
    }

    public String formatGetUsers(List<User> users) {
        String response = "{\n\"Users\": [\n";
        int firstCheck = 0;
        for (User user: users) {
            if (firstCheck == 0) {
                response += String.format("\"%d %s %s\"", user.getUser_id(), user.getFirstName(), user.getLastName());
                firstCheck = 1;
            } else {
                response += String.format(",\n\"%d %s %s\"", user.getUser_id(), user.getFirstName(), user.getLastName());
            }
        }
        response += "\n]\n}";
        return response;
    }

    public String formatGetUser(User user) {
        ObjectMapper objectMapper = new ObjectMapper();
        String userStr = null;
        try {
            userStr = objectMapper.writeValueAsString(user);
        } catch (Exception exception) {
            ErrorHandler.printProgramException(exception, "Could not map user to JSON string");
        }
        return userStr;
    }
}
