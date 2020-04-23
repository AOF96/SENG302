package com.springvuegradle.Hakinakina;

import com.springvuegradle.Hakinakina.controller.UserService;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
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
}
