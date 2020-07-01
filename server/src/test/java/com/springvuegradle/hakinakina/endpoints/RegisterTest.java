package com.springvuegradle.hakinakina.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.hakinakina.dto.request.CreateProfileRequest;
import com.springvuegradle.hakinakina.entity.Gender;
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
@ActiveProfiles("test")
public class RegisterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserRepository userRepo;

    @Autowired
    SessionRepository sessionRepo;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateProfileRequest cpr;

    @BeforeEach
    void createUserDTO() {

        // create DTO
        cpr = new CreateProfileRequest();
        cpr.setLastName("Williams");
        cpr.setFirstName("Mayuko");
        cpr.setMiddleName("007");
        cpr.setNickname("Bond");
        cpr.setPrimaryEmail("mwi@williams.com");
        cpr.setPassword("Super123");
        cpr.setBio("ryu ga gotoku");

        String str="2000-03-31";
        java.sql.Date date = java.sql.Date.valueOf(str);
        cpr.setBirthDate(date);

        Gender g = Gender.FEMALE;
        cpr.setGender(g);

        cpr.setFitness(1);

        // delete any user that has same email primary email as DTO above
        User temp = userRepo.findUserByEmail("mwi@williams.com");
        if (temp != null) {
            sessionRepo.removeTokenByUserId(String.valueOf(temp.getUserId()));
            userRepo.deleteById(temp.getUserId());
        }


    }

    @Test
    void testRegistrationOk() throws Exception {

        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(cpr)))
                .andExpect(status().isCreated());
    }


    @Test
    void testRegistrationWithoutFirstnameShouldFail() throws Exception {
        cpr.setFirstName("");
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cpr)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegistrationWithoutLastnameShouldFail() throws Exception {
        cpr.setLastName("");
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cpr)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegistrationWithoutPasswordShouldFail() throws Exception {
        cpr.setPassword("");
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cpr)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegistrationEmptyEmailShouldFail() throws Exception {
        cpr.setPrimaryEmail("");
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cpr)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegistrationInvalidEmailDomainShouldFail() throws Exception {
        cpr.setPrimaryEmail("mememe");
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cpr)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegistrationWithInvalidEmailFormatShouldFail() throws Exception {
        cpr.setPrimaryEmail("meow.com");
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cpr)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegistrationWithInvalidDateOfBirth() throws Exception {
        Date now = new Date();
        java.sql.Date dateOfBirth = new java.sql.Date(now.getTime());
        cpr.setBirthDate((java.sql.Date) dateOfBirth);
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cpr)))
                .andExpect(status().isBadRequest());
    }

}
