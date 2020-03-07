package com.springvuegradle.Hakinakina.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository for storing users
 */
@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "Select * from User u where u.primary_email = ?1", nativeQuery = true)
    User findUserByEmail(String email);
}
