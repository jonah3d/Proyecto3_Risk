package com.proyecto3.risk.service;

import com.proyecto3.risk.model.entities.Continent;

import java.util.List;

public interface ContinentService {

    List<Continent> getAllContinents();
    Continent getContinentByName(String name);
}
