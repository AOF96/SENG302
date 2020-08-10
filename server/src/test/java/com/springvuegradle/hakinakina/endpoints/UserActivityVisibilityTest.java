package com.springvuegradle.hakinakina.endpoints;

import com.springvuegradle.hakinakina.entity.Activity;
import com.springvuegradle.hakinakina.entity.User;
import com.springvuegradle.hakinakina.entity.Visibility;
import com.springvuegradle.hakinakina.repository.ActivityRepository;
import com.springvuegradle.hakinakina.repository.UserRepository;
import com.springvuegradle.hakinakina.service.ActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class UserActivityVisibilityTest {

    @ExtendWith(SpringExtension.class)
    @SpringBootTest
    public class UserSearchTest {
        @Autowired
        private UserRepository userRepository;

        @Autowired
        private ActivityRepository activityRepository;

        @Autowired
        private ActivityService activityService;

        @BeforeEach
        public void setUp() {
            userRepository.deleteAll(); //this deletes admin from this test file

            User mayuko = new User();
            setupUser(mayuko, "Mayuko", null, "Williams", "mayuko@acnh.com", 0);
            User walter = new User();
            setupUser(walter, "Walter", "Compiler", "Guttman", "walter@acnh.com", 0);
            Activity activityOne= new Activity();
            setupActivity(activityOne,
                    "Learn DFA",
                    "symbol or string or language",
                    true,
                    Timestamp.valueOf(LocalDateTime.now()),
                    Timestamp.valueOf(LocalDateTime.now()),
                    "Christchurch, New Zealand",
                    Visibility.RESTRICTED
            );
            Activity activityTwo= new Activity();
            setupActivity(activityTwo,
                    "Walk to Erskine",
                    "Labs are there",
                    true,
                    Timestamp.valueOf(LocalDateTime.now()),
                    Timestamp.valueOf(LocalDateTime.now()),
                    "Christchurch, New Zealand",
                    Visibility.PUBLIC
            );
        }



        void setupUser(User user, String fName, String mName, String lName, String email, int pLevel) {
            user.setFirstName(fName);
            user.setMiddleName(mName);
            user.setLastName(lName);
            user.setPrimaryEmail(email);
            user.setPermissionLevel(pLevel);
            userRepository.save(user);
        }

        void setupActivity(Activity activity,
                           String name,
                           String description,
                           boolean continuous,
                           java.sql.Timestamp startTime,
                           java.sql.Timestamp endTime,
                           String location,
                           Visibility visibility) {
            activity.setName(name);
            activity.setDescription(description);
            activity.setContinuous(true);
            activity.setStartTime(startTime);
            activity.setEndTime(endTime);
            activity.setLocation(location);
            activity.setVisibility(visibility);

            activityRepository.save(activity);
        }
    }
}
