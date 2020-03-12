package com.springvuegradle.Hakinakina;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.Hakinakina.entity.*;
import com.springvuegradle.Hakinakina.util.EncryptionUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
                "  \"email\": \"jacky@google.com\",\n" +
                "  \"password\": \"jacky'sSecuredPwd\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": \"3\"\n"  +
                "}";

        this.mockMvc.perform(post("/createprofile").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.StatusCode").value("201"))
                .andExpect(jsonPath("$.Content").value("User created"));
    }

    @Test
    public void cannotCreateProfileWithExistingEmailTest() throws Exception {
        String json = "{\n" +
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

        User user = new User("Maurice", "Benson", "jacky@google.com", Date.valueOf("1985-12-20"), Gender.MALE,
                2, "jacky'sSecuredPwd");
        userRepository.save(user);
        this.mockMvc.perform(post("/createprofile").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.StatusCode").value("400"))
                .andExpect(jsonPath("$.Errors").value("Email already exists"));
    }

    @Test
    public void getAllUsersTest() throws Exception {
        User user = new User("Maurice", "Benson", "jacky@google.com", Date.valueOf("1985-12-20"), Gender.MALE,
                2, "jacky'sSecuredPwd");
        userRepository.save(user);

        this.mockMvc.perform(get("/users"))
                .andExpect(jsonPath("$.Users").value("1 Maurice Benson"));

        User user2 = new User("John", "Smith", "jacky2@google.com", Date.valueOf("1985-12-20"), Gender.MALE,
                2, "jacky'sSecuredPwd");
        userRepository.save(user2);

        ArrayList<String> expected = new ArrayList<String>();
        expected.add("1 Maurice Benson");
        expected.add("2 John Smith");

        this.mockMvc.perform(get("/users"))
                .andExpect(jsonPath("$.Users").value(expected));
    }

    @Test
    public void getOneUserTest() throws Exception {
        User user = new User("Maurice", "Benson", "jacky@google.com", Date.valueOf("1985-12-20"), Gender.MALE,
                2, "jacky'sSecuredPwd");
        userRepository.save(user);

        this.mockMvc.perform(get("/user/" + user.getUser_id()))
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
        User user = new User("Maurice", "Benson", "jacky@google.com", Date.valueOf("1985-12-20"), Gender.MALE,
                2, "123");
        userRepository.save(user);
        String json = "{\n" +
                "  \"email\": \"jacky@google.com\",\n" +
                "  \"attempt\": \"123\"" +
                "}";

        this.mockMvc.perform(get("/checklogin").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.StatusCode").value("200"))
                .andExpect(jsonPath("$.Content").value("Login is correct"));
    }

    @Test
    public void changePasswordTest() throws Exception {
        User user = new User("Maurice", "Benson", "jacky@google.com", Date.valueOf("1985-12-20"), Gender.MALE,
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
            .andExpect(jsonPath("$.StatusCode").value("200"))
            .andExpect(jsonPath("$.Content").value("Successfully changed the password"));

        User userUpdated = userRepository.findUserByEmail("jacky@google.com");
        assertEquals(userUpdated.getPassword(), EncryptionUtil.getEncryptedPassword("mynewpwd", userUpdated.getSalt()));
        assertNotEquals(userUpdated.getPassword(), oldPassword);
    }

    @Test
    public void changePasswordWithNoUserWithIDTest() throws Exception {
        User user = new User("Maurice", "Benson", "jacky@google.com", Date.valueOf("1985-12-20"), Gender.MALE,
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
    public void editUser() throws Exception {
        User user = new User("Maurice", "Benson", "jacky@google.com", Date.valueOf("1985-12-20"), Gender.MALE,
                2, "jacky'sSecuredPwd");
        userRepository.save(user);

        String jsonRequest = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky boi\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": 4,\n" +
                "  \"passports\": [\n" +
                "    \"United States of America\",\n" +
                "    \"Thailand\"\n" +
                "  ]\n" +
                "}";

        long id = userRepository.findUserByEmail("jacky@google.com").getUser_id();

        this.mockMvc.perform(put("/profiles/" + id).contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
                .andExpect(jsonPath("$.StatusCode").value("201"))
                .andExpect(jsonPath("$.Content").value("User updated"));
    }
}
