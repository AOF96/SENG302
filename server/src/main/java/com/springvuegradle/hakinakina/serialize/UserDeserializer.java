package com.springvuegradle.hakinakina.serialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.repository.ActivityTypeRepository;
import com.springvuegradle.hakinakina.repository.EmailRepository;
import com.springvuegradle.hakinakina.repository.LocationRepository;
import com.springvuegradle.hakinakina.repository.PassportCountryRepository;
import com.springvuegradle.hakinakina.util.ParserHelper;
import com.springvuegradle.hakinakina.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springvuegradle.hakinakina.util.ParserHelper.*;

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
        String lastName = ParserHelper.getValueString(node, "lastname");
        String firstName = ParserHelper.getValueString(node, "firstname");
        String primaryEmail = ParserHelper.getValueString(node, "primary_email");
        String password = ParserHelper.getValueString(node, "password");
        String dateOfBirth = ParserHelper.getValueString(node, "date_of_birth");
        int fitnessLevel = ParserHelper.getValueInt(node, "fitness");
        // Get gender
        String genderString = ParserHelper.getValueString(node, "gender");
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
        String middleName = ParserHelper.getValueString(node, "middlename");
        String nickName = ParserHelper.getValueString(node, "nickname");
        String bio = ParserHelper.getValueString(node, "bio");
        int permissionLevel = ParserHelper.getValueInt(node, "permission_level");
        // Get passport countries
        Set<PassportCountry> userCountries = getPassportCountries(node, "passports");
        Set<Email> additionalEmail = getAdditionalEmail(node, "additional_email");

        // Create user with compulsory attributes
        User user = new User(firstName, lastName, primaryEmail, dateOfBirth, gender, fitnessLevel, password);

        // Add additional attributes
        for (PassportCountry country : userCountries) {
            user.addPassportCountry(country);
        }
        for (Email email : additionalEmail) {
            user.addEmail(email);
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
            Location location = ParserHelper.createLocation(node.get("location"));
            user.setLocation(location);
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
}
