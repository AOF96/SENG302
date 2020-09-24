package com.springvuegradle.hakinakina;

import com.springvuegradle.hakinakina.entity.Activity;
import com.springvuegradle.hakinakina.entity.Gender;
import com.springvuegradle.hakinakina.entity.User;
import com.springvuegradle.hakinakina.entity.Visibility;
import com.springvuegradle.hakinakina.repository.ActivityRepository;
import com.springvuegradle.hakinakina.repository.UserActivityRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ActivityTests {

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    UserActivityRoleRepository userActivityRoleRepository;

    @BeforeEach
    public void setUp() {
        userActivityRoleRepository.deleteAll();
        activityRepository.deleteAll();
    }

    public Activity createActivity() {
        return new Activity("Climb mount Everest", "Let's climb the mountain",
                false, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
    }

    @Test
    public void testCreatingNewActivity() {
        Activity activity = new Activity("Climb mount Everest", "Let's climb the mountain",
                false, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));

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

    @Test
    public void searchUsersWithMultipleTermsOR() {
        Activity activity1 = new Activity();
        activity1.setName("Fun");
        Activity activity2 = new Activity();
        activity2.setName("Exciting");
        Activity activity3 = new Activity();
        activity3.setName("Fun and Exciting");
        Activity activity4 = new Activity();
        activity4.setName("Scary and Dangerous");

        activityRepository.save(activity1);
        activityRepository.save(activity2);
        activityRepository.save(activity3);
        activityRepository.save(activity4);

        Set<String> searchTerms = new HashSet<String>();
        searchTerms.add("Fun");
        searchTerms.add("Exciting");

        List<Activity> results = activityRepository.findActivityNamesOr(searchTerms);
        assertEquals(3, results.size());
        for (Activity activity : results) {
            assertTrue((activity.getName().contains("Fun") || activity.getName().contains("Exciting")));
        }
    }

    @Test
    public void searchUsersWithMultipleTermsOREmptyTerms() {
        Activity activity1 = new Activity();
        activity1.setName("Fun");
        Activity activity2 = new Activity();
        activity2.setName("Exciting");
        Activity activity3 = new Activity();
        activity3.setName("Fun and Exciting");
        Activity activity4 = new Activity();
        activity4.setName("Scary and Dangerous");

        activityRepository.save(activity1);
        activityRepository.save(activity2);
        activityRepository.save(activity3);
        activityRepository.save(activity4);

        Set<String> searchTerms = new HashSet<String>();

        List<Activity> results = activityRepository.findActivityNamesOr(searchTerms);
        assertEquals(0, results.size());
    }

    @Test
    public void searchUsersWithMultipleTermsAND() {
        Activity activity1 = new Activity();
        activity1.setName("Fun");
        Activity activity2 = new Activity();
        activity2.setName("Exciting");
        Activity activity3 = new Activity();
        activity3.setName("Fun and Exciting");
        Activity activity4 = new Activity();
        activity4.setName("Scary and Dangerous");
        Activity activity5 = new Activity();
        activity5.setName("Pretty Fun but maybe not that Exciting");

        activityRepository.save(activity1);
        activityRepository.save(activity2);
        activityRepository.save(activity3);
        activityRepository.save(activity4);
        activityRepository.save(activity5);

        Set<String> searchTerms = new HashSet<String>();
        searchTerms.add("Fun");
        searchTerms.add("Exciting");

        List<Activity> results = activityRepository.findActivityNamesAnd(searchTerms);
        assertEquals(2, results.size());
        for (Activity activity : results) {
            assertTrue((activity.getName().contains("Fun") && activity.getName().contains("Exciting")));
        }
    }

    @Test
    public void searchUsersWithMultipleTermsANDEmptyTerms() {
        Activity activity1 = new Activity();
        activity1.setName("Fun");
        Activity activity2 = new Activity();
        activity2.setName("Exciting");
        Activity activity3 = new Activity();
        activity3.setName("Fun and Exciting");
        Activity activity4 = new Activity();
        activity4.setName("Scary and Dangerous");
        Activity activity5 = new Activity();
        activity5.setName("Pretty Fun but maybe not that Exciting");

        activityRepository.save(activity1);
        activityRepository.save(activity2);
        activityRepository.save(activity3);
        activityRepository.save(activity4);
        activityRepository.save(activity5);

        Set<String> searchTerms = new HashSet<String>();

        List<Activity> results = activityRepository.findActivityNamesAnd(searchTerms);
        assertEquals(0, results.size());
    }
}
