package com.springvuegradle.hakinakina.repository;

import com.springvuegradle.hakinakina.entity.Activity;
import com.springvuegradle.hakinakina.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Repository for storing activities that the user can perform.
 */
@RepositoryRestResource
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Activity findActivityById(Long id);

    @Query(value = "INSERT INTO User_Activities (user_id, activity_id) values (?, ?);", nativeQuery = true)
    void insertActivityForUser(Long userId, Long activityId);

    @Query(value = "SELECT * FROM Activity a WHERE a.author_user_id = ? AND a.activity_id = ?", nativeQuery = true)
    Activity validateAuthor(Long userId, Long activity_id);

    @Query(value = "DELETE FROM User_Activities WHERE user_id = ? AND activity_id = ?", nativeQuery = true)
    void deleteActivityForUser(Long userId, Long activityId);

    @Query(value = "SELECT * FROM Activity a WHERE a.continuous = ? AND a.activity_id IN (SELECT u.activity_id FROM User_Activities u WHERE u.user_id = ?)", nativeQuery = true)
    List<Activity> getActivitiesForUserOfType(boolean isContinuous, Long id);

    @Query(value = "SELECT * FROM Activity a WHERE a.continuous = ? AND author_user_id = ?", nativeQuery = true)
    List<Activity> getActivitiesForAuthorOfType(boolean isContinuous, Long id);

    @Query(value = "SELECT * FROM User u WHERE u.permission_level != 2 AND u.user_id IN (SELECT c.user_id FROM User_Activity_Shared WHERE c.activity_id = ?)", nativeQuery = true)
    List<User> getSharedUsers(Long activityId);

}
