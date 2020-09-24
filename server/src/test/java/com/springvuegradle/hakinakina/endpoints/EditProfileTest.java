package com.springvuegradle.hakinakina.endpoints;

import com.springvuegradle.hakinakina.entity.Gender;
import com.springvuegradle.hakinakina.entity.Location;
import com.springvuegradle.hakinakina.entity.Session;
import com.springvuegradle.hakinakina.entity.User;
import com.springvuegradle.hakinakina.repository.SessionRepository;
import com.springvuegradle.hakinakina.repository.UserRepository;
import com.springvuegradle.hakinakina.serialize.UserDeserializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EditProfileTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    SessionRepository sessionRepository;
    @Autowired
    private MockMvc mockMvc;

    private String input;
    private Long userId;

    @BeforeEach
    void setup() {
        Session testSession = new Session("t0k3n");
        User testUser = new User("John", "Smith", "john@gmail.com", null,
                Gender.MALE, 1, "Password1");
        userId = userRepository.save(testUser).getUserId();
        testSession.setUser(testUser);
        sessionRepository.save(testSession);

        input = "{\n" +
                "  \"lastname\": \"Qiu\",\n" +
                "  \"firstname\": \"Jackie\",\n" +
                "  \"middlename\": \"Danger\",\n" +
                "  \"nickname\": \"J\",\n" +
                "  \"primary_email\": \"jqi26@uclive.ac.nz\",\n" +
                "  \"password\": \"Password1\",\n" +
                "  \"bio\": \"bio\",\n" +
                "  \"date_of_birth\": \"%s\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": 2\n" +
                "}";
    }

    @Test
    public void editProfileWithBoundaryLowerDatesShouldSucceed() throws Exception {
        String birthday = "1985-01-20";

        mockMvc.perform(put("/profiles/" + userId)
                .cookie(new Cookie("s_id", "t0k3n"))
                .contentType(MediaType.APPLICATION_JSON).content(String.format(input, birthday)))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("User updated")));
    }

    @Test
    public void editProfileWithBoundaryUpperDatesShouldSucceed() throws Exception {
        String birthday = "1985-12-25";

        mockMvc.perform(put("/profiles/" + userId)
                .cookie(new Cookie("s_id", "t0k3n"))
                .contentType(MediaType.APPLICATION_JSON).content(String.format(input, birthday)))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("User updated")));
    }

    @Test
    public void editProfilePasswordWithNumberUpperLowerCaseShouldSucceed() throws Exception {

        String editPasswordJson = "{\n" +
                "  \"old_password\": \"Password1\",\n" +
                "  \"new_password\": \"%s\",\n" +
                "  \"repeat_password\": \"%s\"\n" +
                "}";

        String editingPassword = "Password2";

        mockMvc.perform(put("/profiles/" + userId + "/password")
                .cookie(new Cookie("s_id", "t0k3n"))
                .contentType(MediaType.APPLICATION_JSON).content(String.format(editPasswordJson, editingPassword, editingPassword)))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("Successfully changed the password")));
    }

    @Test
    public void editProfilePasswordWithoutNumberShouldFail() throws Exception {

        String editPasswordJson = "{\n" +
                "  \"old_password\": \"Password1\",\n" +
                "  \"new_password\": \"%s\",\n" +
                "  \"repeat_password\": \"%s\"\n" +
                "}";

        String editingPassword = "Passwordish";

        mockMvc.perform(put("/profiles/" + userId + "/password")
                .cookie(new Cookie("s_id", "t0k3n"))
                .contentType(MediaType.APPLICATION_JSON).content(String.format(editPasswordJson, editingPassword, editingPassword)))
                .andExpect(status().is(400))
                .andExpect(content().string(containsString("Your password must include at least one number")));
    }

    @Test
    public void editProfilePasswordWithoutUpperCaseLetterShouldFail() throws Exception {

        String editPasswordJson = "{\n" +
                "  \"old_password\": \"Password1\",\n" +
                "  \"new_password\": \"%s\",\n" +
                "  \"repeat_password\": \"%s\"\n" +
                "}";

        String editingPassword = "password123";

        mockMvc.perform(put("/profiles/" + userId + "/password")
                .cookie(new Cookie("s_id", "t0k3n"))
                .contentType(MediaType.APPLICATION_JSON).content(String.format(editPasswordJson, editingPassword, editingPassword)))
                .andExpect(status().is(400))
                .andExpect(content().string(containsString("Your password must include at least one uppercase letter")));
    }

    @Test
    public void editProfilePasswordWithoutLowerCaseLetterShouldFail() throws Exception {

        String editPasswordJson = "{\n" +
                "  \"old_password\": \"Password1\",\n" +
                "  \"new_password\": \"%s\",\n" +
                "  \"repeat_password\": \"%s\"\n" +
                "}";

        String editingPassword = "PASSWORD123";

        mockMvc.perform(put("/profiles/" + userId + "/password")
                .cookie(new Cookie("s_id", "t0k3n"))
                .contentType(MediaType.APPLICATION_JSON).content(String.format(editPasswordJson, editingPassword, editingPassword)))
                .andExpect(status().is(400))
                .andExpect(content().string(containsString("Your password must include at least one lowercase letter")));
    }

    @Test
    public void editProfilePasswordWithJustNumbersShouldFail() throws Exception {

        String editPasswordJson = "{\n" +
                "  \"old_password\": \"Password1\",\n" +
                "  \"new_password\": \"%s\",\n" +
                "  \"repeat_password\": \"%s\"\n" +
                "}";

        String editingPassword = "123456789";

        mockMvc.perform(put("/profiles/" + userId + "/password")
                .cookie(new Cookie("s_id", "t0k3n"))
                .contentType(MediaType.APPLICATION_JSON).content(String.format(editPasswordJson, editingPassword, editingPassword)))
                .andExpect(status().is(400))
                .andExpect(content().string(containsString("Your password must include at least one uppercase letter")));
    }

    @Test
    public void editProfilePasswordWithLessThanEightLettersShouldFail() throws Exception {

        String editPasswordJson = "{\n" +
                "  \"old_password\": \"Password1\",\n" +
                "  \"new_password\": \"%s\",\n" +
                "  \"repeat_password\": \"%s\"\n" +
                "}";

        String editingPassword = "Short2";

        mockMvc.perform(put("/profiles/" + userId + "/password")
                .cookie(new Cookie("s_id", "t0k3n"))
                .contentType(MediaType.APPLICATION_JSON).content(String.format(editPasswordJson, editingPassword, editingPassword)))
                .andExpect(status().is(400))
                .andExpect(content().string(containsString("Your password must be at least 8 letters long.")));
    }
}
