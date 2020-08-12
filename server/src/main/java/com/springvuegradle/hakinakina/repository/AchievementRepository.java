package com.springvuegradle.hakinakina.repository;

import com.springvuegradle.hakinakina.entity.Achievement;
import com.springvuegradle.hakinakina.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository for storing achievements that activities have
 */
@RepositoryRestResource
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    Achievement findAchievementById(Long id);

}
