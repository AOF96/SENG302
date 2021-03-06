package com.springvuegradle.hakinakina.serialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.springvuegradle.hakinakina.entity.PassportCountry;
import com.springvuegradle.hakinakina.repository.PassportCountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Parses a set of PassportCountry objects from json
 */
@Service
public class CountryDeserializer extends StdDeserializer<Set<PassportCountry>> {

    @Autowired
    PassportCountryRepository countryRepository;

    public CountryDeserializer() {
        this(null);
    }

    public CountryDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Set<PassportCountry> deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        Set<PassportCountry> userCountries = new HashSet<>();
        for (JsonNode node1 : node) {
            userCountries.add(countryRepository.findCountryByName(node1.asText()));
        }

        return userCountries;
    }
}
