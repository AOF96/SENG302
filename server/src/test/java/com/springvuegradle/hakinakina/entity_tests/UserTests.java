package com.springvuegradle.hakinakina.entity_tests;

import com.springvuegradle.hakinakina.repository.LocationRepository;
import com.springvuegradle.hakinakina.service.UserService;
import com.springvuegradle.hakinakina.entity.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserTests {

    @Autowired
    UserService userService;
    @Autowired
    public LocationRepository locationRepository;

    @Test
    public void testCreatingNewUser() {
        User testUser = new User("Maurice", "Benson", "jacky@google.com",
                "1985-12-20", Gender.MALE, 3,
                "jacky'sSecuredPwd");

        assertNull(testUser.getUserId());
        assertEquals("1985-12-20", testUser.getBirthDate().toString());
    }

    @Test
    public void testNewUserHasEncryptedPassword() {
        User testUser = new User("Maurice", "Benson", "jacky@google.com",
                "1985-12-20", Gender.MALE, 3,
                "jacky'sSecuredPwd");

        assertNotEquals("jacky'sSecuredPwd", testUser.getPassword());
    }

    @Test
    public void testAddingPassportCountriesToUser() {
        User testUser = new User("Maurice", "Benson", "jacky@google.com",
                "1985-12-20", Gender.MALE, 3,
                "jacky'sSecuredPwd");
        testUser.addPassportCountry(new PassportCountry("NZ", "New Zealand"));
        testUser.addPassportCountry(new PassportCountry("US", "United States"));

        ArrayList<String> countries = new ArrayList<>();
        for (PassportCountry country : testUser.getPassportCountries()) {
            countries.add(country.getName());
        }
        Collections.sort(countries);

        assertEquals("[New Zealand, United States]", countries.toString());
    }

    @Test
    public void testAddingEmailsToUser() {
        User testUser = new User("Maurice", "Benson", "jacky@google.com",
                "1985-12-20", Gender.MALE, 3,
                "jacky'sSecuredPwd");
        testUser.addEmail(new Email("jacky@hotmail.com"));
        testUser.addEmail(new Email("jacky@anothermail.com"));

        ArrayList<String> emails = new ArrayList<>();
        for (Email email : testUser.getEmails()) {
            emails.add(email.getEmail());
        }
        Collections.sort(emails);

        assertEquals("[jacky@anothermail.com, jacky@hotmail.com]", emails.toString());
    }

    @Test
    public void testEmailFormatCheck() {

        String correctEmail = "mwi67@uclive.ac.nz";
        assertTrue(userService.isEmailProperlyFormatted(correctEmail));

        String wrongEmail = "me@you";
        assertFalse(userService.isEmailProperlyFormatted(wrongEmail));

    }

    @Test
    public void testPermissionLevelOfNewUser() {

        User testUser = new User("Maurice", "Benson", "jacky@google.com",
                "1985-12-20", Gender.MALE, 3,
                "jacky'sSecuredPwd");

        assertEquals(0, testUser.getPermissionLevel());
    }


    @Test
    public void testAddedLocationToNewUser() {

        User testUser = new User("Maurice", "Benson", "jacky@google.com",
                "1985-12-20", Gender.MALE, 3,
                "jacky'sSecuredPwd");

        testUser.setCity("Houston");
        testUser.setState("Texas");
        testUser.setCountry("U.S.A");
        assertEquals("Houston", testUser.getCity());
        assertEquals("Texas", testUser.getState());
        assertEquals("U.S.A", testUser.getCountry());
    }

    @Test
    public void testOverrideNotEqualsMethod() {
        User testUser = new User("Maurice", "Benson", "jacky@google.com",
                "1985-12-20", Gender.MALE, 3,
                "jacky'sSecuredPwd");

        testUser.setUserId(1212L);
        User testUser2 = new User("Maurice", "Benson", "jacky12@google.com",
                "1985-12-20", Gender.MALE, 3,
                "jacky'sSecuredPwd");

        testUser2.setUserId(9898L);
        assertFalse(testUser.equals(testUser2));
    }

    @Test
    public void testOverrideEqualsMethod() {
        User testUser = new User("Maurice", "Benson", "jacky@google.com",
                "1985-12-20", Gender.MALE, 3,
                "jacky'sSecuredPwd");

        testUser.setUserId(1212L);
        User testUser2 = new User("Maurice", "Benson", "jacky@google.com",
                "1985-12-20", Gender.MALE, 3,
                "jacky'sSecuredPwd");

        testUser2.setUserId(1212L);
        assertTrue(testUser.equals(testUser2));
    }

    @Test
    public void testUserCurrentLocation(){
        Location testLocation = new Location("University of Canterbury", "Upper Riccarton",
                "Christchurch", 8041, "Canterbury", "New Zealand",
                -43.522447, 172.579442);
        testLocation.setId(1L);
        locationRepository.save(testLocation);

        User testUser2 = new User("James", "Shaw", "james@google.com",
                "1985-12-20", Gender.MALE, 3,
                "james'sSecuredPwd");

        testUser2.setUserId(1212L);
        testUser2.setCurrentLocation(testLocation);
        assertTrue(testUser2.getCurrentLocation().equals(testLocation));
        assertTrue(testUser2.getCurrentLocation().getSuburb().equals("Upper Riccarton"));

    }

    @Test
    public void testUserHomeLocation(){
        Location testLocation = new Location("University of Canterbury", "Upper Riccarton",
                "Christchurch", 8041, "Canterbury", "New Zealand",
                -43.522447, 172.579442);
        testLocation.setId(1L);
        locationRepository.save(testLocation);

        User testUser2 = new User("James", "Shaw", "james@google.com",
                "1985-12-20", Gender.MALE, 3,
                "james'sSecuredPwd");

        testUser2.setUserId(1212L);
        testUser2.setHomeLocation(testLocation);
        assertTrue(testUser2.getHomeLocation().equals(testLocation));
        assertEquals(testUser2.getHomeLocation().getLatitude(),-43.522447);
        assertEquals(testUser2.getHomeLocation().getLongitude(),172.579442);
        assertTrue(testUser2.getHomeLocation().getCity().equals("Christchurch"));
        assertTrue(testUser2.getHomeLocation().getState().equals("Canterbury"));
        assertTrue(testUser2.getHomeLocation().getCountry().equals("New Zealand"));
        assertTrue(testUser2.getHomeLocation().getStreetAddress().equals("University of Canterbury"));

    }

}
