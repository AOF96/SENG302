package com.springvuegradle.Hakinakina.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.springvuegradle.Hakinakina.entity.PassportCountry;
import com.springvuegradle.Hakinakina.entity.PassportCountryRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class PasswordDeserializer extends StdDeserializer<String> {


    public PasswordDeserializer() {
        this(null);
    }

    public PasswordDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public String deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        String salt = null;
        String password = null;

        try {
            salt = EncryptionUtil.getNewSalt();
            password = EncryptionUtil.getEncryptedPassword(node.asText(), salt);
        } catch (Exception e) {
            ErrorHandler.printProgramException(e, "Error while creating password.");
        }

        return password;
    }
}