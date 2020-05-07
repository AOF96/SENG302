package com.springvuegradle.Hakinakina.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.springvuegradle.Hakinakina.entity.ActivityType;
import com.springvuegradle.Hakinakina.entity.PassportCountry;

import java.io.IOException;
import java.util.Set;

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
