package com.springvuegradle.hakinakina;

import com.springvuegradle.hakinakina.entity.Activity;
import com.springvuegradle.hakinakina.entity.Gender;
import com.springvuegradle.hakinakina.entity.User;
import com.springvuegradle.hakinakina.entity.Visibility;
import com.springvuegradle.hakinakina.repository.ActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ActivityTests {

    @Autowired
    ActivityRepository activityRepository;

    @BeforeEach
    public void setUp() {
        activityRepository.deleteAll();
    }

    public Activity createActivity() {
        return new Activity("Climb mount Everest", "Let's climb the mountain",
                false, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()),
                "Christchurch, New Zealand");
    }

    @Test
    public void testCreatingNewActivity() {
        Activity activity = new Activity("Climb mount Everest", "Let's climb the mountain",
                false, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()),
                "Christchurch, New Zealand");

        assertNull(activity.getId());
        assertEquals("Climb mount Everest", activity.getName());
    }

    @Test
    public void addVisibilityToActivityTest() {
        Activity activity = createActivity();
        activity.setVisibility(Visibility.PUBLIC);

        activityRepository.save(activity);

        assertEquals(Visibility.PUBLIC, activityRepository.findActivityById(activity.getId()).getVisibility());
    }
}
