package com.springvuegradle.hakinakina.repository;

import com.springvuegradle.hakinakina.entity.ActivityChange;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


/**
 * Repository for storing ActivityChange objects
 */
@RepositoryRestResource
public interface ActivityChangeRepository extends JpaRepository<ActivityChange, Long> {
    @Query(value = "SELECT * FROM Activity_Change INNER JOIN User_Activities ON user_id = ? " +
            "AND activity_id = activity_activity_id ORDER BY activity_change_id DESC", nativeQuery = true)
    Page<ActivityChange> getUserHomeFeedById(@Param(value="pageable") Pageable pageable, @Param(value="profileId") Long profileId);

    @Query(value = "SELECT * FROM Activity_Change WHERE activity_activity_id = :activityId ORDER BY activity_change_id DESC", nativeQuery = true)
    Page<ActivityChange> getChangesForActivity(@Param(value="pageable") Pageable pageable, @Param(value="activityId") Long activityId);
}
