package com.proyecto3.risk.controllers;

import com.proyecto3.risk.exceptions.CountryException;
import com.proyecto3.risk.model.dtos.CountryResponseDto;
import com.proyecto3.risk.repository.CountryRepository;
import com.proyecto3.risk.service.CountryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CountryController {


    @Autowired
    private CountryService countryService;


    @Autowired
    private ModelMapper modelMapper;


    @GetMapping("/countries")
    public ResponseEntity<List<CountryResponseDto>> getCountries() {
        var listOfCountries = countryService.getAllCountries();
        if (listOfCountries == null || listOfCountries.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        List<CountryResponseDto> countries = listOfCountries.stream()
                .map(country -> modelMapper.map(country, CountryResponseDto.class))
                .toList();
        return new ResponseEntity<>(countries, HttpStatus.OK);
    }


    @GetMapping("/countries/{name}")
    public ResponseEntity<CountryResponseDto> getCountryByName(@PathVariable String name) {
        if (name == null || name.isEmpty()) {
            throw new CountryException("Country name cannot be null or empty");
        }

        var country = countryService.getCountryByName(name);
        if (country == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        CountryResponseDto countryResponseDto = modelMapper.map(country, CountryResponseDto.class);

        return new ResponseEntity<>(countryResponseDto, HttpStatus.OK);
    }
}
