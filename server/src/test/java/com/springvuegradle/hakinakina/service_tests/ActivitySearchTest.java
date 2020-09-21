package com.springvuegradle.hakinakina.service_tests;

import com.springvuegradle.hakinakina.dto.SearchActivityDto;
import com.springvuegradle.hakinakina.dto.SearchUserDto;
import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.repository.*;
import com.springvuegradle.hakinakina.service.ActivityService;
import com.springvuegradle.hakinakina.service.SearchService;
import com.springvuegradle.hakinakina.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ActivitySearchTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserService userService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private UserActivityRoleRepository userActivityRoleRepository;

    @Autowired
    private LocationRepository locationRepository;

    private User mayuko;
    private User walter;
    private User fabian;
    private User marina;
    private User richard;
    private Activity privateActivity;
    private Activity restrictedActivity;
    private Activity publicActivity;

    @BeforeEach
    public void setUp() {

        // delete everything from h2
        userActivityRoleRepository.deleteAll();
        activityRepository.removeAllUserActivities();
        activityRepository.deleteAll();
        userRepository.deleteAll(); //this deletes admin from this test file
        sessionRepository.deleteAll();

        // set up users
        mayuko = new User();
        setupUser(mayuko, "Mayuko", null, "Williams", "mayuko@acnh.com", 0);
        fabian = new User(); // fabian is shared all activities, can see everything except for private activities
        setupUser(fabian, "Fabian", "Scrum", "Gilson", "fabian@acnh.com", 0);
        richard = new User(); // richard does not have any activities that are shared to him
        setupUser(richard, "Richard", null, "Robb", "richard@acnh.com", 0);
        marina = new User(); // Default Admin
        setupUser(marina, "Marina", "Ski", "Filipovic", "marina@acnh.com", 2);
        walter = new User();// Admin
        setupUser(walter, "Walter", "Compiler", "Guttman", "walter@acnh.com", 1);

        privateActivity = new Activity();
        restrictedActivity = new Activity();
        publicActivity = new Activity();

        // only mayuko/creator can see it
        setUpActivity(privateActivity,
                "Private Mayuko Activity",
                true,
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now()),
                mayuko,
                Visibility.PRIVATE,
                mayuko
        );

        // only shared users can see it
        setUpActivity(restrictedActivity,
                "Restricted Mayuko Activity",
                true,
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now()),
                mayuko,
                Visibility.RESTRICTED,
                fabian
        );

        // everyone can see it
        setUpActivity(publicActivity,
                "Public Mayuko Activity",
                true,
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now()),
                mayuko,
                Visibility.PUBLIC,
                fabian
        );




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

    void setUpActivity(Activity activity,
                       String name,
                       boolean continuous,
                       java.sql.Timestamp startTime,
                       java.sql.Timestamp endTime,
                       User author,
                       Visibility visibility,
                       User follower) {

        Location myLocation = new Location(
                "2 Homestead Lane",
                "Upper Riccarton",
                "Christchurch",
                8442,
                "Canterbury",
                "New Zealand",
                -43.52,
                172.57);

        locationRepository.save(myLocation);

        activity.setName(name);
        activity.setContinuous(continuous);
        activity.setAuthor(author);
        activity.setVisibility(visibility);
        activity.setLocation(myLocation);
        activityRepository.save(activity);

        // set role for creator of the activity
        long aId = activity.getId();
        long uId = author.getUserId();
        UserActivityKey userActivityKey = new UserActivityKey(uId, aId);
        userActivityRoleRepository.save(new UserActivityRole(userActivityKey, ActivityRole.CREATOR));

        // add Activity? seems like it adds author
        // activityService.addActivity(activity, follower.getUserId(), follower.getFirstName());

        long followerId = follower.getUserId();

        // make  follow activity
        userService.subscribeToActivity(followerId, activity.getId(), follower.getFirstName());
    }

    

    @Test
    void testCreatorCanGetPublicActivitySearchResult() {

        Page<SearchActivityDto> page = searchService.findActivityPaginated("public", 0, 10, mayuko);
        List<SearchActivityDto> content = page.getContent();
        assertEquals("Public Mayuko Activity", content.get(0).getName());
    }

    @Test
    void testUserCanGetPublicActivitySearchResult() {

        Page<SearchActivityDto> page = searchService.findActivityPaginated("public", 0, 10, richard);
        List<SearchActivityDto> content = page.getContent();
        assertEquals("Public Mayuko Activity", content.get(0).getName());
    }

    @Test
    void testAdminCanGetPublicActivitySearchResult() {

        Page<SearchActivityDto> page = searchService.findActivityPaginated("public", 0, 10, walter);
        List<SearchActivityDto> content = page.getContent();
        assertEquals("Public Mayuko Activity", content.get(0).getName());
    }

    @Test
    void testGlobalAdminCanGetPublicActivitySearchResult() {

        Page<SearchActivityDto> page = searchService.findActivityPaginated("public", 0, 10, marina);
        List<SearchActivityDto> content = page.getContent();
        assertEquals("Public Mayuko Activity", content.get(0).getName());
    }

    @Test
    void testCreatorCanGetPrivateActivitySearchResult() {

        Page<SearchActivityDto> page = searchService.findActivityPaginated("private", 0, 10, mayuko);
        List<SearchActivityDto> content = page.getContent();
        assertEquals("Private Mayuko Activity", content.get(0).getName());
    }

    @Test
    void testUserCanNotGetPrivateActivitySearchResult() {

        Page<SearchActivityDto> page = searchService.findActivityPaginated("private", 0, 10, richard);
        long resultCount = page.getTotalElements();
        assertEquals(0, resultCount);
    }

    @Test
    void testAdminCanGetPrivateActivitySearchResult() {
        Page<SearchActivityDto> page = searchService.findActivityPaginated("private", 0, 10, walter);
        List<SearchActivityDto> content = page.getContent();
        assertEquals("Private Mayuko Activity", content.get(0).getName());
    }

    @Test
    void testGlobalAdminCanGetPrivateActivitySearchResult() {
        Page<SearchActivityDto> page = searchService.findActivityPaginated("private", 0, 10, marina);
        List<SearchActivityDto> content = page.getContent();
        assertEquals("Private Mayuko Activity", content.get(0).getName());
    }

    @Test
    void testCreatorCanGetRestrictedActivitySearchResult() {

        Page<SearchActivityDto> page = searchService.findActivityPaginated("restricted", 0, 10, mayuko);
        List<SearchActivityDto> content = page.getContent();
        assertEquals("Restricted Mayuko Activity", content.get(0).getName());
    }

    @Test
    void testSharedUserCanGetRestrictedActivitySearchResult() {

        Page<SearchActivityDto> page = searchService.findActivityPaginated("restricted", 0, 10, fabian);
        List<SearchActivityDto> content = page.getContent();
        assertEquals("Restricted Mayuko Activity", content.get(0).getName());
    }

    @Test
    void testNotSharedUserCanNotGetRestrictedActivitySearchResult() {

        Page<SearchActivityDto> page = searchService.findActivityPaginated("restricted", 0, 10, richard);
        long resultCount = page.getTotalElements();
        assertEquals(0, resultCount);
    }

    @Test
    void testAdminCanGetRestrictedActivitySearchResult() {

        Page<SearchActivityDto> page = searchService.findActivityPaginated("restricted", 0, 10, walter);
        List<SearchActivityDto> content = page.getContent();
        assertEquals("Restricted Mayuko Activity", content.get(0).getName());
    }

    @Test
    void testGlobalAdminCanGetRestrictedActivitySearchResult() {

        Page<SearchActivityDto> page = searchService.findActivityPaginated("restricted", 0, 10, marina);
        List<SearchActivityDto> content = page.getContent();
        assertEquals("Restricted Mayuko Activity", content.get(0).getName());
    }
}
