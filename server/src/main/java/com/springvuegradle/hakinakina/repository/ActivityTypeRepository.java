package com.springvuegradle.hakinakina.repository;

import com.springvuegradle.hakinakina.entity.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ActivityTypeRepository extends JpaRepository<ActivityType, Long> {
    @Query(value = "Select * from Activity_Type a where a.type_id = ?1", nativeQuery = true)
    ActivityType findActivityTypeByName(String name);
}
