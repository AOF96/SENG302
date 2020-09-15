package com.springvuegradle.hakinakina.service_tests;

import com.springvuegradle.hakinakina.repository.LocationRepository;
import com.springvuegradle.hakinakina.repository.SessionRepository;
import com.springvuegradle.hakinakina.service.UserService;
import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.repository.UserRepository;
import io.cucumber.java.eo.Se;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.Cookie;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private UserService service;

    @BeforeAll
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void calculateAgeTest() {
        Date date = Date.valueOf("1995-04-23");
        int age = UserService.calculateAge(date, LocalDate.of(2020, 4, 23));
        assertEquals(25, age);
    }

    @Test
    public void calculateAge2Test() {
        Date date = Date.valueOf("2005-04-23");
        int age = UserService.calculateAge(date, LocalDate.of(2020, 4, 23));
        assertEquals(15, age);
    }

    @Test
    public void checkAgeTest() {
        assertTrue(UserService.checkAge(Date.valueOf("1995-04-23"), LocalDate.of(2020, 4, 23)));
        assertTrue(UserService.checkAge(Date.valueOf("2007-04-23"), LocalDate.of(2020, 4, 23)));
        assertTrue(UserService.checkAge(Date.valueOf("1880-04-23"), LocalDate.of(2020, 4, 23)));
        assertFalse(UserService.checkAge(Date.valueOf("1879-04-23"), LocalDate.of(2020, 4, 23)));
        assertFalse(UserService.checkAge(Date.valueOf("2008-04-23"), LocalDate.of(2020, 4, 23)));
        assertFalse(UserService.checkAge(Date.valueOf("2019-04-23"), LocalDate.of(2020, 4, 23)));
        assertFalse(UserService.checkAge(Date.valueOf("1500-04-23"), LocalDate.of(2020, 4, 23)));
    }

    @Test
    public void editActivityTypesUserExistsTest() {
        User testUser = new User("John", "Smith", "john@gmail.com", null,
                Gender.MALE, 2, "Password1");
        final Cookie tokenCookie = new Cookie("s_id", "t0k3n");
        testUser.setUserId((long) 1);
        Session session = new Session("tok3n");
        session.setUser(testUser);
        when(sessionRepository.findUserIdByToken("tok3n")).thenReturn(session);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(testUser));

        assertEquals(200, service.editActivityTypes(new ArrayList<String>(), 1, "tok3n").getStatusCode().value());
    }

    @Test
    public void editActivityTypesUserDoesNotExistsTest() {
        User testUser = new User("John", "Smith", "john@gmail.com", null,
                Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);
        //Session session = new Session("tok3n");
        //session.setUser(testUser);
        //when(sessionRepository.findUserIdByToken("tok3n")).thenReturn(session);
        when(userRepository.findById((long) 1)).thenReturn(Optional.empty());

        assertEquals(401, service.editActivityTypes(new ArrayList<String>(), 1, "t0k3n").getStatusCode().value());
    }

    @Test
    public void editHomeLocationTest(){
        Location location = new Location("42 manuka Palace", "Twizel", "Mackenzie", 7999, "Mackenzie", "New Zealand", -12.2323, 4.5656);
        location.setId(12L);
        User user3 = new User("Dane", "Joe", "dane@mail.com", "", Gender.NON_BINARY, 3, "coolPassword3");
        user3.setUserId(1L);

        userRepository.save(user3);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user3));

        service.editLocation("42 manuka Palace", "Twizel", "Mackenzie", 7999, "Mackenzie", "New Zealand", -12.2323, 4.5656, 1L);

        assertEquals("42 manuka Palace", user3.getHomeLocation().getStreetAddress());
    }

    @Test
    public void getIntersectionOfListOfSetsOfUsersTest() {
        User user1 = new User("John", "Doe", "john@mail.com", "", Gender.MALE, 1, "coolPassword1");
        User user2 = new User("Jane", "Doe", "jane@mail.com", "", Gender.FEMALE, 2, "coolPassword2");
        User user3 = new User("Dane", "Joe", "dane@mail.com", "", Gender.NON_BINARY, 3, "coolPassword3");

        List<Set<User>> list = new ArrayList<>();
        Set<User> set1 = new HashSet<>();
        set1.add(user1);
        set1.add(user2);

        Set<User> set2 = new HashSet<>();
        set2.add(user2);
        set2.add(user3);

        Set<User> set3 = new HashSet<>();
        set3.add(user1);
        set3.add(user2);
        set3.add(user3);

        list.add(set1);
        list.add(set2);
        list.add(set3);

        List<User> expectedResult = new ArrayList<>();
        expectedResult.add(user2);
        assertEquals(expectedResult, service.getIntersectionOfListOfSetsOfUsers(list));
    }

    @Test
    public void getIntersectionOfListOfSetsOfUsersOneSetTest() {
        User user1 = new User("John", "Doe", "john@mail.com", "", Gender.MALE, 1, "coolPassword1");
        User user2 = new User("Jane", "Doe", "jane@mail.com", "", Gender.FEMALE, 2, "coolPassword2");
        user1.setUserId(2222L);
        user2.setUserId(2323L);
        List<Set<User>> list = new ArrayList<>();
        Set<User> set1 = new HashSet<>();
        set1.add(user1);
        set1.add(user2);
        list.add(set1);

        //Order of result is random, thus use several asserts instead of comparing two lists
        List<User> result = service.getIntersectionOfListOfSetsOfUsers(list);
        assertEquals(2, result.size());
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));
    }

    @Test
    public void getIntersectionOfListOfSetsOfUsersEmptyListTest() {
        List<Set<User>> list = new ArrayList<>();
        assertEquals(0, service.getIntersectionOfListOfSetsOfUsers(list).size());
    }
}
