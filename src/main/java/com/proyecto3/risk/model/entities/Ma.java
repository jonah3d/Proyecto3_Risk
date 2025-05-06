package com.proyecto3.risk.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@IdClass(MaId.class)
@Entity
@Table(name = "ma", indexes = {
        @Index(name = "player_id", columnList = "player_id")
})
public class Ma {
    @Id
    @NotNull
    @Column(name = "card_id", nullable = false)
    private Long cardId;

    @Id
    @NotNull
    @Column(name = "player_id", nullable = false)
    private Long playerId;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

}