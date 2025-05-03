package com.proyecto3.risk.repository;

import com.proyecto3.risk.model.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    @Query("SELECT c FROM Country c WHERE c.name = ?1")
    Country findByName(String name);

}
