package com.proyecto3.risk.service;

import com.proyecto3.risk.exceptions.CountryException;
import com.proyecto3.risk.model.entities.Country;
import com.proyecto3.risk.repository.CountryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class CountryServiceImp implements CountryService {

    @Autowired
    private CountryRepository countryRepository;


    public CountryServiceImp() {

    }

    @Override
    public List<Country> getAllCountries() {

        List<Country> countries =countryRepository.findAll();

        for (Country country : countries) {
            System.out.println("Country: " + country.getName());
        }


        return countries;
    }

    @Override
    public Country getCountryByName(String name) {

        if (name.isBlank()) {
            throw new CountryException("Country name cannot be null or empty");
        }


        Country country = countryRepository.findByName(name);
        if (country == null) {
            throw new CountryException("Country not found with name: " + name);
        }
        return country;
    }

    @Override
    public Country getCountryById(Long id) {

        if (id <= 0) {
            throw new CountryException("Country ID must be greater than zero");
        }

        Country country = countryRepository.findById(id).orElse(null);

        if (country == null) {
            throw new CountryException("Country not found with ID: " + id);
        }

        return country;
    }
}
