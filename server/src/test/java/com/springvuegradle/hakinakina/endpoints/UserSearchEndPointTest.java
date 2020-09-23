package com.springvuegradle.hakinakina.endpoints;

import com.jayway.jsonpath.JsonPath;
import com.springvuegradle.hakinakina.entity.Session;
import com.springvuegradle.hakinakina.entity.User;
import com.springvuegradle.hakinakina.repository.SessionRepository;
import com.springvuegradle.hakinakina.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserSearchEndPointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @BeforeEach
    void setup() {
        User mayuko = new User();
        setupUser(mayuko, "Mayuko", "Middle","Williams", "mayuko@acnh.com");
        User miguel = new User();
        setupUser(miguel, "Miguel", "Morales","Turton", "turton@acnh.com");
        User fabian = new User();
        setupUser(fabian, "Fabian", null,"Gibson", "gibson@acnh.com");
    }

    void setupUser(User user, String fName, String mName, String lName, String email) {
        user.setFirstName(fName);
        user.setMiddleName(mName);
        user.setLastName(lName);
        user.setPrimaryEmail(email);
        user.setPermissionLevel(0);
        userRepository.save(user);

        Session testSession = new Session(fName); // your first name is your token
        testSession.setUser(user);
        sessionRepository.save(testSession);
    }


    @Test
    void testSearchUserWithExistingEmailShouldReturnUser() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("s_id", "Fabian"))
                .param("page", "0")
                .param("email", "gibson@acnh.com")
                .param("searchTerms", "\"\"")
                .param("searchTypes","fullname")
                .param("searchTermsMethod","single")
                .param("method", "and")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("Gibson", JsonPath.parse(response).read("$.content[0].lastname"));
    }


    @Test
    void testUserSearchWithLastNameReturnsUser() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("s_id", "Fabian"))
                .param("page", "0")
                .param("lastname", "Gibson")
                .param("searchTerms", "\"\"")
                .param("searchTypes","fullname")
                .param("searchTermsMethod","single")
                .param("method", "and")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("Fabian", JsonPath.parse(response).read("$.content[0].firstname"));
    }


    @Test
    void testUserSearchWithFullNameReturnsUser() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("s_id", "Fabian"))
                .param("page", "0")
                .param("fullname", "Fabian Gibson")
                .param("searchTerms", "\"\"")
                .param("searchTypes","fullname")
                .param("searchTermsMethod","single")
                .param("method", "and")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("Fabian", JsonPath.parse(response).read("$.content[0].firstname"));
    }


    @Test
    void testUserSearchWithFullNameWithMiddleNameReturnsUser() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("s_id", "Mayuko"))
                .param("page", "0")
                .param("fullname", "Mayuko Middle Williams")
                .param("method", "and")
                .param("searchTerms", "\"\"")
                .param("searchTypes","fullname")
                .param("searchTermsMethod","single")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("Mayuko", JsonPath.parse(response).read("$.content[0].firstname"));
    }


    @Test
    void testUserSearchWithInvalidPageShouldThrowError() throws Exception {
        mockMvc.perform(get("/profiles")
                .cookie(new Cookie("s_id", "Fabian"))
                .param("page", "-1")
                .param("fullname", "Fabian Gibson")
                .param("searchTerms", "\"\"")
                .param("searchTypes","fullname")
                .param("searchTermsMethod","single")
                .param("method", "and")
                .param("size", "10"))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testUserSearchWithInvalidSizeShouldThrowError() throws Exception {
        mockMvc.perform(get("/profiles")
                .cookie(new Cookie("s_id", "Fabian"))
                .param("page", "0")
                .param("fullname", "Fabian Gibson")
                .param("searchTerms", "\"\"")
                .param("searchTypes","fullname")
                .param("searchTermsMethod","single")
                .param("method", "and")
                .param("size", "-1"))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testUserSearchWithInvalidMethodShouldThrowError() throws Exception {
        mockMvc.perform(get("/profiles")
                .cookie(new Cookie("s_id", "Fabian"))
                .param("page", "0")
                .param("fullname", "Fabian Gibson")
                .param("searchTerms", "\"\"")
                .param("searchTypes","fullname")
                .param("searchTermsMethod","single")
                .param("method", "we are not sure")
                .param("size", "10"))
                .andExpect(status().isBadRequest());
    }
}
