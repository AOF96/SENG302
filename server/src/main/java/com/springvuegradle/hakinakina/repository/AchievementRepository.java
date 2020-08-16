package com.springvuegradle.hakinakina.repository;

import com.springvuegradle.hakinakina.entity.Achievement;
import com.springvuegradle.hakinakina.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Repository for storing achievements that activities have
 */
@RepositoryRestResource
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    Achievement findAchievementById(Long id);

    //ToDO Remove this once the email table has been fixed.
    @Query(value = "select * from Achievement where activity_activity_id = ?1", nativeQuery = true)
    List<Achievement> getAchievementsByActivityId(Long id);

}
