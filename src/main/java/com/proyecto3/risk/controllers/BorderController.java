package com.proyecto3.risk.controllers;

import com.proyecto3.risk.model.dtos.BorderResponseDto;
import com.proyecto3.risk.model.entities.Border;
import com.proyecto3.risk.service.BorderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class BorderController {

    @Autowired
    private BorderService borderService;

    @Autowired
    private ModelMapper modelMapper;



    @GetMapping("/borders")
    public ResponseEntity<List<BorderResponseDto>> getAllBorders() {
        List<Border> borders = borderService.getAllBorders();
        List<BorderResponseDto> borderDtos = borders.stream()
                .map(border -> modelMapper.map(border, BorderResponseDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(borderDtos);
    }


    @GetMapping("/border/{country1Id}/{country2Id}")
    public ResponseEntity<BorderResponseDto> getBorderById(
            @PathVariable Long country1Id,
            @PathVariable Long country2Id) {

        Border border = borderService.findById(country1Id, country2Id);

        if (border == null) {
            return ResponseEntity.notFound().build();
        }

        BorderResponseDto borderDto = modelMapper.map(border, BorderResponseDto.class);
        return ResponseEntity.ok(borderDto);
    }


    @GetMapping("/border/{countryId}")
    public ResponseEntity<List<BorderResponseDto>> getBordersByCountryId(
            @PathVariable Long countryId) {

        List<Border> borders = borderService.findByCountryId(countryId);
        List<BorderResponseDto> borderDtos = borders.stream()
                .map(border -> modelMapper.map(border, BorderResponseDto.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(borderDtos);
    }

}
