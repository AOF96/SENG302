package com.springvuegradle.Hakinakina;

import com.springvuegradle.Hakinakina.entity.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EmailTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailRepository emailRepository;

    @BeforeEach
    @AfterEach
    public void resetRepositories() {
        userRepository.deleteAll();
        emailRepository.deleteAll();
    }


    @Test
    public void testInsertingSecondaryEmail() {
        Email email = new Email("johnsmith@gmail.com");
        emailRepository.save(email);

        long millis=System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        User user = new User("John", "Smith", "email@email.com", Date.valueOf("1985-12-20"), Gender.MALE, 3,
                "JohnS");
        userRepository.save(user);

        user.addEmail(email);
        userRepository.save(user);

        assertEquals("[johnsmith@gmail.com]", user.getEmails().toString());
    }

    @Test
    public void insertingEmptySecondaryEmail() throws Exception {
        long millis=System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        User user = new User("John", "Smith", "email@email.com", Date.valueOf("1985-12-20"), Gender.MALE, 3,
                "JohnS");
        userRepository.save(user);

        String json = "{" +
                "\n\"primary_email\": \"email@email.com\"," +
                "\n\"additional_email\": [\"\"]" +
                "\n}";

        this.mockMvc.perform(put("/profiles/" + user.getUser_id() + "/emails").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.StatusCode").value("400"))
                .andExpect(jsonPath("$.Errors").value("No emails successfully updated, emails either in use or empty"));
    }

    @Test
    public void insertingAlreadyExistingEmail() throws Exception {
        Email email = new Email("johnsmith@gmail.com");
        emailRepository.save(email);

        long millis=System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        User user = new User("John", "Smith", "email@email.com", Date.valueOf("1985-12-20"), Gender.MALE, 3,
                "JohnS");
        userRepository.save(user);

        String json = "{" +
                "\n\"primary_email\": \"email@email.com\"," +
                "\n\"additional_email\": [\"johnsmith@gmail.com\"]" +
                "\n}";

        this.mockMvc.perform(put("/profiles/" + user.getUser_id() + "/emails").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.StatusCode").value("400"))
                .andExpect(jsonPath("$.Errors").value("No emails successfully updated, emails either in use or empty"));
    }

    @Test
    public void insertingOneValidOneAlreadyExistingEmail() throws Exception {
        Email email = new Email("johnsmith@gmail.com");
        emailRepository.save(email);

        long millis=System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        User user = new User("John", "Smith", "email@email.com", Date.valueOf("1985-12-20"), Gender.MALE, 3,
                "JohnS");
        userRepository.save(user);

        String json = "{" +
                "\n\"primary_email\": \"email@email.com\"," +
                "\n\"additional_email\": [\"johnsmith@gmail.com\", \"emails@emails.co.nz\"]" +
                "\n}";

        this.mockMvc.perform(put("/profiles/" + user.getUser_id() + "/emails").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.StatusCode").value("200"))
                .andExpect(jsonPath("$.Content").value("Secondary emails successfully added: emails@emails.co.nz, "));
    }
    @Test
    public void insertingOneValidOneEmpty() throws Exception {
        long millis=System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        User user = new User("John", "Smith", "email@email.com", Date.valueOf("1985-12-20"), Gender.MALE, 3,
                "JohnS");
        userRepository.save(user);

        String json = "{" +
                "\n\"primary_email\": \"email@email.com\"," +
                "\n\"additional_email\": [\"\", \"emailss@emailss.co.nz\"]" +
                "\n}";

        this.mockMvc.perform(put("/profiles/" + user.getUser_id() + "/emails").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.StatusCode").value("200"))
                .andExpect(jsonPath("$.Content").value("Secondary emails successfully added: emailss@emailss.co.nz, "));
    }
}
