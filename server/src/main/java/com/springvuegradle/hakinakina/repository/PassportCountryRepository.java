package com.springvuegradle.hakinakina.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PassportCountryRepository extends JpaRepository<PassportCountry, Long> {
    @Query(value = "Select * from Passport_Country c where c.name = ?1", nativeQuery = true)
    PassportCountry findCountryByName(String name);
}
