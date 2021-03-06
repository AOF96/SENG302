package com.springvuegradle.hakinakina.repository;

import com.springvuegradle.hakinakina.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface UserActivityRoleRepository extends JpaRepository<UserActivityRole, UserActivityKey> {


    Optional<UserActivityRole> findByIdAndActivityRole(UserActivityKey key, ActivityRole activityRole);

    /**
     * Retrieves the role of user in certain activity.
     * @param activityId Id of the activity your want to look at
     * @param userId Id of an user you want to know the role for
     * @return Optional object with UserActivityRole
     */
    Optional<UserActivityRole> findByIdActivityIdAndIdUserIdAndActivityRole(Long activityId, Long userId, ActivityRole activityRole);

    /**
     * Delete the role of user in certain activity.
     * @param activityId Id of the activity your want to look at
     * @param userId Id of an user who has the role you want to delete
     */
    void deleteByIdActivityIdAndIdUserId(Long activityId, Long userId);

    @Modifying
    @Query(value = "DELETE FROM User_Activity_Role WHERE activity_id = ? AND user_id = ?",  nativeQuery = true)
    void deleteUserFromActivityRoles(Long activityId, Long userId);

    void deleteByActivity(Activity activity);

    Optional<UserActivityRole> getByActivityAndUser(Activity activity, User user);

    void deleteAllByActivityRole(ActivityRole role);
}
