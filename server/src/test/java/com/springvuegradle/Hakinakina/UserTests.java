package com.springvuegradle.Hakinakina;

import com.springvuegradle.Hakinakina.entity.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class UserTests {

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
}
