package com.springvuegradle.Hakinakina.util;

/**
 * Class with methods for handling program exceptions and for handling errors to return to client application
 */
public class ErrorHandler {

    /**
     * Prints message describing exception to the console
     * @param exception
     * @param message
     */
    public static void printProgramException(Exception exception, String message) {
        System.out.println(String.format("%s: %s", exception, message));
    }

    /**
     * Prints given error code and description to the console, will eventually send to client
     * @param errorCode
     * @param message
     */
    public static void printRunningError(int errorCode, String message) {
        System.out.println(String.format("Error %d: %s", errorCode, message));
    }
}
