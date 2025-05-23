package com.proyecto3.risk.service;

import com.proyecto3.risk.model.entities.Continent;
import com.proyecto3.risk.repository.ContinentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ContinentServiceImp implements ContinentService {

    @Autowired
    private ContinentRepository continentRepository;


    public ContinentServiceImp() {
    }

    @Override
    public List<Continent> getAllContinents() {

        var continents = continentRepository.findAllWithCountries();

        if (continents.isEmpty()) {
            throw new RuntimeException("No continents found");
        }
        return continents;
    }

    @Override
    public Continent getContinentByName(String name) {

        if(name.isBlank()) {
            throw new RuntimeException("Continent name cannot be null or empty");
        }
        Continent continent = continentRepository.findByName(name);
        if(continent == null) {
            throw new RuntimeException("Continent not found with name: " + name);
        }
        return continent;
    }

    @Override
    public Continent getContinentById(long id) {
        if(id <= 0) {
            throw new RuntimeException("Continent id cannot be null or empty");
        }
        Continent continent = continentRepository.findById(id).orElse(null);
        if(continent == null) {
            throw new RuntimeException("Continent not found with id: " + id);
        }
        return continent;
    }
}
