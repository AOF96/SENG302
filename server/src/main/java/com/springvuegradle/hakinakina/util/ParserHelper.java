package com.springvuegradle.hakinakina.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.springvuegradle.hakinakina.entity.Location;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

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
        String latitudeString = getValueString(node, "latitude");
        double latitude = 0;
        if (latitudeString != null) {
            if (!latitudeString.equals("")) {
                latitude = Double.parseDouble(latitudeString);
            }
        }
        String longitudeString = getValueString(node, "longitude");
        double longitude = 0;
        if (longitudeString != null) {
            if (!longitudeString.equals("")) {
                longitude = Double.parseDouble(longitudeString);
            }
        }

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
        } else if (fieldValue.asText().equals("null")) {
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

    /**
     * Converts a date-time string of the formal YYYY-MM-DDTHH:MM:SS into a TimeStamp object.
     * @param datetime A string
     * @return A TimeStamp object.
     */
    public static Timestamp parseDateTime(String datetime) {
        LocalDateTime temp = LocalDateTime.parse(datetime).plusHours(12L);
        return Timestamp.valueOf(temp);
    }
}
