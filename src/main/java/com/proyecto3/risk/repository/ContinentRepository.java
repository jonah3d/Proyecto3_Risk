package com.proyecto3.risk.repository;

import com.proyecto3.risk.model.entities.Continent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;



@Repository
public interface ContinentRepository extends JpaRepository<Continent, Long> {

    @Query("SELECT c FROM Continent c WHERE c.name = ?1")
    Continent findByName(String name);

    @Query("SELECT DISTINCT c FROM Continent c LEFT JOIN FETCH c.countries")
    List<Continent> findAllWithCountries();


    // Continent findById(long id);

}
