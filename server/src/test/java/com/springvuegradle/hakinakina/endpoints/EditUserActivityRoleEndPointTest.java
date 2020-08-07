package com.springvuegradle.hakinakina.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.springvuegradle.hakinakina.dto.EditActivityRoleDto;
import com.springvuegradle.hakinakina.dto.EditSubscriberRoleDto;
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

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EditUserActivityRoleEndPointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserActivityRoleRepository userActivityRoleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    private User mayuko = new User();
    private User fabian = new User();
    private Activity mayukosActivity = new Activity();

    @BeforeEach
    public void setup() {
        userActivityRoleRepository.deleteAll();
        activityRepository.deleteAll();
        userRepository.deleteAll();
        setupUser(mayuko, "Mayuko", null, "Williams", "mayuko@acnh.com", 0);
        setupUser(fabian, "Fabian", null,"Gibson", "gibson@acnh.com", 0);
        setupActivity(mayukosActivity);
        activityService.addActivity(mayukosActivity, mayuko.getUserId(), "Mayuko");
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

    void setupActivity(Activity activity) {
        Date startDate1 = new Date(2021, 10, 10);
        Date endDate1 = new Date(2021, 10, 12);
        activity.setName("Climb Mount Everest");
        activity.setDescription("Let's climb Mount Everest together");
        activity.setContinuous(true);
        activity.setStartTime(new Timestamp(startDate1.getTime()));
        activity.setEndTime(new Timestamp(endDate1.getTime()));
        activity.setLocation("Mount Everest");
        Set<ActivityType> activityTypes = Set.of(new ActivityType("Extreme"));
        activity.setActivityTypes(activityTypes);

        activityRepository.save(mayukosActivity);
    }

    @Test
    void testCreatorChangeActivityRoleSuccessful() throws Exception {
        // changing fabian's role in activity
        EditSubscriberRoleDto subDto = new EditSubscriberRoleDto(fabian.getPrimaryEmail(), ActivityRole.ORGANISER);
        EditActivityRoleDto actDto = new EditActivityRoleDto((subDto));

        mockMvc.perform(put("/profiles/" + mayuko.getUserId() + "/activities/" + mayukosActivity.getId() + "/subscriber")
                .cookie(new Cookie("s_id", "Mayuko"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(actDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testChangeActivityRoleByNotCreatorShouldBeUnsuccessful() throws Exception{

        EditSubscriberRoleDto subDto = new EditSubscriberRoleDto(mayuko.getPrimaryEmail(), ActivityRole.ORGANISER);
        EditActivityRoleDto actDto = new EditActivityRoleDto((subDto));

        mockMvc.perform(put("/profiles/" + fabian.getUserId() + "/activities/" + mayukosActivity.getId() + "/subscriber")
                .cookie(new Cookie("s_id", "Fabian"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(actDto)))
                .andExpect(status().isForbidden());
    }
}
