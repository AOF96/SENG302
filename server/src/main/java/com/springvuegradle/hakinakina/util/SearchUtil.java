package com.springvuegradle.hakinakina.util;

/**
 * Helper class when dealing with searching
 */
public class SearchUtil {
    /**
     * Helper function used in searchByActivityName
     * checks whether the strings given has quotation marks (" or ') around the search term string.
     * @param searchText text(string) you are using to search for an activity
     * @return true if quotation wraps the search term, false otherwise
     */
    public static boolean isWithinQuotation(String searchText) {
        if (searchText != null && searchText.length() > 1) {
            return searchText.startsWith("\"") && searchText.endsWith("\"") || searchText.startsWith("'") && searchText.endsWith("'");
        }
        return false;
    }
}
