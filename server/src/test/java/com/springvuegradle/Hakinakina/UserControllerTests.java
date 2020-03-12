package com.springvuegradle.Hakinakina;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.Hakinakina.entity.*;
import com.springvuegradle.Hakinakina.util.EncryptionUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PassportCountryRepository passportCountryRepository;

    @Autowired
    private EmailRepository emailRepository;

    @BeforeEach
    public void resetRepositories() {
        userRepository.deleteAll();
        emailRepository.deleteAll();
    }

    @Test
    public void createProfileTest() throws Exception {
        String json = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"password\": \"jacky'sSecuredPwd\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": \"3\"\n"  +
                "}";

        this.mockMvc.perform(post("/profiles").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().is(201))
                .andExpect(content().string("User created"));
    }

    @Test
    public void cannotCreateProfileWithExistingEmailTest() throws Exception {
        //TODO Update test
        String json = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"password\": \"jacky'sSecuredPwd\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": \"3\"\n"  +
                "}";

        User user = new User("Maurice", "Benson", "jacky@google.com", "1985-12-20", Gender.MALE,
                2, "jacky'sSecuredPwd");
        userRepository.save(user);
        this.mockMvc.perform(post("/profiles").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.StatusCode").value("403"))
                .andExpect(jsonPath("$.Errors").value("Email already exists"));
    }

//    @Test
//    public void getAllUsersTest() throws Exception {
//        //TODO Update test
//        User user = new User("Maurice", "Benson", "jacky@google.com", "1985-12-20", Gender.MALE,
//                2, "jacky'sSecuredPwd");
//        userRepository.save(user);
//
//        this.mockMvc.perform(get("/profiles"))
//                .andExpect(jsonPath("$.Users").value("1 Maurice Benson"));
//
//        User user2 = new User("John", "Smith", "jacky2@google.com", "1985-12-20", Gender.MALE,
//                2, "jacky'sSecuredPwd");
//        user2.setUser_id((long) 2);
//        userRepository.save(user2);
//
//        ArrayList<String> expected = new ArrayList<>();
//        expected.add("1 Maurice Benson");
//        expected.add("2 John Smith");
//
//        this.mockMvc.perform(get("/profiles"))
//                .andExpect(jsonPath("$.Users").value(expected));
//    }

    @Test
    public void getOneUserTest() throws Exception {
        User user = new User("Maurice", "Benson", "jacky@google.com", "1985-12-20", Gender.MALE,
                2, "jacky'sSecuredPwd");
        userRepository.save(user);

        this.mockMvc.perform(get("/profiles/" + user.getUser_id()))
                .andExpect(jsonPath("$.firstname").value("Maurice"));
    }

    @Test
    public void getAllEmailsTest() throws Exception {
        Email email = new Email("email@gmail.com");
        emailRepository.save(email);

        this.mockMvc.perform(get("/emails"))
                .andExpect(content().string("[{\"email\":\"email@gmail.com\",\"user\":null}]"));
    }

    @Test
    public void loginCheckTest() throws Exception {
        User user = new User("Maurice", "Benson", "jacky@google.com", "1985-12-20", Gender.MALE,
                2, "123");
        userRepository.save(user);
        String json = "{\n" +
                "  \"email\": \"jacky@google.com\",\n" +
                "  \"password\": \"123\"" +
                "}";

        this.mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().is(201));
    }

    @Test
    public void changePasswordTest() throws Exception {
        User user = new User("Maurice", "Benson", "jacky@google.com", "1985-12-20", Gender.MALE,
                2, "jacky'sSecuredPwd");
        userRepository.save(user);

        String oldPassword = user.getPassword();
        long id = user.getUser_id();
        String json = "{" +
                "\n\"profile_id\": " + id + "," +
                "\n\"old_password\": \"myoldpwd\"," +
                "\n\"new_password\": \"mynewpwd\"," +
                "\n\"repeat_password\": \"mynewpwd\"" +
                "\n}";

        this.mockMvc.perform(post("/editpassword").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().is(200))
                .andExpect(content().string("Successfully changed the password"));

        User userUpdated = userRepository.findUserByEmail("jacky@google.com");
        assertEquals(userUpdated.getPassword(), EncryptionUtil.getEncryptedPassword("mynewpwd", userUpdated.getSalt()));
        assertNotEquals(userUpdated.getPassword(), oldPassword);
    }

    @Test
    public void changePasswordWithNoUserWithIDTest() throws Exception {
        User user = new User("Maurice", "Benson", "jacky@google.com", "1985-12-20", Gender.MALE,
                2, "jacky'sSecuredPwd");
        userRepository.save(user);

        String json = "{" +
                "\n\"profile_id\": 1000," +
                "\n\"old_password\": \"myoldpwd\"," +
                "\n\"new_password\": \"mynewpwd\"," +
                "\n\"repeat_password\": \"mynewpwd\"" +
                "\n}";

        this.mockMvc.perform(post("/editpassword").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.StatusCode").value("400"))
                .andExpect(jsonPath("$.Errors").value("No user with that ID"));
    }

    @Test
    public void createUserNoFirstName() throws Exception {
        String json = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"password\": \"jacky'sSecuredPwd\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": \"3\"\n"  +
                "}";

        this.mockMvc.perform(post("/profiles").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.StatusCode").value("400"))
                .andExpect(jsonPath("$.Errors").value("Please provide your full name. First and last names are required."));
    }

    @Test
    public void createUserNoMiddleName() throws Exception {
        String json = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Jack\",\n" +
                "  \"middlename\": \"\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"password\": \"jacky'sSecuredPwd\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": \"3\"\n"  +
                "}";

        this.mockMvc.perform(post("/profiles").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().is(201));
    }

    @Test
    public void createUserNoLastName() throws Exception {
        String json = "{\n" +
                "  \"lastname\": \"\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"password\": \"jacky'sSecuredPwd\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": \"3\"\n"  +
                "}";

        this.mockMvc.perform(post("/profiles").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.StatusCode").value("400"))
                .andExpect(jsonPath("$.Errors").value("Please provide your full name. First and last names are required."));
    }

    @Test
    public void createUserNoEmail() throws Exception {
        String json = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"\",\n" +
                "  \"password\": \"jacky'sSecuredPwd\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": \"3\"\n"  +
                "}";

        this.mockMvc.perform(post("/profiles").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.StatusCode").value("400"))
                .andExpect(jsonPath("$.Errors").value("Please provide a valid email."));
    }

    @Test
    public void createUserNoDateOfBirth() throws Exception {
        String json = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"password\": \"jacky'sSecuredPwd\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": \"3\"\n"  +
                "}";

        this.mockMvc.perform(post("/profiles").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.StatusCode").value("400"))
                .andExpect(jsonPath("$.Errors").value("Please provide a valid date of birth, yyyy-mm-dd."));
    }

    @Test
    public void createUserNoGender() throws Exception {
        String json = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"password\": \"jacky'sSecuredPwd\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"\",\n" +
                "  \"fitness\": \"3\"\n"  +
                "}";

        this.mockMvc.perform(post("/profiles").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.StatusCode").value("400"))
                .andExpect(jsonPath("$.Errors").value("Please provide a valid gender. male, female or non-binary."));
    }

    @Test
    public void editUserNoFirstName() throws Exception {
        User editNoFirstNameTest = new User("Maurice", "Benson", "jacky@google.com", "1985-12-20", Gender.MALE,
                2, "jacky'sSecuredPwd");
        editNoFirstNameTest.setMiddleName("Jack");
        userRepository.save(editNoFirstNameTest);

        String json = "{\n" +
                "  \"profile_id\": \"editNoFirstNameTest.getUser_id()\",\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"password\": \"jacky'sSecuredPwd\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": \"3\"\n"  +
                "}";

        this.mockMvc.perform(put("/profiles/"  + editNoFirstNameTest.getUser_id()).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.StatusCode").value("403"))
                .andExpect(jsonPath("$.Errors").value("You cannot delete required fields. Please provide you're full name. First, middle and last names are required."));
    }

    @Test
    public void editUserNoLastName() throws Exception {
        User editNoLastNameTest = new User("Maurice", "Benson", "jacky@google.com", "1985-12-20", Gender.MALE,
                2, "jacky'sSecuredPwd");
        editNoLastNameTest.setMiddleName("Jack");
        userRepository.save(editNoLastNameTest);

        String json = "{\n" +
                "  \"profile_id\": \"editNoLastNameTest.getUser_id()\",\n" +
                "  \"lastname\": \"\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"password\": \"jacky'sSecuredPwd\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": \"3\"\n"  +
                "}";

        this.mockMvc.perform(put("/profiles/" + editNoLastNameTest.getLastName()).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.StatusCode").value("403"))
                .andExpect(jsonPath("$.Errors").value("You cannot delete required fields. Please provide you're full name. First, middle and last names are required."));
    }

    @Test
    public void editUserNoEmail() throws Exception {
        //TODO Update test
        User editNoEmailTest = new User("Maurice", "Benson", "jacky@google.com", "1985-12-20", Gender.MALE,
                2, "jacky'sSecuredPwd");
        editNoEmailTest.setMiddleName("Jack");
        userRepository.save(editNoEmailTest);

        String json = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"\",\n" +
                "  \"password\": \"jacky'sSecuredPwd\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": \"3\"\n"  +
                "}";

        this.mockMvc.perform(put("/profiles/1" + editNoEmailTest.getPrimaryEmail()).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.StatusCode").value("403"))
                .andExpect(jsonPath("$.Errors").value("You cannot delete required fields. Please provide a valid email."));
    }

    @Test
    public void editUserNoDateOfBirth() throws Exception {
        User editNoDateOfBirthTest = new User("Maurice", "Benson", "jacky@google.com", "1985-12-20", Gender.MALE,
                2, "jacky'sSecuredPwd");
        editNoDateOfBirthTest.setMiddleName("Jack");
        userRepository.save(editNoDateOfBirthTest);

        String json = "{\n" +
                "  \"profile_id\": \"editNoDateOfBirthTest.getUser_id()\",\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"password\": \"jacky'sSecuredPwd\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": \"3\"\n"  +
                "}";

        this.mockMvc.perform(put("/profiles/" + editNoDateOfBirthTest.getBirthDate()).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.StatusCode").value("403"))
                .andExpect(jsonPath("$.Errors").value("You cannot delete required fields. Please provide a valid date of birth, yyyy-mm-dd."));
    }

    @Test
    public void editUserNoGender() throws Exception {
        User editNoGenderTest = new User("Maurice", "Benson", "jacky@google.com", "1985-12-20", Gender.MALE,
                2, "jacky'sSecuredPwd");
        editNoGenderTest.setMiddleName("Jack");
        userRepository.save(editNoGenderTest);

        String json = "{\n" +
                "  \"profile_id\": \"editNoGenderTest.getUser_id()\",\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"password\": \"jacky'sSecuredPwd\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"\",\n" +
                "  \"fitness\": \"3\"\n" +
                "}";

        this.mockMvc.perform(put("/profiles/" + editNoGenderTest.getGender()).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.StatusCode").value("403"))
                .andExpect(jsonPath("$.Errors").value("You cannot delete required fields. Please provide a valid gender. male, female or non-binary."));
    }
}