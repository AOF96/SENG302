package com.springvuegradle.hakinakina.repository;

import com.springvuegradle.hakinakina.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Retrieves users based on the following query parameters
     * This returns the users' primary email, full name (first, middle and last name) and nickname
     * Provides the startIndex and size of the result set
     */
    @Query(value = "SELECT u.primary_email, u.first_name, u.last_name, u.middle_name, u.nick_name " +
            "FROM User u " +
            "WHERE (:email is null or u.primary_email = :email) " +
            "AND (:nickname is null or u.nick_name = :nickname) " +
            "AND (:fullname is null or u.first_name LIKE %:fullname% or u.middle_name LIKE %:fullname% or u.last_name LIKE %:fullname%) " +
            "LIMIT :startIndex, :size", nativeQuery = true)
    List<String> searchForUser(@Param("email") String email,
                               @Param("nickname") String nickname,
                               @Param("fullname") String fullname,
                               int startIndex,
                               @Param("size") int size);
}
