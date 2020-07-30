package com.springvuegradle.hakinakina.repository;

import com.springvuegradle.hakinakina.entity.ActivityType;
import com.springvuegradle.hakinakina.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository for storing users
 */
@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    @Query(value = "Select * from User u where u.primary_email = ?1", nativeQuery = true)
    User findUserByEmail(String email);

    //ToDO Remove this once the email table has been fixed.
    @Query(value = "select primary_email from User", nativeQuery = true)
    List<String> getAllPrimaryEmails();
    
    @Query(value = "select * from User where permission_level = ?1", nativeQuery = true)
    User findByPermissionLevelEquals(int permissionLevel);

    @Query(value = "select * from User where user_id = ?", nativeQuery = true)
    Optional<User> getUserById(long profileId);

    @Query(value = "select user_id from User where primary_email = ?", nativeQuery = true)
    String getIdByEmail(String email);

    @Query(value = "delete from User where user_id = ?", nativeQuery = true)
    void deleteUserById(Long profileId);

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
            "WHERE u.permissionLevel < 2" +
            "AND (u.primaryEmail like %:email% " +
            "OR concat(u.firstName, ' ', u.lastName) like %:fullname% " +
            "OR u.lastName like %:lastname%)")
    Page<User> findAllByQuery(Pageable pageable, String email, String fullname, String lastname);

    @Query(value = "FROM User u " +
            "WHERE u.primaryEmail = ?1 " +
            "OR concat(u.firstName, ' ', u.lastName) like ?2 " +
            "OR u.lastName = ?3")
    Page<User> findAllByQueryWithQuotation(Pageable pageable, String email, String fullname, String lastname);

 /**
     * Retrieves users based on the following query parameters. Users are retrieved with Activity Types that match any
     * of those in the userActivityTypes set (OR).
     * This returns the users' primary email, full name (first, middle and last name) and nickname in a Page object
     * @param pageable abstract interface for pagination information, if provided page object is sent back
     * @param email email of the user you are searching for
     * @param fullname full name of the user you are searching for
     * @param lastname last name of the user you are searching for
     * @param userActivityTypes set of activities of user you are searching for
     * @return Page object with list of users with the query search
     */
    @Query(nativeQuery = true, value = "SELECT DISTINCT(a.user_id) FROM User u " +
            "INNER JOIN User_ActivityTypes a ON u.user_id = a.user_id " +
            "WHERE (:email IS NULL OR u.primary_email like :email%) " +
            "AND (:fullname IS NULL OR concat(u.first_name, ' ', u.last_name) like :fullname%) " +
            "AND (:lastname IS NULL OR u.last_name like :lastname%) " +
            "AND a.type_id IN :userActivityTypes " +
            "AND u.permission_level < 2")
    Page<User> findAllByActivityTypesOR(Pageable pageable, String email, String fullname, String lastname, Set<ActivityType> userActivityTypes);

    /**
     * Retrieves users based on the following query parameters
     * This returns the users' primary email, full name (first, middle and last name) and nickname
     * Provides the startIndex and size of the result set
     * @param email email address of the user
     * @param nickname nickname of the user
     * @param fullname full name of the user
     * @param startIndex index of results
     * @param size size of results needed
     * @return List of users
     */
    @Query(value = "SELECT u.primary_email as primary_email, u.first_name as firstname, u.last_name as lastname, u.middle_name as middlename, u.nick_name as nickname " +
            "FROM User u " +
            "WHERE (:email is null or u.primary_email = :email%) " +
            "AND (:nickname is null or u.nick_name = :nickname%) " +
            "AND (:fullname is null or u.first_name LIKE :fullname% or u.middle_name LIKE :fullname% or u.last_name LIKE :fullname%) " +
            "LIMIT :startIndex, :size " +
            "FOR JSON AUTO;", nativeQuery = true)
    List<Object> searchForUser(String email,
                               String nickname,
                               String fullname,
                               int startIndex,
                               @Param("size") int size);

    /**
     * Retrieves users based on the following query parameters. Users are retrieved with Activity Types that match all
     * of those in the activityTypes set (AND)
     * This returns the users' primary email, full name (first, middle and last name) and nickname in a Page object
     * @param pageable abstract interface for pagination information, if provided page object is sent back
     * @param email email of the user you are searching for
     * @param fullname full name of the user you are searching for
     * @param lastname last name of the user you are searching for
     * @param activityTypes set of activities of user you are searching for
     * @return Page object with list of users with the query search
     */
   @Query(value = "SELECT DISTINCT(a.user_id) FROM User_ActivityTypes a "
            + "INNER JOIN User u ON a.user_id = u.user_id "
            + "WHERE a.user_id NOT IN (SELECT user_id "
            + "FROM ( "
            + " (SELECT user_id, type_id FROM "
            + " (SELECT type_id FROM Activity_Type WHERE type_id IN :activityTypes) AS all_activity_types "
            + "CROSS JOIN "
            + " (SELECT DISTINCT user_id FROM User_ActivityTypes) AS all_users_linked_to_activities) "
            + "EXCEPT "
            + "(SELECT user_id, type_id FROM User_ActivityTypes) ) AS all_users_we_dont_need ) "
            + "AND (:email IS NULL OR u.primary_email like :email%) "
            + "AND (:fullname IS NULL OR concat(u.first_name, ' ', u.last_name) like :fullname%) "
            + "AND (:lastname IS NULL OR u.last_name like :lastname%) "
            + "AND u.permission_level < 2 ", nativeQuery = true)
    Page<User> getUsersWithActivityTypeAnd(Pageable pageable, String email, String fullname, String lastname, Set<ActivityType> activityTypes);

    /***
     * Query that updated the database to set the permission level to 1 of the user being promoted to admin.
     * @param userID the id of the user being promoted
     */
    @Transactional
    @Modifying
    @Query(value = "UPDATE User SET permission_level = 1 WHERE user_id = ?", nativeQuery = true)
    void grantAdminRights(Long userID);
}




