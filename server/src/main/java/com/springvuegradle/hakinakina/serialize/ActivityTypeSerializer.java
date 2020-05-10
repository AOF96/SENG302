package com.springvuegradle.hakinakina.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.springvuegradle.hakinakina.entity.ActivityType;

import java.io.IOException;
import java.util.Set;

/**
 * Formats activity types when serializing a set of ActivityType objects
 */
public class ActivityTypeSerializer extends JsonSerializer<Set<ActivityType>> {

    @Override
    public void serialize(Set<ActivityType> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();
        for (ActivityType activityType : value) {
            gen.writeString(activityType.getName());
        }
        gen.writeEndArray();
    }
}
