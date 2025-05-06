package com.proyecto3.risk.service;

import com.proyecto3.risk.model.entities.Card;

import java.util.List;

public interface CardService {
    List<Card> getAllCards();
    Card getCardByName(String name);
}
