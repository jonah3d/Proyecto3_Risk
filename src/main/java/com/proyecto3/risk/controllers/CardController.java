package com.proyecto3.risk.controllers;

import com.proyecto3.risk.model.dtos.CardResponseDto;
import com.proyecto3.risk.service.AvatarService;
import com.proyecto3.risk.service.CardService;
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
public class CardController {


    @Autowired
    private CardService cardservice;

    @Autowired
    private ModelMapper modelMapper;


    @GetMapping("/cards")
    public ResponseEntity<List<CardResponseDto>> getAllCards(){

    var cards = cardservice.getAllCards();

    if(cards.isEmpty()){
        return ResponseEntity.noContent().build();
    }

   List<CardResponseDto> cardDtos = cards.stream()
           .map(Card->modelMapper.map(Card, CardResponseDto.class))
           .toList();

    return new ResponseEntity<>(cardDtos, HttpStatus.OK);

    }

    @GetMapping("/cards/{name}")
    public ResponseEntity<CardResponseDto> getCard(@PathVariable String name){

        if(name.isBlank()){
            return ResponseEntity.badRequest().build();
        }

       var card =  cardservice.getCardByName(name);

      var newCard =   modelMapper.map(card, CardResponseDto.class);

      return new ResponseEntity<>(newCard, HttpStatus.OK);

    }
}
