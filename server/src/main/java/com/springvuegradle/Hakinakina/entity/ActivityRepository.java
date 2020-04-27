package com.springvuegradle.Hakinakina.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository for storing activities that the user can perform.
 */
@RepositoryRestResource
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Activity findActivityById(Long id);

    @Query(value = "SELECT * FROM User_Activities u WHERE u.user_id = ? AND u.activity_id = ?", nativeQuery = true)
    Activity validateAuthor(Long userId, Long activity_id);

    @Query(value = "DELETE FROM User_Activities WHERE user_id = ? AND activity_id = ?", nativeQuery = true)
    void deleteUser_ActivitiesValue(Long userId, Long activityId);

    @Query(value = "DELETE FROM Activity_ActivityType WHERE activity_id = ?", nativeQuery = true)
    void deleteActivity_ActivityTypeValue(Long activityId);

    @Query(value = "DELETE FROM Activity WHERE activity_id = ?", nativeQuery = true)
    void deleteActivityById(Long id);

}
