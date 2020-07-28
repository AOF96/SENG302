package com.springvuegradle.hakinakina.repository;

import com.springvuegradle.hakinakina.entity.ActivityChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository for storing ActivityChange objects
 */
@RepositoryRestResource
public interface ActivityChangeRepository extends JpaRepository<ActivityChange, Long> {
}
