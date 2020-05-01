package com.springvuegradle.Hakinakina.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository for storing activities that the user can perform.
 */
@RepositoryRestResource
public interface ActivityRepository extends JpaRepository<Activity, Long> {

}
