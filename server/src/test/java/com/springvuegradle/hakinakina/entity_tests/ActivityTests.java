package com.springvuegradle.hakinakina.entity_tests;

import com.springvuegradle.hakinakina.entity.Activity;
import com.springvuegradle.hakinakina.entity.ActivityAttribute;
import com.springvuegradle.hakinakina.entity.Gender;
import com.springvuegradle.hakinakina.entity.User;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ActivityTests {


    User testUser = new User("Maurice", "Benson", "jacky@google.com",
            "1985-12-20", Gender.MALE, 3,
            "jacky'sSecuredPwd");
    User testUser2 = new User("James", "Benson", "jacky12@google.com",
            "1985-12-20", Gender.MALE, 3,
            "jacky'sSecuredPwd");
    User testUser3 = new User("Maurice", "Benson", "jacky@google.com",
            "1985-12-20", Gender.MALE, 3,
            "jacky'sSecuredPwd");
    Activity activity = new Activity("scuba diving", "dive to the bottom of the sea", false, null, null, "Ireland");
    Activity activity2 = new Activity("sky diving", "jump from the plane", false, null, null, "England");
    Activity activity3 = new Activity("scuba diving", "dive to the bottom of the sea", false, null, null, "Ireland");
    Activity activity4 = new Activity("sky diving", "jump from the plane", false, null, null, "England");



    @BeforeEach
    public void init(){
        testUser.setUserId(122L);
        activity.setAuthor(testUser);
        activity.setId(22L);
        testUser2.setUserId(121L);
        activity2.setAuthor(testUser2);
        activity2.setId(23L);
        testUser3.setUserId(122L);
        activity3.setAuthor(testUser3);
        activity3.setId(22L);
        activity4.setAuthor(testUser2);
        activity4.setId(7L);
    }

    @Test
    public void findActivityChangesDifferences(){
        Set<ActivityAttribute> expected = new HashSet<>();
        expected.add(ActivityAttribute.NAME);
        expected.add(ActivityAttribute.DESCRIPTION);
        expected.add(ActivityAttribute.LOCATION);
        expected.add(ActivityAttribute.AUTHOR);
        expected.add(ActivityAttribute.ID);
        activity2.setContinuous(true);
        expected.add(ActivityAttribute.CONTINUOUS);
        activity.setStartTime(new Timestamp(1));
        activity.setStartTime(new Timestamp(2));
        expected.add(ActivityAttribute.START_TIME);
        assertEquals(expected, activity.findActivityChanges(activity2));
    }

    @Test
    public void findActivityChangesIdentical(){
        Set<ActivityAttribute> expected = new HashSet<>();
        assertEquals(expected, activity.findActivityChanges(activity3));
    }

    @Test
    public void findActivityChangesPartiallySame(){
        Set<ActivityAttribute> expected = new HashSet<>();
        activity4.setContinuous(true);
        activity4.setEndTime(new Timestamp(4));
        activity4.setLocation("New Zealand");
        expected.add(ActivityAttribute.END_TIME);
        expected.add(ActivityAttribute.CONTINUOUS);
        expected.add(ActivityAttribute.LOCATION);
        expected.add(ActivityAttribute.ID);
        assertEquals(expected, activity2.findActivityChanges(activity4));
    }


}