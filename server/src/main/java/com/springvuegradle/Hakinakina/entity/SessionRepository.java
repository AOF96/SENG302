package com.springvuegradle.Hakinakina.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
@RepositoryRestResource
public interface SessionRepository extends JpaRepository<Session, String>{



        @Query(value = "Insert INTO Sessions (auth_token) VALUES (?) WHERE user_id = ?", nativeQuery = true)
        Session insertToken(String token, Long user_id);


}
