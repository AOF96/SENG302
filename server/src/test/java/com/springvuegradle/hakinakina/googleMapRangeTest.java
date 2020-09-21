package com.springvuegradle.hakinakina;


import com.springvuegradle.hakinakina.entity.Activity;
import com.springvuegradle.hakinakina.entity.Location;
import com.springvuegradle.hakinakina.repository.ActivityRepository;
import com.springvuegradle.hakinakina.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class googleMapRangeTest {

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    LocationRepository locationRepository;

    private Activity activity;

    @BeforeEach
    void setup() {
        // Clear the database
        activityRepository.deleteAll();
        locationRepository.deleteAll();

        // Setup random activity with location 0, 0
        setupActivity(0, 0);
        setupActivity(2, 0);
    }

    void setupActivity(double longitude, double latitude) {
        activity = new Activity();
        activity.setName("My activity");

        Location location = new Location();
        location.setLongitude(longitude);
        location.setLatitude(latitude);

        location = locationRepository.save(location);

        activity.setLocation(location);
        activity = activityRepository.save(activity);
    }

    @Test
    void testSpecifyLangLotShouldReturnCorrectActivities() {
        List<Activity> activitiesInRange = activityRepository.getActivitiesInRange(
                -1,
                1,
                -1,
                1
        );

        assertEquals(1, activitiesInRange.size());
    }


    @Test
    void testSpecifyLocationWithNoActivitiesShouldReturnNone() {
        List<Activity> activitiesInRange = activityRepository.getActivitiesInRange(
                -2,
                -1,
                -2,
                -1
        );

        assertEquals(0, activitiesInRange.size());
    }


    @Test
    void testSpecifyLatLongThatCoversEverythingShouldReturnAllActivities() {
        List<Activity> activitiesInRange = activityRepository.getActivitiesInRange(
                -1,
                1,
                -2,
                2
        );

        assertEquals(2, activitiesInRange.size());
    }
}
