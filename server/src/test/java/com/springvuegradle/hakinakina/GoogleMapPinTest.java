package com.springvuegradle.hakinakina;

import com.springvuegradle.hakinakina.dto.ActivityMapDto;
import com.springvuegradle.hakinakina.dto.SearchUserDto;
import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.repository.ActivityRepository;
import com.springvuegradle.hakinakina.repository.LocationRepository;
import com.springvuegradle.hakinakina.repository.SessionRepository;
import com.springvuegradle.hakinakina.repository.UserRepository;
import com.springvuegradle.hakinakina.service.ActivityService;
import com.springvuegradle.hakinakina.service.UserService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Check if the right type of pins are on the map
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class GoogleMapPinTest {

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    ActivityService activityService;

    @Autowired
    UserService userService;

    private Activity privateActivity;
    private Activity publicActivity;
    private Activity restrictedActivity;
    private User mayuko;
    private User fabian;
    private User walter;

    @BeforeEach
    void setup() {
        activityRepository.deleteAll();
        locationRepository.deleteAll();
        userRepository.deleteAll();
        sessionRepository.deleteAll();

        mayuko = new User();
        fabian = new User();
        walter = new User();
        privateActivity = new Activity();
        publicActivity = new Activity();
        restrictedActivity = new Activity();

        // author of the activity
        setupUser(mayuko, "Mayuko", null, "Williams", "mayuko@acnh.com", 0);
        setupUser(fabian, "Fabian", "Scrum", "Gilson", "fabian@acnh.com", 0);
        setupUser(walter, "Walter", "Compiler", "Guttman", "walter@acnh.com", 0);
        setupActivity(privateActivity,"private test",0, 0, Visibility.PRIVATE);
        setupActivity(publicActivity, "public test",2, 0, Visibility.PUBLIC);
        setupActivity(restrictedActivity, "restricted test",1, 0, Visibility.RESTRICTED);

        long followerId = fabian.getUserId();

        // make mayuko follow activity
        userService.subscribeToActivity(followerId, restrictedActivity.getId(), "Fabian");
    }

    void setupActivity(Activity activity, String name, double longitude, double latitude, Visibility visibility) {
        activity.setName(name);
        activity.setAuthor(mayuko);
        activity.setVisibility(visibility);


        Location location = new Location();
        location.setLongitude(longitude);
        location.setLatitude(latitude);

        location = locationRepository.save(location);

        activity.setLocation(location);
        activityRepository.save(activity);
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
    public void testPublicActivityPinsShouldBeSentToAppearForAuthor() {

        List<ActivityMapDto> dtos = activityService.getActivityInRangeDtos(
                -10,
                10,
                -10,
                10,
                mayuko.getUserId()
        );
        List<Long> ids = dtos.stream().map(a -> a.getId()).collect(Collectors.toList());
        assertTrue(ids.contains(publicActivity.getId()));
    }

    @Test
    public void testPrivateActivityPinsShouldBeSentToAppearForAuthor() {

        List<ActivityMapDto> dtos = activityService.getActivityInRangeDtos(
                -10,
                10,
                -10,
                10,
                mayuko.getUserId()
        );
        List<Long> ids = dtos.stream().map(a -> a.getId()).collect(Collectors.toList());
        assertTrue(ids.contains(privateActivity.getId()));
    }

    @Test
    public void testRestrictedActivityPinsShouldBeSentToAppearForAuthor() {

        List<ActivityMapDto> dtos = activityService.getActivityInRangeDtos(
                -10,
                10,
                -10,
                10,
                mayuko.getUserId()
        );
        List<Long> ids = dtos.stream().map(a -> a.getId()).collect(Collectors.toList());
        assertTrue(ids.contains(restrictedActivity.getId()));
    }

    @Test
    public void testPublicActivityPinsShouldBeSentToAppearForUser() {

        List<ActivityMapDto> dtos = activityService.getActivityInRangeDtos(
                -10,
                10,
                -10,
                10,
                fabian.getUserId()
        );
        List<Long> ids = dtos.stream().map(a -> a.getId()).collect(Collectors.toList());
        assertTrue(ids.contains(publicActivity.getId()));
    }

    @Test
    public void testPrivateActivityPinsShouldNotBeSentToAppearForUser() {

        List<ActivityMapDto> dtos = activityService.getActivityInRangeDtos(
                -10,
                10,
                -10,
                10,
                fabian.getUserId()
        );
        List<Long> ids = dtos.stream().map(a -> a.getId()).collect(Collectors.toList());
        assertFalse(ids.contains(privateActivity.getId()));
    }

    @Test
    public void testRestrictedActivityPinsShouldBeSentToAppearForSharedUser() {

        List<ActivityMapDto> dtos = activityService.getActivityInRangeDtos(
                -10,
                10,
                -10,
                10,
                fabian.getUserId()
        );
        List<Long> ids = dtos.stream().map(a -> a.getId()).collect(Collectors.toList());
        assertTrue(ids.contains(restrictedActivity.getId()));
    }

    @Test
    public void testRestrictedActivityPinsShouldNotBeSentToAppearForUser() {

        List<ActivityMapDto> dtos = activityService.getActivityInRangeDtos(
                -10,
                10,
                -10,
                10,
                walter.getUserId()
        );
        List<Long> ids = dtos.stream().map(a -> a.getId()).collect(Collectors.toList());
        assertFalse(ids.contains(restrictedActivity.getId()));
    }
}
