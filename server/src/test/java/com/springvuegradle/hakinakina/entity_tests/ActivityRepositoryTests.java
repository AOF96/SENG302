package com.springvuegradle.hakinakina.entity_tests;

import com.springvuegradle.hakinakina.entity.Activity;
import com.springvuegradle.hakinakina.entity.ActivityType;
import com.springvuegradle.hakinakina.entity.Location;
import com.springvuegradle.hakinakina.repository.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tests checking the the Repository method when retrieving activities in a given bound of latitudes and longitudes.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ActivityRepositoryTests {

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    LocationRepository locationRepository;

    @BeforeEach
    public void clearRepositories(){
        activityRepository.deleteAll();
        locationRepository.deleteAll();
    }

    private Activity createTestActivity() {
        // add test activity and connect it to the test user
        // it should prints using toJson method as following
        //{"id":1,"users":[],"activity_name":"name","description":"description","activity_type":[{"name":"Fun","users":[]}],"continuous":false,"start_time":1000000000,"end_time":1000001000,"location":"location"}
        java.util.Date date = new java.util.Date();
        long time = 1000000000;
        java.sql.Date startTime = new java.sql.Date(time);
        java.sql.Date endTime = new java.sql.Date(time+1000);
        Activity testActivity = new Activity("name", "description", false,
                new Timestamp(startTime.getTime()), new Timestamp(endTime.getTime()));

        testActivity.setId((long) 1);
        Set<ActivityType> activityTypes = new HashSet<>();
        activityTypes.add(new ActivityType("Fun"));
        testActivity.setActivityTypes(activityTypes);
        return testActivity;
    }

    @Test
    public void getActivitiesInRangeTest(){
        Activity activity = createTestActivity();
        Location location = new Location("12 house lane", "house", "Wanaka", 7021,
                "state", "country", 500.0, 500.0);
        Activity activity2 = createTestActivity();
        Location location2 = new Location("12 house lane", "house", "Wanaka", 7021,
                "state", "country", 600.0, 500.0);
        locationRepository.save(location);
        locationRepository.save(location2);
        activity.setLocation(location);
        activity2.setLocation(location2);
        activityRepository.save(activity);
        activityRepository.save(activity2);
        assertEquals(2, activityRepository.getActivitiesInRange(-1000.0, 2000.0, -2000.0, 1000.0).size());
    }

    @Test
    public void getActivitiesRangeTestOutRangeLatitude(){
        Activity activity = createTestActivity();
        Location location = new Location("12 house lane", "house", "Wanaka", 7021,
                "state", "country", 3000.0, 500.0);
        locationRepository.save(location);
        activity.setLocation(location);
        activityRepository.save(activity);
        assertNotEquals(1, activityRepository.getActivitiesInRange(-1000.0, 2000.0, -2000.0, 1000.0).size());
    }

    @Test
    public void getActivitiesRangeTestOutRangeLongitude(){
        Activity activity = createTestActivity();
        Location location = new Location("12 house lane", "house", "Wanaka", 7021,
                "state", "country", 500.0, -3500.0);
        locationRepository.save(location);
        activity.setLocation(location);
        activityRepository.save(activity);
        assertNotEquals(1, activityRepository.getActivitiesInRange(-1000.0, 2000.0, -2000.0, 1000.0).size());
    }

    @Test
    public void getActivitiesRangeTestReturnsNoActivity(){
        Activity activity = createTestActivity();
        Location location = new Location("12 house lane", "house", "Wanaka", 7021,
                "state", "country", 3500.0, -3500.0);
        locationRepository.save(location);
        activity.setLocation(location);
        activityRepository.save(activity);
        assertNotEquals(1, activityRepository.getActivitiesInRange(-1000.0, 2000.0, -2000.0, 1000.0).size());
    }

}

