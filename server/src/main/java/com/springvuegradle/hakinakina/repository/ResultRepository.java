package com.springvuegradle.hakinakina.repository;

import com.springvuegradle.hakinakina.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ResultRepository extends JpaRepository<Result, Long> {
}
