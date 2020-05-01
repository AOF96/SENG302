package com.springvuegradle.Hakinakina;

import com.springvuegradle.Hakinakina.entity.PassportCountry;
import com.springvuegradle.Hakinakina.entity.User;
import com.springvuegradle.Hakinakina.entity.Gender;
import com.springvuegradle.Hakinakina.util.JSONParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JSONParserTests {

    @Test
    public void testParsingNewUser() {
        String request = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"email\": \"jacky@google.com\",\n" +
                "  \"password\": \"jacky'sSecuredPwd\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": \"3\"\n"  +
                "}";
        User testUser = new User("Maurice", "Benson", "jacky@google.com",
                "1985-12-20", Gender.MALE, 3,
                "jacky'sSecuredPwd");

        JSONParser parser = new JSONParser();
        User newUser = parser.createProfile(request);

        assertEquals(testUser.getFirstName(), newUser.getFirstName());
        assertEquals(testUser.getLastName(), newUser.getLastName());
        assertEquals(testUser.getGender(), newUser.getGender());
        assertEquals(testUser.getBirthDate().toString(), newUser.getBirthDate().toString());
    }

    @Test
    public void testParsingUserToJson() {
        User testUser = new User("Maurice", "Benson", "jacky@google.com",
                "1985-12-20", Gender.MALE, 3,
                "jacky'sSecuredPwd");
        testUser.setUserId((long)1);
        testUser.setBio("Hi there");
        testUser.addPassportCountry(new PassportCountry("NZ", "New Zealand"));

        String expectedJSON = "{\"bio\":\"Hi there\",\"profile_id\":1,\"firstname\":\"Maurice\",\"lastname\"" +
                ":\"Benson\",\"middlename\":null,\"gender\":\"Male\",\"nickname\":null,\"date_of_birth\":\"1985-12-20\"" +
                ",\"fitness\":3,\"passports\":[\"New Zealand\"],\"primary_email\":\"jacky@google.com\",\"additional_email\":[],\"permission_level\":0}";

        assertEquals(expectedJSON, testUser.toJson());
    }
}
