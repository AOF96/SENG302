package com.springvuegradle.hakinakina.endpoints;

import com.springvuegradle.hakinakina.entity.Gender;
import com.springvuegradle.hakinakina.entity.Location;
import com.springvuegradle.hakinakina.repository.ActivityRepository;
import com.springvuegradle.hakinakina.repository.SessionRepository;
import com.springvuegradle.hakinakina.entity.User;
import com.springvuegradle.hakinakina.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RegisterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserRepository userRepo;

    @Autowired
    SessionRepository sessionRepo;

    @Autowired
    ActivityRepository activityRepository;

    private User u;

    @BeforeEach
    void createUser() {

        activityRepository.deleteAll();
        sessionRepo.deleteAll();
        userRepo.deleteAll();


        Location location = new Location("Street", "Suburb", "City", 7201,
                "State", "Country", 0, 0);
        u = new User("Mayuko", "Williams",
                "mwi@williams.com", "1970-01-01", Gender.FEMALE,
                3, "P@ssw0rd!123");
        location.setUser(u);
        u.setLocation(location);
        u.setUserId(-1L);
    }

    @Test
    void testRegistrationOk() throws Exception {
        String input = "{\n" +
                "  \"lastname\": \"Williams\",\n" +
                "  \"firstname\": \"Mayuko\",\n" +
                "  \"middlename\": \"007\",\n" +
                "  \"nickname\": \"MWill\",\n" +
                "  \"primary_email\": \"mwi@williams.com\",\n" +
                "  \"password\": \"SuPeRSeCuReP@sSw0rD!!123\",\n" +
                "  \"bio\": \"bio\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"female\",\n" +
                "  \"fitness\": 3\n" +
                "}";


        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(input))
                .andExpect(status().isCreated());
    }

    @Test
    void testRegistrationWithoutFirstnameShouldFail() throws Exception {
        u.setFirstName("");
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(u.toJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegistrationWithoutLastnameShouldFail() throws Exception {
        u.setLastName("");
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(u.toJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegistrationWithoutPasswordShouldFail() throws Exception {
        u.setEncryptedPassword("");
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(u.toJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegistrationEmptyEmailShouldFail() throws Exception {
        u.setPrimaryEmail("");
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(u.toJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegistrationInvalidEmailDomainShouldFail() throws Exception {
        u.setPrimaryEmail("me@you");
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(u.toJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegistrationWithInvalidEmailFormatShouldFail() throws Exception {
        u.setPrimaryEmail("meow.com");
        System.out.println(u.toJson());
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(u.toJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegistrationWithInvalidDateOfBirth() throws Exception {
        Date now = new Date();
        java.sql.Date dateOfBirth = new java.sql.Date(now.getTime());
        u.setBirthDate((java.sql.Date) dateOfBirth);
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(u.toJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegistrationWithFirstNameWithNumberInItShouldFail() throws Exception {
        u.setFirstName("Fabian123");
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(u.toJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegistrationWithLastNameWithNumberInItShouldFail() throws Exception {
        u.setLastName("Fabian123");
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(u.toJson()))
                .andExpect(status().isBadRequest());
    }
}
