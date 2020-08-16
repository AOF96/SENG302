package com.springvuegradle.hakinakina.entity_tests;

import com.springvuegradle.hakinakina.entity.Achievement;
import com.springvuegradle.hakinakina.entity.Activity;
import com.springvuegradle.hakinakina.entity.ActivityType;
import com.springvuegradle.hakinakina.entity.ResultType;
import com.springvuegradle.hakinakina.repository.AchievementRepository;
import com.springvuegradle.hakinakina.repository.ActivityRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AchievementTests {

    @Autowired
    AchievementRepository achievementRepository;

    @Autowired
    ActivityRepository activityRepository;

    @BeforeEach
    public void clearTestRepositories() throws Exception {
        activityRepository.deleteAll();
        achievementRepository.deleteAll();
    }

    /**
     * Creates activity to use with tests
     *
     * @return Activity entity
     */
    private Activity createTestActivity() {
        // add test activity and connect it to the test user
        // it should prints using toJson method as following
        //{"id":1,"users":[],"activity_name":"name","description":"description","activity_type":[{"name":"Fun","users":[]}],"continuous":false,"start_time":1000000000,"end_time":1000001000,"location":"location"}
        java.util.Date date = new java.util.Date();
        long time = 1000000000;
        java.sql.Date startTime = new java.sql.Date(time);
        java.sql.Date endTime = new java.sql.Date(time+1000);
        Activity testActivity = new Activity("name", "description", false,
                new Timestamp(startTime.getTime()), new Timestamp(endTime.getTime()), "location");

        testActivity.setId((long) 1);
        Set<ActivityType> activityTypes = new HashSet<>();
        activityTypes.add(new ActivityType("Fun"));
        testActivity.setActivityTypes(activityTypes);
        return testActivity;
    }

    @Test
    public void createAchievementTest() {
        Achievement achievement = new Achievement("Test", "Test", ResultType.TIME);

        Assert.assertEquals(ResultType.TIME, achievement.getResultType());
    }

    @Test
    public void addAchievementToActivityTest() {
        Activity activity = createTestActivity();
        activityRepository.save(activity);

        Achievement achievement = new Achievement("Test", "Test", ResultType.TIME);
        achievementRepository.save(achievement);

        activity = activityRepository.findAll().get(0);
        activity.addAchievement(achievement);
        activityRepository.save(activity);

        Assert.assertEquals(1, activityRepository.findAll().get(0).getAchievements().size());
    }
}
