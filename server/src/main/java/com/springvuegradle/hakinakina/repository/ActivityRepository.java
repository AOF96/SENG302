package com.springvuegradle.hakinakina.repository;

import com.springvuegradle.hakinakina.dto.UserRolesDto;
import com.springvuegradle.hakinakina.entity.Activity;
import com.springvuegradle.hakinakina.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/**
 * Repository for storing activities that the user can perform.
 */
@RepositoryRestResource
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Activity findActivityById(Long id);

    @Query(nativeQuery = true, value = "SELECT count(*) FROM User_Activities_Shared WHERE user_id = ?1 AND activity_id = ?2")
    int findUserActivityVisibility(Long userId, Long activity);

    Optional<Activity> findFirstByName(String name);

    @Query(value = "select * from Activity where activity_id = ?", nativeQuery = true)
    Optional<Activity> getActivityById(long activityId);

    @Modifying
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

    @Query(value = "SELECT * FROM User u WHERE u.permission_level != 2 AND u.user_id IN (SELECT c.user_id FROM User_Activities_Shared c WHERE c.activity_id = ?)", nativeQuery = true)
    List<User> getSharedUsers(Long activityId);

    @Query(value = "SELECT * FROM User_Activities_Shared  WHERE activity_id = ? AND user_id = ?", nativeQuery = true)
    List<Activity> getSharedActivitiesForAuthorOfType(Long activityId, Long userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM User_Activities", nativeQuery = true)
    void removeAllUserActivities();

    @Query(value = "SELECT count(*) FROM User_Activities WHERE activity_id = ?", nativeQuery = true)
    int getNumFollowersForActivity(long activityId);

    @Query(value = "SELECT count(*) FROM User_Activity_Role WHERE activity_id = ? AND activityRole = 'PARTICIPANT'", nativeQuery = true)
    int getNumParticipantsForActivity(long activityId);

    @Query(value = "SELECT count(*) FROM User_Activity_Role WHERE activity_id = ? AND activityRole = 'ORGANISER'", nativeQuery = true)
    int getNumOrganisersForActivity(long activityId);

    @Query(value = "SELECT * FROM User_Activities_Shared  WHERE activity_id = ? AND author_user_id = ?", nativeQuery = true)
    List<Activity> getSharedActivitiesForAuthorOfType(Long activityId, Long userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM User_Activities", nativeQuery = true)
    void removeAllUserActivities();
}
