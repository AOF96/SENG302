package com.springvuegradle.Hakinakina.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;
import com.springvuegradle.Hakinakina.entity.ActivityType;
import com.springvuegradle.Hakinakina.entity.ActivityTypeRepository;
import com.springvuegradle.Hakinakina.entity.PassportCountry;
import com.springvuegradle.Hakinakina.entity.PassportCountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class ActivityTypeDeserializer extends StdDeserializer<Set<ActivityType>> {

    @Autowired
    ActivityTypeRepository activityTypeRepository;

    public ActivityTypeDeserializer() {
        this(null);
    }

    public ActivityTypeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Set<ActivityType> deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        Set<ActivityType> userActivityType = new HashSet<>();
        for (JsonNode node1 : node) {
            userActivityType.add(activityTypeRepository.findActivityTypeByName(node1.asText()));
        }

        return userActivityType;
    }
}
