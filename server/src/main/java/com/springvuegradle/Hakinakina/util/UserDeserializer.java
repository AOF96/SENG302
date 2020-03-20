package com.springvuegradle.Hakinakina.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.springvuegradle.Hakinakina.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserDeserializer extends StdDeserializer<User> {

    @Autowired
    PassportCountryRepository countryRepository;

    @Autowired
    ActivityTypeRepository activityTypeRepository;

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
        Integer fitnessLevel = getValueInt(node, "fitness");
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
            case ("non_binary"):
                gender = Gender.NON_BINARY;
                break;
        }

        // Get other attributes if they exist
        String middleName = getValueString(node, "middlename");
        String nickName = getValueString(node, "nickname");
        String bio = getValueString(node, "bio");
        // Get passport countries
        Set<PassportCountry> userCountries = getPassportCountries(node, "passports");
        Set<ActivityType> activityTypes = getActivityTypes(node, "activity_types");

        // Create user with compulsory attributes
        User user = new User(firstName, lastName, primaryEmail, dateOfBirth, gender, fitnessLevel, password);

        // Add additional attributes
        for (PassportCountry country : userCountries) {
            user.addPassportCountry(country);
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

        return user;
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
        } else {
            return fieldValue.asText();
        }
    }

    /**
     * Returns value of field if it exists
     *
     * @param node
     * @param field
     * @return int value or null
     */
    public Integer getValueInt(JsonNode node, String field) {
        JsonNode fieldValue = node.get(field);
        if (fieldValue == null) {
            return -1;
        } else {
            return fieldValue.asInt();
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
