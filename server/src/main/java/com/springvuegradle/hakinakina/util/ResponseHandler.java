package com.springvuegradle.hakinakina.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

/** Class for handling server reponses to client using http codes */
public class ResponseHandler {

    public ResponseHandler() {}

    /**
     * Formats a success response to return to the client
     * @param statusCode
     * @param content
     */
    public ResponseEntity formatSuccessResponse(int statusCode, String content) {
        return new ResponseEntity(content, HttpStatus.valueOf(statusCode));
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

    /**
     * Formats a success response to return to the client
     *
     * @param statusCode status code of the response
     * @param content content of the response
     * @return ResponseEntity with response
     */
    public ResponseEntity<String> formatSuccessResponseString(int statusCode, String content) {
        return new ResponseEntity<String>(content, HttpStatus.valueOf(statusCode));
    }

    /**
     * Formats an error response to return to the client
     *
     * @param statusCode status code of the response
     * @param error response error message
     * @return ResponseEntity with response
     */
    public ResponseEntity<String> formatErrorResponseString(int statusCode, String error) {
        return new ResponseEntity<String>(error, HttpStatus.valueOf(statusCode));
    }
}
