package com.springvuegradle.Hakinakina.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface EmailRepository extends JpaRepository<Email, Long> {
    @Query(value = "Select * from Email e where e.email = ?1", nativeQuery = true)
    Email findEmailByString(String email);
}
