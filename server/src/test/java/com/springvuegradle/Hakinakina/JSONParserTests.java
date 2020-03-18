package com.springvuegradle.Hakinakina;

import com.springvuegradle.Hakinakina.entity.User;
import com.springvuegradle.Hakinakina.entity.Gender;
import com.springvuegradle.Hakinakina.util.JSONParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.sql.Date;

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

        Assertions.assertEquals(testUser.getFirstName(), newUser.getFirstName());
        Assertions.assertEquals(testUser.getLastName(), newUser.getLastName());
        Assertions.assertEquals(testUser.getGender(), newUser.getGender());
        Assertions.assertEquals(testUser.getBirthDate().toString(), newUser.getBirthDate().toString());
    }
}
