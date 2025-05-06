package com.proyecto3.risk.service;

import com.proyecto3.risk.model.entities.Card;
import com.proyecto3.risk.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CardServiceImp implements CardService {


    @Autowired
    CardRepository cardRepository;

    @Override
    public List<Card> getAllCards() {

        var cards = cardRepository.findAll();
        if(cards.isEmpty()){
            return null;
        }

        return cards;
    }

    @Override
    public Card getCardByName(String name) {

        if(name.isBlank()){
            throw new RuntimeException("Name cant be blank");
        }


       var card =  cardRepository.getCardByName(name);
        if(card == null){
            return null;
        }



        return card;
    }
}
