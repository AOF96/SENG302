package com.springvuegradle.hakinakina.repository;

import com.springvuegradle.hakinakina.entity.HomeFeedEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


/**
 * Repository for storing HomeFeedEntry objects
 */
@RepositoryRestResource
public interface HomeFeedRepository extends JpaRepository<HomeFeedEntry, Long> {
    @Query(value = "SELECT DISTINCT h FROM HomeFeedEntry h INNER JOIN UserActivityRole u ON u.user.userId = :profileId " +
            "WHERE (h.scope = 'PRIVATE' AND h.user.userId = :profileId) OR  (h.scope = 'ACTIVITY' AND h.activity = u.activity)" +
            "ORDER BY h.datetime DESC")
    Page<HomeFeedEntry> getUserHomeFeedById(Pageable pageable, Long profileId);

    @Query(value = "SELECT * FROM Home_Feed_Entry WHERE activity_activity_id = :activityId AND scope = 'ACTIVITY' ORDER BY datetime DESC", nativeQuery = true)
    Page<HomeFeedEntry> getChangesForActivity(Pageable pageable, Long activityId);
}
