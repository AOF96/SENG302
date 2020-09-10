package com.springvuegradle.hakinakina.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.springvuegradle.hakinakina.entity.Location;

public class ParserHelper {
    /**
     * Parses a JsonNode to create and return a Location object.
     * @param node The JsonNode to parse.
     * @return A Location object.
     */
    public static Location createLocation(JsonNode node) {
        String streetAddress = getValueString(node, "street_address");
        String suburb = getValueString(node, "suburb");
        int postcode = getValueInt(node, "postcode");
        String city = getValueString(node, "city");
        String state = getValueString(node, "state");
        String country = getValueString(node, "country");
        Long latitude = getValueLong(node, "latitude");
        Long longitude = getValueLong(node, "longitude");

        return new Location(streetAddress, suburb, city, postcode, state, country, latitude, longitude);
    }

    /**
     * Returns value of field if it exists
     *
     * @param node
     * @param field
     * @return string value or empty string
     */
    public static String getValueString(JsonNode node, String field) {
        JsonNode fieldValue = node.get(field);
        if (fieldValue == null) {
            return null;
        } else if (fieldValue.asText() == "null") {
            return null;
        } else {
            return fieldValue.asText();
        }
    }

    /**
     * Returns value of field if it exists
     *
     * @param node
     * @param field
     * @return int value or -1
     */
    public static int getValueInt(JsonNode node, String field) {
        JsonNode fieldValue = node.get(field);
        if (fieldValue == null) {
            return -1;
        } else {
            return fieldValue.asInt();
        }
    }

    /**
     * Returns value of field if it exists
     *
     * @param node
     * @param field
     * @return long value or null
     */
    public static Long getValueLong(JsonNode node, String field) {
        JsonNode fieldValue = node.get(field);
        if (fieldValue == null) {
            return 0L;
        } else {
            return fieldValue.asLong();
        }
    }
}
