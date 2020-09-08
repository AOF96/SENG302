package com.springvuegradle.hakinakina.repository;

import com.springvuegradle.hakinakina.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for storing locations
 */
public interface LocationRepository extends JpaRepository<Location, Long> {
    //Will be used for retrieving locations for users and activities
}
