package com.springvuegradle.hakinakina;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.hakinakina.entity.Location;
import com.springvuegradle.hakinakina.serialize.UserDeserializer;
import com.springvuegradle.hakinakina.util.ParserHelper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ParserHelperTest {
    @Test
    public void getLocationTest() throws JsonProcessingException {
        String json = "{\n" +
                "  \"location\": {\n" +
                "    \"street_address\": \"48 Somewhere Road\",\n" +
                "    \"suburb\": \"Ilam\",\n" +
                "    \"postcode\": 8000,\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"state\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"latitude\": 0.0,\n" +
                "    \"longitude\": 0.0\n" +
                "  }  \n" +
                "}";

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);

        Location testLocation = new Location("48 Somewhere Road", "Ilam", "Christchurch",
                8000, "Canterbury", "New Zealand", 0.0, 0.0);
        Location otherLocation = ParserHelper.createLocation(jsonNode.get("location"));
        assertEquals(testLocation, otherLocation);
    }
}
