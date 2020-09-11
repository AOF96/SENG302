package com.springvuegradle.hakinakina.deserializer_tests;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.springvuegradle.hakinakina.controller.ActivityController;
import com.springvuegradle.hakinakina.controller.UserController;
import com.springvuegradle.hakinakina.entity.Activity;
import com.springvuegradle.hakinakina.entity.Location;
import com.springvuegradle.hakinakina.repository.LocationRepository;
import com.springvuegradle.hakinakina.serialize.ActivityDeserializer;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ActivityDeserializerTest {
    @InjectMocks
    ActivityDeserializer deserializer;

    @Mock
    private LocationRepository locationRepository;

    @BeforeAll
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

//    @Test
//    public void deserializeTest() throws IOException {
//        String json = "{\n" +
//                "    \"activity_name\":\"Cool Activity\",\n" +
//                "    \"continuous\":false,\n" +
//                "    \"start_time\":\"2020-09-12 09:00:00\",\n" +
//                "    \"end_time\":\"2020-09-13 17:00:00\",\n" +
//                "    \"description\":\"This is a very cool Activity.\",\n" +
//                "    \"location\": {\n" +
//                "        \"street_address\": \"48 Somewhere Road\",\n" +
//                "        \"suburb\": \"Ilam\",\n" +
//                "        \"postcode\": 8000,\n" +
//                "        \"city\": \"Christchurch\",\n" +
//                "        \"state\": \"Canterbury\",\n" +
//                "        \"country\": \"New Zealand\",\n" +
//                "        \"latitude\": 0.0,\n" +
//                "        \"longitude\": 0.0\n" +
//                "    },  \n" +
//                "    \"activity_type\":[\"Fun\",\"Adventurous\"],\n" +
//                "    \"visibility\":\"private\"\n" +
//                "}";
//
//        JsonFactory factory = new JsonFactory();
//        JsonParser parser = factory.createParser(json);
//        ObjectCodec codec = new ObjectMapper();
//        parser.setCodec(codec);
//        Location dummyLocation = new Location(null, null, null, 0, null,
//                null, 0, 0);
//        when(locationRepository.save(any(Location.class))).thenReturn(dummyLocation);
//        Activity activity = deserializer.deserialize(parser, null);
//
//        System.out.println(activity);
//    }
}
