package com.springvuegradle.hakinakina.deserializer_tests;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.springvuegradle.hakinakina.entity.Activity;
import com.springvuegradle.hakinakina.entity.Location;
import com.springvuegradle.hakinakina.repository.LocationRepository;
import com.springvuegradle.hakinakina.serialize.ActivityDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

public class ActivityDeserializerTest {
    private ActivityDeserializer deserializer = new ActivityDeserializer();

//    @Test
//    public void deserializeTest() throws IOException {
//        String json = "{\n" +
//                "    \"activity_name\":\"Cool Activity\",\n" +
//                "    \"continuous\":false,\n" +
//                "    \"start_time\":\"2020-09-12T09:00:00\",\n" +
//                "    \"end_time\":\"2020-09-13T17:00:00\",\n" +
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
//        doNothing().when(any(LocationRepository.class)).save(any(Location.class));
//        Activity activity = deserializer.deserialize(parser, null);
//
//        System.out.println(activity);
//    }
}
