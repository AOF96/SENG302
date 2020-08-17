package com.springvuegradle.hakinakina.endpoints;

import com.jayway.jsonpath.JsonPath;
import com.springvuegradle.hakinakina.dto.ActivityVisibilityDto;
import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.repository.ActivityRepository;
import com.springvuegradle.hakinakina.repository.SessionRepository;
import com.springvuegradle.hakinakina.repository.UserActivityRoleRepository;
import com.springvuegradle.hakinakina.repository.UserRepository;
import com.springvuegradle.hakinakina.service.ActivityService;
import com.springvuegradle.hakinakina.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserActivityVisibilityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserActivityRoleRepository userActivityRoleRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserService userService;

    private User mayuko;
    private User walter;
    private User fabian;
    private Activity activityOne;
    private Activity activityTwo;
    private Activity activityThree;


    @BeforeEach
    public void setUp() {
        // delete everything from h2
        userActivityRoleRepository.deleteAll();
        activityRepository.removeAllUserActivities();
        activityRepository.deleteAll();
        userRepository.deleteAll(); //this deletes admin from this test file
        sessionRepository.deleteAll();

        // create users (in this test file, mayuko follows walter's activities, fabian is not following anything)
        mayuko = new User();
        setupUser(mayuko, "Mayuko", null, "Williams", "mayuko@acnh.com", 0);
        walter = new User();
        setupUser(walter, "Walter", "Compiler", "Guttman", "walter@acnh.com", 0);
        fabian = new User();
        setupUser(fabian, "Fabian", "Agile", "Gilson", "fabian@acnh.com", 0);


        // Restricted activity
        activityOne = new Activity();
        setupActivity(walter,
                activityOne,
                "Learn DFA",
                "symbol or string or language",
                true,
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now()),
                "Christchurch, New Zealand",
                Visibility.RESTRICTED,
                mayuko
        );

        // Public activity
        activityTwo = new Activity();
        setupActivity(walter,
                activityTwo,
                "Walk to Erskine",
                "Labs are there",
                true,
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now()),
                "Christchurch, New Zealand",
                Visibility.PUBLIC,
                mayuko
        );

        // Private activity
        activityThree = new Activity();
        setupActivity(walter,
                activityThree,
                "Create short pencils",
                "Useful when pointing automata",
                true,
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now()),
                "Christchurch, New Zealand",
                Visibility.PRIVATE,
                mayuko
        );
    }


    /**
     * helper function to sets up user for this test file
     */
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

    /**
     * helper function to sets up activity for this test file
     */
    void setupActivity(User user,
                       Activity activity,
                       String name,
                       String description,
                       boolean continuous,
                       java.sql.Timestamp startTime,
                       java.sql.Timestamp endTime,
                       String location,
                       Visibility visibility,
                       User follower) {
        activity.setName(name);
        activity.setDescription(description);
        activity.setContinuous(continuous);
        activity.setStartTime(startTime);
        activity.setEndTime(endTime);
        activity.setLocation(location);
        activity.setVisibility(visibility);
        activity.setAuthor(user);
        activityRepository.save(activity);

        // set role for creator of the activity
        long aId = activity.getId();
        long uId = user.getUserId();
        UserActivityKey userActivityKey = new UserActivityKey(uId, aId);
        userActivityRoleRepository.save(new UserActivityRole(userActivityKey, ActivityRole.CREATOR));

          // add Activity? seems like it adds author
          // activityService.addActivity(activity, follower.getUserId(), follower.getFirstName());

        long followerId = follower.getUserId();

        // make mayuko follow activity
        userService.subscribeToActivity(followerId, activity.getId(), "Mayuko");

    }

    // fabian can't view private page
    @Test
    void testUserCannotSeePrivateActivity() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/activities/" + activityThree.getId()
                + "/profiles/" + fabian.getUserId() + "/uservisibility")
                .cookie(new Cookie("s_id", "Fabian")))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("not allowed", JsonPath.parse(response).read("$.visibility"));
    }

    // mayuko can't view private page
    @Test
    void testSharedUserCannotSeePrivateActivity() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/activities/" + activityThree.getId()
                + "/profiles/" + mayuko.getUserId() + "/uservisibility")
                .cookie(new Cookie("s_id", "Mayuko")))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("not allowed", JsonPath.parse(response).read("$.visibility"));
    }

    // walter can view his private page
    @Test
    void testCreatorCanSeePrivateActivity() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/activities/" + activityThree.getId()
                + "/profiles/" + walter.getUserId() + "/uservisibility")
                .cookie(new Cookie("s_id", "Walter")))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("allowed", JsonPath.parse(response).read("$.visibility"));
    }

    // fabian can view public page
    @Test
    void testUserCanSeePrivateActivity() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/activities/" + activityTwo.getId()
                + "/profiles/" + fabian.getUserId() + "/uservisibility")
                .cookie(new Cookie("s_id", "Fabian")))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("allowed", JsonPath.parse(response).read("$.visibility"));
    }

    // mayuko can view public page
    @Test
    void testSharedUserCanSeePublicActivity() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/activities/" + activityTwo.getId()
                + "/profiles/" + mayuko.getUserId() + "/uservisibility")
                .cookie(new Cookie("s_id", "Mayuko")))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("allowed", JsonPath.parse(response).read("$.visibility"));
    }

    // walter can view his public page
    @Test
    void testCreatorCanSeePublicActivity() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/activities/" + activityTwo.getId()
                + "/profiles/" + walter.getUserId() + "/uservisibility")
                .cookie(new Cookie("s_id", "Walter")))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("allowed", JsonPath.parse(response).read("$.visibility"));
    }

    // fabian can't view restrictred page
    @Test
    void testUserCannotSeeRestrictedActivity() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/activities/" + activityOne.getId()
                + "/profiles/" + fabian.getUserId() + "/uservisibility")
                .cookie(new Cookie("s_id", "Fabian")))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("not allowed", JsonPath.parse(response).read("$.visibility"));
    }

    // mayuko can view restricted page (mayuko is set in userShared list in activity)
    @Test
    void testSharedUserCanSeeRestrictedActivity() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/activities/" + activityOne.getId()
                + "/profiles/" + mayuko.getUserId() + "/uservisibility")
                .cookie(new Cookie("s_id", "Mayuko")))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("allowed", JsonPath.parse(response).read("$.visibility"));
    }

    // walter can view his restricted page
    @Test
    void testCreatorCanSeeRestrictedActivity() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/activities/" + activityOne.getId()
                + "/profiles/" + walter.getUserId() + "/uservisibility")
                .cookie(new Cookie("s_id", "Walter")))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("allowed", JsonPath.parse(response).read("$.visibility"));
    }
}
