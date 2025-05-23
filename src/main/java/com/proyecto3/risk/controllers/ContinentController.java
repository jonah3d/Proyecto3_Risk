package com.proyecto3.risk.controllers;

import com.proyecto3.risk.model.dtos.ContinentResponseDto;
import com.proyecto3.risk.service.ContinentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ContinentController {


    @Autowired
    private ContinentService continentService;

    @Autowired
    private ModelMapper modelMapper;


    @GetMapping("/continents")
    public ResponseEntity<List<ContinentResponseDto>> getContinents() {
        var continents = continentService.getAllContinents();

        if (continents.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        List<ContinentResponseDto> continentResponseDtos = continents.stream()
                .map(continent -> modelMapper.map(continent, ContinentResponseDto.class))
                .toList();

        return new ResponseEntity<>(continentResponseDtos, HttpStatus.OK);
    }

    @GetMapping("/continents/{name}")
    public ResponseEntity<ContinentResponseDto> getContinentByName(@PathVariable String name) {
        var continent = continentService.getContinentByName(name);

        if (continent == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ContinentResponseDto continentResponseDto = modelMapper.map(continent, ContinentResponseDto.class);

        return new ResponseEntity<>(continentResponseDto, HttpStatus.OK);
    }

    @GetMapping("/continents/id/{id}")
    public ResponseEntity<ContinentResponseDto> getContinentById(@PathVariable long id) {
        var continent = continentService.getContinentById(id);

        if (continent == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ContinentResponseDto continentResponseDto = modelMapper.map(continent, ContinentResponseDto.class);

        return new ResponseEntity<>(continentResponseDto, HttpStatus.OK);
    }

}
