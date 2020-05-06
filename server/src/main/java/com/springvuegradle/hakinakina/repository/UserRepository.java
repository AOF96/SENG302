package com.springvuegradle.hakinakina.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

/**
 * Repository for storing users
 */
@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "Select * from User u where u.primary_email = ?1", nativeQuery = true)
    User findUserByEmail(String email);

    //ToDO Remove this once the email table has been fixed.
    @Query(value = "select primary_email from User", nativeQuery = true)
    List<String> getAllPrimaryEmails();

    // Automatically generates query that finds user based on their permission level :D
    User findByPermissionLevelEquals(int permissionLevel);

    @Query(value = "select * from User where user_id = ?", nativeQuery = true)
    Optional<User> getUserById(long profileId);
}
