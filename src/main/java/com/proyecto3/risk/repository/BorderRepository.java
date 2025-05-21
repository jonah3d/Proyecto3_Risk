package com.proyecto3.risk.repository;

import com.proyecto3.risk.model.entities.Border;
import com.proyecto3.risk.model.entities.BorderId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorderRepository extends JpaRepository<Border, BorderId> {

    @Query("SELECT b FROM Border b WHERE b.country1Id = :countryId OR b.country2Id = :countryId")
    List<Border> findByCountryId(@Param("countryId") Long countryId);

/*
    @Query("SELECT b FROM Border b WHERE b.country1.id IN :countryIds OR b.country2.id IN :countryIds")
    List<Border> findByCountryIds(@Param("countryIds") List<Long> countryIds);*/
}
