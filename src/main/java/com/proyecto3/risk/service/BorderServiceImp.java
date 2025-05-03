package com.proyecto3.risk.service;

import com.proyecto3.risk.model.entities.Border;
import com.proyecto3.risk.model.entities.BorderId;
import com.proyecto3.risk.repository.BorderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class BorderServiceImp implements BorderService{


    @Autowired
    private BorderRepository borderRepository;

    @Override
    public Border findById(Long country1Id, Long country2Id) {

        BorderId id = new BorderId(country1Id, country2Id);

        return borderRepository.findById(id).orElse(null);
    }

    @Override
    public List<Border> getAllBorders() {

        return borderRepository.findAll();
    }

    @Override
    public List<Border> findByCountryId(Long countryId) {
        // Use the custom query method from the repository
        return borderRepository.findByCountryId(countryId);
    }
}
