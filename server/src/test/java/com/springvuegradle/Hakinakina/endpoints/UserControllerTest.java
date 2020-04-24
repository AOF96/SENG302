package com.springvuegradle.Hakinakina.endpoints;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.springvuegradle.Hakinakina.controller.UserController;
import com.springvuegradle.Hakinakina.controller.UserService;
import com.springvuegradle.Hakinakina.entity.*;
import com.springvuegradle.Hakinakina.util.ResponseHandler;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PassportCountryRepository countryRepository;

    @MockBean
    private EmailRepository emailRepository;

    @MockBean
    private SessionRepository sessionRepository;

    @MockBean
    private ActivityTypeRepository activityTypeRepository;

    private ResponseHandler responseHandler = new ResponseHandler();

    private final String EDIT_PROFILE_JSON = "{\n" +
            "  \"lastname\": \"Benson\",\n" +
            "  \"firstname\": \"Maurice\",\n" +
            "  \"middlename\": \"Jack\",\n" +
            "  \"nickname\": \"Jacky\",\n" +
            "  \"primary_email\": \"jacky@google.com\",\n" +
            "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
            "  \"date_of_birth\": \"1985-12-20\",\n" +
            "  \"gender\": \"male\",\n" +
            "  \"fitness\": 4\n" +
            "}";

    @Test
    public void userCreationSuccess() throws Exception {
        String input = "{\n" +
                "  \"lastname\": \"Qiu\",\n" +
                "  \"firstname\": \"Jackie\",\n" +
                "  \"middlename\": \"Danger\",\n" +
                "  \"nickname\": \"J\",\n" +
                "  \"primary_email\": \"jqi26@uclive.ac.nz\",\n" +
                "  \"password\": \"Password1\",\n" +
                "  \"bio\": \"bio\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": 2\n" +
                "}";

        when(service.validateCreateProfile(any(User.class))).thenReturn(responseHandler
                .formatSuccessResponse(200, "User updated"));
        this.mockMvc.perform(post("/profiles").contentType(MediaType.APPLICATION_JSON)
                .content(input)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("User updated")));
    }

    @Test
    public void loginSuccess() throws Exception {
        String input = "{\n" +
                "  \"email\": \"my@email.com\",\n" +
                "  \"password\": \"mysecurepwd\"\n" +
                "}";
        when(service.checkLogin("my@email.com", "mysecurepwd"))
                .thenReturn(new ResponseEntity("{}", HttpStatus.valueOf(201)));
        this.mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                .content(input)).andDo(print()).andExpect(status().is(201));
    }

    @Test
    public void loginFail() throws Exception {
        String input = "{\n" +
                "  \"email\": \"my@email.com\",\n" +
                "  \"password\": \"mysecurepwd\"\n" +
                "}";
        when(service.checkLogin("my@email.com", "mysecurepwd"))
                .thenReturn(new ResponseEntity("Incorrect password", HttpStatus.FORBIDDEN));
        this.mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                .content(input)).andDo(print()).andExpect(status().is(403))
                .andExpect(content().string(containsString("Incorrect password")));
    }

    @Test
    public void logoutSuccess() throws Exception {
        doNothing().when(sessionRepository).removeToken(any(String.class));
        this.mockMvc.perform(post("/logout").cookie(new Cookie("s_id", "1")))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("User logged out")));
    }

    @Test
    public void getAllUsersTest() throws Exception {
        User testUser = new User("John", "Smith", "john@gmail.com", null,
                Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);
        ArrayList<User> testList = new ArrayList<User>();
        testList.add(testUser);
        when(userRepository.findAll()).thenReturn(testList);
        this.mockMvc.perform(get("/profiles"))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("{\n\"Users\": [\n\"1 John Smith\"\n]\n}")));
    }

    @Test
    public void getOneUserSuccessTest() throws Exception {
        User testUser = new User("John", "Smith", "john@gmail.com", null,
                Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(testUser));
        this.mockMvc.perform(get("/profiles/1"))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("{\"bio\":null,\"profile_id\":1,\"firstname\":" +
                        "\"John\",\"lastname\":\"Smith\",\"middlename\":null,\"gender\":\"Male\",\"nickname\":null," + "" +
                        "\"date_of_birth\":null,\"fitness\":2,\"passports\":[],\"activity_types\":[],\"primary_email\":\"john@gmail.com\"," +
                        "\"additional_email\":[]}")));
    }

    @Test
    public void getOneUserFailTest() throws Exception {
        when(userRepository.findById((long) 1)).thenReturn(Optional.empty());
        this.mockMvc.perform(get("/profiles/1"))
                .andExpect(status().is(404))
                .andExpect(content().string(containsString("User does not exist")));
    }

    @Test
    public void getAllCountriesTest() throws Exception {
        PassportCountry country1 = new PassportCountry("NZ", "New Zealand");
        PassportCountry country2 = new PassportCountry("AU", "Australia");
        ArrayList<PassportCountry> countries = new ArrayList<>();
        countries.add(country1);
        countries.add(country2);
        when(countryRepository.findAll()).thenReturn(countries);
        this.mockMvc.perform(get("/countries"))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("[New Zealand, Australia]")));
    }

    @Test
    public void editEmailTest() throws Exception {
        String input = "{\n" +
                "     *   \"primary_email\": \"triplej@google.com\",\n" +
                "     *   \"additional_email\": [\n" +
                "     *     \"triplej@xtra.co.nz\",\n" +
                "     *     \"triplej@msn.com\"\n" +
                "     *   ]\n" +
                "     * }";
        when(service.editEmail(any(String.class), any(Long.class), any(String.class))).thenReturn(
                responseHandler.formatSuccessResponse(200, "Primary email switched successfully"));
        this.mockMvc.perform(put("/profiles/1/emails").cookie(new Cookie("s_id", "1"))
                .contentType(MediaType.APPLICATION_JSON).content(input))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("Primary email switched successfully")));
    }

    @Test
    public void addEmailsTest() throws Exception {
        String input = "{\n" +
                "     *   \"primary_email\": \"triplej@google.com\",\n" +
                "     *   \"additional_email\": [\n" +
                "     *     \"triplej@xtra.co.nz\",\n" +
                "     *     \"triplej@msn.com\"\n" +
                "     *   ]\n" +
                "     * }";
        when(service.addEmails(any(String.class), any(Long.class), any(String.class))).thenReturn(
                responseHandler.formatSuccessResponse(201, "New emails successfully added"));
        this.mockMvc.perform(post("/profiles/1/emails").cookie(new Cookie("s_id", "1"))
                .contentType(MediaType.APPLICATION_JSON).content(input))
                .andExpect(status().is(201))
                .andExpect(content().string(containsString("New emails successfully added")));
    }

    @Test
    public void getAllEmailsTest() throws Exception {
        ArrayList<String> emails = new ArrayList<>();
        emails.add("john@mail.com");
        emails.add("jane@mail.com");
        when(userRepository.getAllPrimaryEmails()).thenReturn(emails);
        this.mockMvc.perform(get("/emails"))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("[john@mail.com, jane@mail.com]")));
    }

    @Test
    public void editUserSuccessTest() throws Exception {
        Session testSession = new Session("t0k3n");
        User testUser = new User("John", "Smith", "john@gmail.com", null,
                Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);
        testUser.resetPassportCountries();
        testSession.setUser(testUser);

        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(testSession);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(testUser));
        when(service.validateEditUser(any(User.class)))
                .thenReturn(responseHandler.formatSuccessResponse(200, "User updated"));
        this.mockMvc.perform(put("/profiles/1").cookie(new Cookie("s_id", "t0k3n"))
                .contentType(MediaType.APPLICATION_JSON).content(EDIT_PROFILE_JSON))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("User updated")));
    }

    @Test
    public void editUserTokenDoesNotMatchAnySessionTest() throws Exception {
        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(null);
        this.mockMvc.perform(put("/profiles/1").cookie(new Cookie("s_id", "t0k3n"))
                .contentType(MediaType.APPLICATION_JSON).content(EDIT_PROFILE_JSON))
                .andExpect(status().is(400))
                .andExpect(content().string(containsString("Invalid Session")));
    }

    @Test
    public void editUserTokenMismatchTest() throws Exception {
        Session testSession = new Session("t0k3n");
        User testUser = new User("John", "Smith", "john@gmail.com", null,
                Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);
        testUser.resetPassportCountries();
        testSession.setUser(testUser);

        when(sessionRepository.findUserIdByToken("t0k3n")).thenReturn(testSession);
        this.mockMvc.perform(put("/profiles/2").cookie(new Cookie("s_id", "t0k3n"))
                .contentType(MediaType.APPLICATION_JSON).content(EDIT_PROFILE_JSON))
                .andExpect(status().is(400))
                .andExpect(content().string(containsString("Session mismatch")));
    }

    @Test
    public void editPasswordSuccessTest() throws Exception {
        String input = "{\n" +
                "  \"old_password\": \"myoldpwd\",\n" +
                "  \"new_password\": \"mynewpwd\",\n" +
                "  \"repeat_password\": \"mynewpwd\"\n" +
                "}";
        when(service.changePassword(1, "t0k3n", "myoldpwd", "mynewpwd"))
                .thenReturn(responseHandler.formatSuccessResponse(
                        200, "Successfully changed the password"));
        this.mockMvc.perform(put("/profiles/1/password").cookie(new Cookie("s_id", "t0k3n"))
                .contentType(MediaType.APPLICATION_JSON).content(input))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("Successfully changed the password")));
    }

    @Test
    public void editPasswordRepeatMismatchTest() throws Exception {
        String input = "{\n" +
                "  \"old_password\": \"myoldpwd\",\n" +
                "  \"new_password\": \"mynewpwd\",\n" +
                "  \"repeat_password\": \"mynewerpwd\"\n" +
                "}";
        this.mockMvc.perform(put("/profiles/1/password").cookie(new Cookie("s_id", "t0k3n"))
                .contentType(MediaType.APPLICATION_JSON).content(input))
                .andExpect(status().is(400))
                .andExpect(content().string(containsString("newPassword and repeatPassword do no match")));
    }
}
