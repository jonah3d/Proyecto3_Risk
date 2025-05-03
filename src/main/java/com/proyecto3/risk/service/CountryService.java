package com.proyecto3.risk.service;

import com.proyecto3.risk.model.entities.Country;

import java.util.List;
import java.util.Set;

public interface CountryService {
    List<Country> getAllCountries();
    Country getCountryByName(String name);
    Country getCountryById(Long id);
}
