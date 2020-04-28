package com.springvuegradle.Hakinakina;

import com.springvuegradle.Hakinakina.controller.ActivityService;
import com.springvuegradle.Hakinakina.entity.Activity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ActivityServiceTest {
    @InjectMocks
    private ActivityService service;

    @BeforeAll
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getActivitySummariesTest() {
        Activity activity1 = new Activity("Climb Mount Everest", "Let's climb Mount Everest together",
                true, new Date(2021, 10, 10), new Date(2021, 10, 11),
                "Mount Everest");
        activity1.setId((long) 1);

        Activity activity2 = new Activity("Descend Mount Everest", "Let's descend Mount Everest together",
                true, new Date(2021, 10, 11), new Date(2021, 10, 12),
                "Mount Everest");
        List<Activity> activities = new ArrayList<>();
        activity2.setId((long) 2);

        activities.add(activity1);
        activities.add(activity2);

        List<Map<String, String>> summaries = service.getActivitySummaries(activities);
        assertEquals(2, summaries.size());

        assertEquals(3, summaries.get(0).size());
        assertEquals("Climb Mount Everest", summaries.get(0).get("name"));
        assertEquals("Let's climb Mount Everest together", summaries.get(0).get("description"));
        assertEquals("1", summaries.get(0).get("id"));

        assertEquals(3, summaries.get(1).size());
        assertEquals("Descend Mount Everest", summaries.get(1).get("name"));
        assertEquals("Let's descend Mount Everest together", summaries.get(1).get("description"));
        assertEquals("2", summaries.get(1).get("id"));
    }
}

