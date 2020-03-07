package com.springvuegradle.Hakinakina;

import com.springvuegradle.Hakinakina.controller.UserController;
import com.springvuegradle.Hakinakina.entity.PassportCountry;
import com.springvuegradle.Hakinakina.entity.PassportCountryRepository;
import com.springvuegradle.Hakinakina.entity.User;
import com.springvuegradle.Hakinakina.entity.UserRepository;
import com.springvuegradle.Hakinakina.util.JSONParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserTests {

    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PassportCountryRepository countryRepository;

    @Test
    public void testCreatingNewUser() {
        JSONParser jsonParser = new JSONParser();
        User testUser = jsonParser.createProfile("{\n" +
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
                "}");
        userRepository.save(testUser);
        long userId = testUser.getUser_id();

        Assertions.assertEquals("Maurice", userRepository.findById(userId).get().getFirstName());
    }

    @Test
    public void testCreatingPassportCountry() {
        PassportCountry country = new PassportCountry("NZ", "New Zealand");
        countryRepository.save(country);
        String countryId = country.getCountryId();

        Assertions.assertEquals("NZ", countryRepository.findCountryByName("New Zealand").getCountryId());
    }

    @Test
    public void testAddingPassportToUser() {
        PassportCountry country = new PassportCountry("NZ", "New Zealand");
        countryRepository.save(country);
        String countryId = country.getCountryId();

        JSONParser jsonParser = new JSONParser();
        User testUser = jsonParser.createProfile("{\n" +
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
                "}");
        userRepository.save(testUser);
        testUser.addPassportCountry(country);
        userRepository.save(testUser);
        long userId = testUser.getUser_id();

        Assertions.assertEquals("[New Zealand]", userRepository.findById(userId).get().getPassportCountries().toString());
    }

    @Test
    public void testAddingMultiplePassportsToUser() {

        JSONParser jsonParser = new JSONParser();
        User testUser = jsonParser.createProfile("{\n" +
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
                "}");
        userRepository.save(testUser);
        testUser.addPassportCountry(countryRepository.findCountryByName("United States"));
        testUser.addPassportCountry(countryRepository.findCountryByName("Thailand"));
        userRepository.save(testUser);
        long userId = testUser.getUser_id();

        ArrayList<String> countries = new ArrayList<>();
        for (PassportCountry passportCountry : userRepository.findById(userId).get().getPassportCountries()) {
            countries.add(passportCountry.getName());
        }
        Collections.sort(countries);

        Assertions.assertEquals("[Thailand, United States]", countries.toString());
    }
}
