package com.springvuegradle.hakinakina;

import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.repository.ActivityRepository;
import com.springvuegradle.hakinakina.repository.SessionRepository;
import com.springvuegradle.hakinakina.repository.UserActivityRoleRepository;
import com.springvuegradle.hakinakina.repository.UserRepository;
import com.springvuegradle.hakinakina.service.ActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserActivityRoleTest {

    @Autowired
    private UserActivityRoleRepository userActivityRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityRepository activityRepository;

    private User mayuko;

    @BeforeEach
    public void setup() {
        userActivityRoleRepository.deleteAll();
        activityRepository.deleteAll();
        userRepository.deleteAll();
        mayuko = new User();
        setupUser(mayuko, "Mayuko", null, "Williams", "mayuko@acnh.com", 0);
    }

    void setupUser(User user, String fName, String mName, String lName, String email, int pLevel) {
        user.setFirstName(fName);
        user.setMiddleName(mName);
        user.setLastName(lName);
        user.setPrimaryEmail(email);
        user.setPermissionLevel(pLevel);
        userRepository.save(user);

        Session testSession = new Session(fName); // your first name is your token
        testSession.setUser(user);
        sessionRepository.save(testSession);
    }

    @Test
    public void testCreateActivityCreatorAssignment() {

        Date startDate1 = new Date(2021, 10, 10);
        Date endDate1 = new Date(2021, 10, 12);
        Activity activity1 = new Activity("Climb Mount Everest", "Let's climb Mount Everest together",
                true, new Timestamp(startDate1.getTime()), new Timestamp(endDate1.getTime()),
                "Mount Everest");
        Set<ActivityType> activityTypes = Set.of(new ActivityType("Extreme"));
        activity1.setActivityTypes(activityTypes);


        activityService.addActivity(activity1, this.mayuko.getUserId(), "Mayuko");

        Optional<Activity> byName = activityRepository.findFirstByName("Climb Mount Everest");
        assertTrue(byName.isPresent());

        Optional<UserActivityRole> byId = userActivityRoleRepository.findById(
                new UserActivityKey(this.mayuko.getUserId(), byName.get().getId())
        );
        assertTrue(byId.isPresent());
        assertEquals(ActivityRole.CREATOR, byId.get().getActivityRole());
    }


    @Test
    public void testIfActivityDeletedShouldDeleteRoleAsWell() {

        Date startDate1 = new Date(2021, 10, 10);
        Date endDate1 = new Date(2021, 10, 12);
        Activity activity1 = new Activity("Climb Mount Everest", "Let's climb Mount Everest together",
                true, new Timestamp(startDate1.getTime()), new Timestamp(endDate1.getTime()),
                "Mount Everest");
        Set<ActivityType> activityTypes = Set.of(new ActivityType("Extreme"));
        activity1.setActivityTypes(activityTypes);


        ResponseEntity<String> response = activityService.addActivity(activity1, this.mayuko.getUserId(), "Mayuko");
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Optional<Activity> byName = activityRepository.findFirstByName("Climb Mount Everest");
        assertTrue(byName.isPresent());

        response = activityService.removeActivity(this.mayuko.getUserId(), byName.get().getId(), "Mayuko");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        byName = activityRepository.findFirstByName("Climb Mount Everest");
        assertFalse(byName.isPresent());
    }

}