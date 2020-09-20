package com.springvuegradle.hakinakina.entity_tests;



import com.springvuegradle.hakinakina.entity.Location;
import com.springvuegradle.hakinakina.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class LocationTests {
    @Autowired
    public LocationRepository locationRepository;

    @BeforeEach
    public void clearRepository() {
        locationRepository.deleteAll();
    }

   /* @Test
    public void createLocation() {
        Location testLocation = new Location("University of Canterbury", "Upper Riccarton",
                "Christchurch", 8041, "Canterbury", "New Zealand",
                -43.522447, 172.579442);
        testLocation.setId(1L);
        locationRepository.save(testLocation);
        assertEquals(testLocation.getStreetAddress(), locationRepository.findAll().get(0).getStreetAddress());
        assertEquals(testLocation.getCity(), locationRepository.findAll().get(0).getCity());
        assertEquals(testLocation.getPostcode(), locationRepository.findAll().get(0).getPostcode());
        assertEquals(testLocation.getLatitude(), locationRepository.findAll().get(0).getLatitude());
        assertEquals(testLocation.getLongitude(), locationRepository.findAll().get(0).getLongitude());
    }*/
}
