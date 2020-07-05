package com.springvuegradle.hakinakina.repository;

import com.springvuegradle.hakinakina.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value = "select user_id from User where primary_email = ?", nativeQuery = true)
    String getIdByEmail(String email);


    /**
     * Retrieves users based on the following query parameters
     * This returns the users' primary email, full name (first, middle and last name) and nickname in a Page object
     * @param pageable abstract interface for pagination information, if provided page object is sent back
     * @param email email of the user you are searching for
     * @param fullname full name of the user you are searching for
     * @param lastname last name of the user you are searching for
     * @return Page object with list of users with the query search
     */
    @Query(value = "FROM User u " +
            "WHERE u.primaryEmail like ?1% " +
            "OR concat(u.firstName, ' ', u.lastName) like ?2% " +
            "OR u.lastName like ?3%")
    Page<User> findAllByQuery(Pageable pageable, String email, String fullname, String lastname);

    /**
     * Retrieves users based on the following query parameters
     * This returns the users' primary email, full name (first, middle and last name) and nickname
     * Provides the startIndex and size of the result set
     */
    @Query(value = "SELECT u.primary_email as primary_email, u.first_name as firstname, u.last_name as lastname, u.middle_name as middlename, u.nick_name as nickname " +
            "FROM User u " +
            "WHERE (:email is null or u.primary_email = :email) " +
            "AND (:nickname is null or u.nick_name = :nickname) " +
            "AND (:fullname is null or u.first_name LIKE %:fullname% or u.middle_name LIKE %:fullname% or u.last_name LIKE %:fullname%) " +
            "LIMIT :startIndex, :size " +
            "FOR JSON AUTO;", nativeQuery = true)

    List<Object> searchForUser(@Param("email") String email,
                               @Param("nickname") String nickname,
                               @Param("fullname") String fullname,
                               int startIndex,
                               @Param("size") int size);

    /***
     * Query that updated the database to set the permission level to 1 of the user being promoted to admin.
     * @param userID the id of the user being promoted
     */
    @Query(value = "UPDATE User SET permission_level = 1 WHERE user_id = ?", nativeQuery = true)
    User grantAdminRights(Long userID);
}
