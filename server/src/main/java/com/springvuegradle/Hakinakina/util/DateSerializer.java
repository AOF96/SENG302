package com.springvuegradle.Hakinakina.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.springvuegradle.Hakinakina.entity.Email;

import java.io.IOException;
import java.sql.Date;
import java.util.Set;

public class DateSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(String.format("%s", value.toLocalDate()));
    }
}
