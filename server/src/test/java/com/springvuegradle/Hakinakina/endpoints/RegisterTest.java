package com.springvuegradle.Hakinakina.endpoints;

import com.springvuegradle.Hakinakina.entity.Gender;
import com.springvuegradle.Hakinakina.entity.SessionRepository;
import com.springvuegradle.Hakinakina.entity.User;
import com.springvuegradle.Hakinakina.entity.UserRepository;
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
@ActiveProfiles("test")
public class RegisterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserRepository userRepo;

    @Autowired
    SessionRepository sessionRepo;

    private User u;

    @BeforeEach
    void createUser() {
        u = new User("Mayuko", "Williams",
                "mwi@williams.com", "1970-01-01", Gender.FEMALE,
                3, "P@ssw0rd!123");
        User temp = userRepo.findUserByEmail("mwi@williams.com");
        if (temp != null) {
            sessionRepo.removeTokenByUserId(String.valueOf(temp.getUserId()));
            userRepo.deleteById(temp.getUserId());
        }


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

}
