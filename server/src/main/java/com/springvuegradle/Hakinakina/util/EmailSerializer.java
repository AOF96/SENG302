package com.springvuegradle.Hakinakina.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.springvuegradle.Hakinakina.entity.Email;
import com.springvuegradle.Hakinakina.entity.PassportCountry;

import java.io.IOException;
import java.util.Set;

public class EmailSerializer extends JsonSerializer<Set<Email>> {

    @Override
    public void serialize(Set<Email> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();
        for (Email email : value) {
            gen.writeString(email.getEmail());
        }
        gen.writeEndArray();
    }
}
