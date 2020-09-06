package com.springvuegradle.hakinakina.repository;

import com.springvuegradle.hakinakina.entity.ActivityChange;
import com.springvuegradle.hakinakina.entity.HomeFeedEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


/**
 * Repository for storing ActivityChange objects
 */
@RepositoryRestResource
public interface HomeFeedRepository extends JpaRepository<HomeFeedEntry, Long> {
    @Query(value = "(SELECT * FROM Home_Feed_Entry INNER JOIN User_Activity_Role ON user_id = :profileId " +
            "AND activity_id = activity_activity_id WHERE scope = 'ACTIVITY') " +
            "UNION " +
            "(SELECT * FROM Home_Feed_Entry WHERE scope = 'PRIVATE' AND user_user_id = :profileId) " +
            "ORDER BY datetime DESC", nativeQuery = true)
    Page<HomeFeedEntry> getUserHomeFeedById(Pageable pageable, Long profileId);

    @Query(value = "SELECT * FROM Home_Feed_Entry WHERE activity_activity_id = :activityId AND scope = 'ACTIVITY' ORDER BY datetime DESC", nativeQuery = true)
    Page<HomeFeedEntry> getChangesForActivity(Pageable pageable, Long activityId);
}
