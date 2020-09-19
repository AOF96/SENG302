package com.springvuegradle.hakinakina;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.hakinakina.entity.Location;
import com.springvuegradle.hakinakina.entity.User;
import com.springvuegradle.hakinakina.repository.UserRepository;
import com.springvuegradle.hakinakina.serialize.UserDeserializer;
import org.junit.jupiter.api.BeforeAll;
import com.springvuegradle.hakinakina.util.ParserHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDeserializerTest {

    @InjectMocks
    private UserDeserializer deserializer = new UserDeserializer();

    @Mock
    private UserRepository userRepository;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getLocationNewTest() throws JsonProcessingException {
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
        User testUser = new User();

        when(userRepository.findUserByEmail("email@email.com")).thenReturn(testUser);

        Location otherLocation = ParserHelper.createLocation(jsonNode.get("location"));
        assertEquals(testLocation, otherLocation);
    }

    @Test
    public void getLocationAlreadyExistTest() throws JsonProcessingException {
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
        User testUser = new User();
        testUser.setLocation(new Location());

        when(userRepository.findUserByEmail("email@email.com")).thenReturn(testUser);

        Location otherLocation = ParserHelper.createLocation(jsonNode.get("location"));
        assertEquals(testLocation, otherLocation);
    }

    @Test
    public void updateLocationAlreadyExistTest() throws JsonProcessingException {
        String json = "{\n" +
                "  \"location\": {\n" +
                "    \"street_address\": \"47 Somewhere Road\",\n" +
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

        Location testLocation = new Location("47 Somewhere Road", "Ilam", "Christchurch",
                8000, "Canterbury", "New Zealand", 0.0, 0.0);

        User testUser = new User();
        testUser.setLocation(testLocation);

        when(userRepository.findUserByEmail("email@email.com")).thenReturn(testUser);

        Location otherLocation = ParserHelper.createLocation(jsonNode.get("location"));
        assertEquals(testLocation.getStreetAddress(), otherLocation.getStreetAddress());
    }
}
