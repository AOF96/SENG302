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
import com.springvuegradle.hakinakina.repository.LocationRepository;
import com.springvuegradle.hakinakina.util.ParserHelper;
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

    @Autowired
    LocationRepository locationRepository;

    public ActivityDeserializer() {
        this(null);
    }

    public ActivityDeserializer(Class<?> vc) {
        super(vc);
    }


    @Override
    public Activity deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        System.out.println(jp);
        JsonNode node = jp.getCodec().readTree(jp);

        // Get compulsory attributes
        String name = ParserHelper.getValueString(node, "activity_name");
        String description = ParserHelper.getValueString(node, "description");
        Set<ActivityType> activityTypes = getActivityTypes(node, "activity_types");
        boolean continuous = node.get("continuous").asBoolean();
        String startTime = ParserHelper.getValueString(node, "start_time");
        String endTime = ParserHelper.getValueString(node, "end_time");
        Visibility visibility = getVisibility(ParserHelper.getValueString(node, "visibility"));
        Set<Achievement> achievements = getAchievements(node, "achievements");

        // Create user with compulsory attributes
        Activity activity = new Activity(name, description, continuous, Timestamp.valueOf(startTime),
                Timestamp.valueOf(endTime));

        activity.setActivityTypes(activityTypes);
        activity.setVisibility(visibility);
        activity.setAchievements(achievements);
        if (node.get("location") != null) {
            Location location = ParserHelper.createLocation(node.get("location"));
            locationRepository.save(location);
            activity.setLocation(location);
        }

        return activity;
    }

    /**
     * Gets visibility object from string
     *
     * @param visibilityString string input
     * @return Visibility object
     */
    public Visibility getVisibility(String visibilityString) {
        if (visibilityString.equals("private")) {
            return Visibility.PRIVATE;
        } else if (visibilityString.equals("restricted")) {
            return Visibility.RESTRICTED;
        } else {
            return Visibility.PUBLIC;
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
