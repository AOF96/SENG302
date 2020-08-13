package com.springvuegradle.hakinakina.endpoints;

import com.jayway.jsonpath.JsonPath;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    private User mayuko;
    private User walter;
    private Activity activityOne;
    private Activity activityTwo;


    @BeforeEach
    public void setUp() {
        userRepository.deleteAll(); //this deletes admin from this test file
        activityRepository.deleteAll();

        mayuko = new User();
        setupUser(mayuko, "Mayuko", null, "Williams", "mayuko@acnh.com", 0);
        walter = new User();
        setupUser(walter, "Walter", "Compiler", "Guttman", "walter@acnh.com", 0);

        activityOne = new Activity();
        setupActivity(walter,
                activityOne,
                "Learn DFA",
                "symbol or string or language",
                true,
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now()),
                "Christchurch, New Zealand",
                Visibility.RESTRICTED
        );

        activityTwo = new Activity();
        setupActivity(walter,
                activityTwo,
                "Walk to Erskine",
                "Labs are there",
                true,
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now()),
                "Christchurch, New Zealand",
                Visibility.PUBLIC
        );
    }

    /**
     * sets up user for this test file
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
     * sets up activity for this test file
     */
    void setupActivity(User user,
                       Activity activity,
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

        user.addActivitiesShared(activity);
        userRepository.save(user);

        long aId = activity.getId();
        long uId = user.getUserId();
        UserActivityKey userActivityKey = new UserActivityKey(uId, aId);
        userActivityRoleRepository.save(new UserActivityRole(userActivityKey, ActivityRole.CREATOR));

        

        activityService.updateActivityVisibility(uId,aId, "Walter",request);

    }

    @Test
    void testCreatorCanSeeActivity() throws Exception{
        MvcResult mvcResult = mockMvc.perform(get("/activities/" + activityOne.getId()
                + "/profiles/" + walter.getUserId() + "/uservisibility")
                .cookie(new Cookie("s_id", "Walter")))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("allowed", JsonPath.parse(response).read("$.visibility"));
    }
}

