package com.springvuegradle.hakinakina.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.springvuegradle.hakinakina.entity.Email;

import java.io.IOException;
import java.util.Set;

/**
 * Correctly formats emails during serialization of a set of Email objects to json
 */
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
