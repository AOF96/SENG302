package com.springvuegradle.Hakinakina.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ActivityTypeRepository extends JpaRepository<ActivityType, Long> {
    @Query(value = "Select * from ActivityType c where c.name = ?1", nativeQuery = true)
    ActivityType findActivityTypeByName(String name);
}
