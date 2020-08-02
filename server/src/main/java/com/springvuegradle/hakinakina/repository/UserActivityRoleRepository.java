package com.springvuegradle.hakinakina.repository;

import com.springvuegradle.hakinakina.entity.ActivityRole;
import com.springvuegradle.hakinakina.entity.UserActivityKey;
import com.springvuegradle.hakinakina.entity.UserActivityRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface UserActivityRoleRepository extends JpaRepository<UserActivityRole, UserActivityKey> {

    Optional<UserActivityRole> findByIdActivityIdAndIdUserIdAndActivityRole(Long activityId, Long userId, ActivityRole activityRole);

    void deleteByIdActivityIdAndIdUserId(Long activityId, Long userId);
}
