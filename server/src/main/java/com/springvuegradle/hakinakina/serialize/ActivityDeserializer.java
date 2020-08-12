package com.springvuegradle.hakinakina.serialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.springvuegradle.hakinakina.entity.*;
import com.springvuegradle.hakinakina.repository.AchievementRepository;
import com.springvuegradle.hakinakina.repository.ActivityTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

/**
 * Parses a User object from json.
 * Could not use default spring parser because of specific missing attribute handling and converting gender to an object
 */
@Service
public class ActivityDeserializer extends StdDeserializer<Activity>  {

    @Autowired
    ActivityTypeRepository activityTypeRepository;

    @Autowired
    AchievementRepository achievementRepository;

    public ActivityDeserializer() {
        this(null);
    }

    public ActivityDeserializer(Class<?> vc) {
        super(vc);
    }


    @Override
    public Activity deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        // Get compulsory attributes
        String name = getValueString(node, "activity_name");
        String description = getValueString(node, "description");
        Set<ActivityType> activityTypes = getActivityTypes(node, "activity_types");
        boolean continuous = node.get("continuous").asBoolean();
        String startTime = getValueString(node, "start_time");
        String endTime = getValueString(node, "end_time");
        String location = getValueString(node, "location");
        Set<Achievement> achievements = getAchievements(node, "achievements");

        String city;
        String state;
        String country;
        List<String> locationSplit = Arrays.asList(location.split("\\s*,\\s*"));
        if (locationSplit.size() == 3) {
            city = locationSplit.get(0);
            state = locationSplit.get(1);
            country = locationSplit.get(2);
        } else {
            city = locationSplit.get(0);
            state = null;
            country = locationSplit.get(1);
        }

        // Create user with compulsory attributes
        Activity activity = new Activity(name, description, continuous, Timestamp.valueOf(startTime),
                Timestamp.valueOf(endTime), location);

        activity.setActivityTypes(activityTypes);
        activity.setAchievements(achievements);
        return activity;
    }

    /**
     * Returns value of field if it exists
     *
     * @param node
     * @param field
     * @return string value or empty string
     */
    public String getValueString(JsonNode node, String field) {
        JsonNode fieldValue = node.get(field);
        if (fieldValue == null) {
            return null;
        } else if (fieldValue.asText().equals("null")) {
            return null;
        } else {
            return fieldValue.asText();
        }
    }

    /**
     * Returns set of ActivityTypes in user creation request
     *
     * @param node
     * @param field
     * @return
     */
    public Set<ActivityType> getActivityTypes(JsonNode node, String field) {
        JsonNode activityTypeNodes = node.get(field);
        if (activityTypeNodes == null) {
            return new HashSet<>();
        } else {
            Set<ActivityType> userActivityTypes = new HashSet<>();
            for (JsonNode activityTypeNode : activityTypeNodes) {
                userActivityTypes.add(activityTypeRepository.findActivityTypeByName(activityTypeNode.asText()));
            }
            return userActivityTypes;
        }
    }

    public Set<Achievement> getAchievements(JsonNode node, String field) {
        JsonNode achievementsNodes = node.get(field);
        if (achievementsNodes == null) {
            return new HashSet<>();
        } else {
            Set<Achievement> achievements = new HashSet<>();
            for (JsonNode achievementNode : achievementsNodes) {
                achievements.add(achievementRepository.findById(achievementNode.asLong()).get());
            }
            return achievements;
        }
    }
}
