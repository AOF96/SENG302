package com.springvuegradle.Hakinakina;

import com.springvuegradle.Hakinakina.controller.UserService;
import com.springvuegradle.Hakinakina.entity.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

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
        testUser.setUserId((long) 1);
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(testUser));

        assertTrue(service.editActivityTypes(new ArrayList<String>(), 1));
    }

    @Test
    public void editActivityTypesUserDoesNotExistsTest() {
        User testUser = new User("John", "Smith", "john@gmail.com", null,
                Gender.MALE, 2, "Password1");
        testUser.setUserId((long) 1);
        when(userRepository.findById((long) 1)).thenReturn(Optional.empty());

        assertFalse(service.editActivityTypes(new ArrayList<String>(), 1));
    }
}
