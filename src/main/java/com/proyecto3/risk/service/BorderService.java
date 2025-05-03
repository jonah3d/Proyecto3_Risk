package com.proyecto3.risk.service;

import com.proyecto3.risk.model.entities.Border;

import java.util.List;

public interface BorderService {


    Border findById(Long country1Id, Long country2Id);


    List<Border> getAllBorders();


    List<Border> findByCountryId(Long countryId);
}
