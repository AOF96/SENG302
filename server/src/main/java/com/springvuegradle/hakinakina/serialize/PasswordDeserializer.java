package com.springvuegradle.hakinakina.serialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.springvuegradle.hakinakina.util.EncryptionUtil;
import com.springvuegradle.hakinakina.util.ErrorHandler;

import java.io.IOException;

/**
 * Ensures password is encrypted when parsed from json
 */
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