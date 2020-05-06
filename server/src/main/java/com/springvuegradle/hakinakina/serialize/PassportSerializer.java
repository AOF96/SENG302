package com.springvuegradle.hakinakina.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.springvuegradle.hakinakina.entity.PassportCountry;

import java.io.IOException;
import java.util.Set;

public class PassportSerializer extends JsonSerializer<Set<PassportCountry>> {

    @Override
    public void serialize(Set<PassportCountry> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();
        for (PassportCountry passportCountry : value) {
            gen.writeString(passportCountry.getName());
        }
        gen.writeEndArray();
    }
}
