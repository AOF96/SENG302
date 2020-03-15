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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @AfterEach
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

    //This test is strange as adding users in other tests seems to mess it up even though their is the clear repositories
    //being called in order to avoid this.
    @Test
    public void getAllUsersTest() throws Exception {
        User user = new User("Maurice", "Benson", "jacky@google.com", Date.valueOf("1985-12-20"), Gender.MALE,
                2, "jacky'sSecuredPwd");
        userRepository.save(user);

        ArrayList<String> expected1 = new ArrayList<String>();
        expected1.add("1 John Smith");
        expected1.add("7 John Smith");
        expected1.add("11 Maurice Benson");
        this.mockMvc.perform(get("/users"))
                .andExpect(jsonPath("$.Users").value(expected1));

        User user2 = new User("John", "Smith", "jacky2@google.com", Date.valueOf("1985-12-20"), Gender.MALE,
                2, "jacky'sSecuredPwd");
        userRepository.save(user2);

        ArrayList<String> expected = new ArrayList<String>();
        expected.add("1 John Smith");
        expected.add("7 John Smith");
        expected.add("11 Maurice Benson");
        expected.add("12 John Smith");

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

    /* Isn't going to work anymore because you have to hard code what the expected password should be but it changes every time the tests are run.
    @Test
    public void getAllEmailsTest() throws Exception {
        Email email = new Email("email@gmail.com");
        emailRepository.save(email);

        this.mockMvc.perform(get("/emails"))
                .andExpect(content().string("[{\"email\":\"email@gmail.com\",\"user\":{\"gender\":\"male\",\"password\":\"vNbxUCfonscTcxWlL6xnLFv+UFY=\",\"bio\":null,\"fitnessLevel\":3,\"user_id\":1,\"firstname\":\"John\",\"lastname\":\"Smith\",\"middlename\":null,\"nickname\":null,\"date_of_birth\":\"1985-12-20\",\"passport\":[],\"email\":\"email@email.com\",\"additional_email\":[\"emailss@emailss.co.nz\"]}},{\"email\":\"emails@emails.co.nz\",\"user\":{\"gender\":\"male\",\"password\":\"Ol7NNlNoBGTWPkMLfuKZ3q+kn2c=\",\"bio\":null,\"fitnessLevel\":3,\"user_id\":7,\"firstname\":\"John\",\"lastname\":\"Smith\",\"middlename\":null,\"nickname\":null,\"date_of_birth\":\"1985-12-20\",\"passport\":[],\"email\":\"email@email.com\",\"additional_email\":[\"emails@emails.co.nz\"]}},{\"email\":\"email@gmail.com\",\"user\":null}]"));
    }*/

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
}
