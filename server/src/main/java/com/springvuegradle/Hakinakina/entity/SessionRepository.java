package com.springvuegradle.Hakinakina.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface SessionRepository extends JpaRepository<Session, String>{
        @Query(value = "Insert INTO Session (token, user_user_id) VALUES (?, ?)", nativeQuery = true)
        Session insertToken(String token, Long user_id);

        @Query(value = "SELECT * FROM Session WHERE token = ?", nativeQuery = true)
        Session findUserIdByToken(String token);

        @Query(value = "DELETE FROM Session WHERE token = ?", nativeQuery = true)
        void removeToken(String token);

        @Query(value = "DELETE FROM Session WHERE user_user_id = ?", nativeQuery = true)
        Session removeTokenByUserId(String token);

        @Query(value = "SELECT token FROM Session WHERE user_user_id = ?", nativeQuery = true)
        List<String> getUserSessionToken(Long profile_id);
}
