package com.springvuegradle.hakinakina.serialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Parses a User object from json.
 * Could not use default spring parser because of specific missing attribute handling and converting gender to an object
 */
@Service
public class UserDeserializer extends StdDeserializer<User> {

    @Autowired
    PassportCountryRepository countryRepository;
    @Autowired
    EmailRepository emailRepository;

    @Autowired
    ActivityTypeRepository activityTypeRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    UserRepository userRepository;

    public UserDeserializer() {
        this(null);
    }

    public UserDeserializer(Class<?> vc) {
        super(vc);
    }

    /**
     * Method to deserialize user creation JSON to user object
     *
     * @param jp
     * @param ctxt
     * @return
     * @throws IOException
     * @throws JsonProcessingException
     */
    @Override
    public User deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        ObjectMapper mapper = new ObjectMapper();

        // Get compulsory attributes
        String lastName = getValueString(node, "lastname");
        String firstName = getValueString(node, "firstname");
        String primaryEmail = getValueString(node, "primary_email");
        String password = getValueString(node, "password");
        String dateOfBirth = getValueString(node, "date_of_birth");
        int fitnessLevel = getValueInt(node, "fitness");
        // Get gender
        String genderString = getValueString(node, "gender");
        Gender gender = null;
        switch (genderString.toLowerCase()) {
            case ("male"):
                gender = Gender.MALE;
                break;
            case ("female"):
                gender = Gender.FEMALE;
                break;
            case ("non-binary"):
                gender = Gender.NON_BINARY;
                break;
        }

        // Get other attributes if they exist
        String middleName = getValueString(node, "middlename");
        String nickName = getValueString(node, "nickname");
        String bio = getValueString(node, "bio");
        int permissionLevel = getValueInt(node, "permission_level");
        // Get passport countries
        Set<PassportCountry> userCountries = getPassportCountries(node, "passports");
        Set<Email> additionalEmail = getAdditionalEmail(node, "additional_email");
        Set<ActivityType> activityTypes = getActivityTypes(node, "activity_types");


        // Create user with compulsory attributes
        User user = new User(firstName, lastName, primaryEmail, dateOfBirth, gender, fitnessLevel, password);

        // Add additional attributes
        for (PassportCountry country : userCountries) {
            user.addPassportCountry(country);
        }
        for (Email email : additionalEmail) {
            user.addEmail(email);
        }
        for (ActivityType activityType : activityTypes) {
            user.addActivityTypes(activityType);
        }
        if (middleName != null) {
            user.setMiddleName(middleName);
        }
        if (nickName != null) {
            user.setNickName(nickName);
        }
        if (bio != null) {
            user.setBio(bio);
        }
        if (permissionLevel != -1) {
            user.setPermissionLevel(permissionLevel);
        } else {
            user.setPermissionLevel(0);
        }
        if (node.get("location") != null) {
            Location location = createLocation(node.get("location"), primaryEmail);
            locationRepository.save(location);
            user.setHomeLocation(location);
        }
        return user;
    }

    /**
     * Parses a JsonNode to create or update and return a Location object.
     * @param node The JsonNode to parse.
     * @return A Location object.
     */
    public Location createLocation(JsonNode node, String userEmail) {
        String streetAddress = getValueString(node, "street_address");
        String suburb = getValueString(node, "suburb");
        int postcode = getValueInt(node, "postcode");
        String city = getValueString(node, "city");
        String state = getValueString(node, "state");
        String country = getValueString(node, "country");
        Long latitude = getValueLong(node, "latitude");
        Long longitude = getValueLong(node, "longitude");

        Location location = getOldLocation(userEmail);

        location.setStreetAddress(streetAddress);
        location.setSuburb(suburb);
        location.setPostcode(postcode);
        location.setCity(city);
        location.setState(state);
        location.setCountry(country);
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        return location;
    }

    /**
     * Gets the user's old location or creates a new one if it doesn't exist
     * @param userEmail the primary email of the user getting updated
     * @return old location if exists or new location
     */
    public Location getOldLocation(String userEmail) {
        User user = userRepository.findUserByEmail(userEmail);
        if (user.getHomeLocation() != null) {
            return user.getHomeLocation();
        } else {
            return new Location();
        }
    }

    /**
     * Returns value of field if it exists
     *
     * @param node
     * @param field
     * @return string value or empty string
     */
    public String getValueString(JsonNode node, String field) {
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
    public int getValueInt(JsonNode node, String field) {
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
    public Long getValueLong(JsonNode node, String field) {
        JsonNode fieldValue = node.get(field);
        if (fieldValue == null) {
            return 0L;
        } else {
            return fieldValue.asLong();
        }
    }

    /**
     * Returns set of PassportCountry in user creation request
     *
     * @param node
     * @param field
     * @return
     */
    public Set<PassportCountry> getPassportCountries(JsonNode node, String field) {
        JsonNode countryNodes = node.get(field);
        if (countryNodes == null) {
            return new HashSet<>();
        } else {
            Set<PassportCountry> userCountries = new HashSet<>();
            for (JsonNode countryNode : countryNodes) {
                userCountries.add(countryRepository.findCountryByName(countryNode.asText()));
            }
            return userCountries;
        }
    }

    /**
     * Returns set of Additional email in user creation request
     *
     * @param node
     * @param field
     * @return
     */
    public Set<Email> getAdditionalEmail(JsonNode node, String field) {
        JsonNode emailNodes = node.get(field);
        if (emailNodes == null) {
            return new HashSet<>();
        } else {
            Set<Email> emails = new HashSet<>();
            for (JsonNode emailNode : emailNodes) {
                emails.add(emailRepository.findEmailByString(emailNode.asText()));
            }
            return emails;
        }
    }

    /**
     * Returns set of ActivityTypes in user creation request
     *
     * @param node
     * @param field
     * @return
     */
    public Set<ActivityType> getActivityTypes(JsonNode node, String field) {
        JsonNode activityTypeNodes = node.get(field);
        if (activityTypeNodes == null) {
            return new HashSet<>();
        } else {
            Set<ActivityType> userActivityTypes = new HashSet<>();
            for (JsonNode activityTypeNode : activityTypeNodes) {
                userActivityTypes.add(activityTypeRepository.findActivityTypeByName(activityTypeNode.asText()));
            }
            return userActivityTypes;
        }
    }
}
