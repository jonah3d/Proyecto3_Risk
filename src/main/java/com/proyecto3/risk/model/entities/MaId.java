package com.proyecto3.risk.model.entities;

import java.io.Serializable;
import java.util.Objects;

public class MaId implements Serializable {
    public Long cardId;

    public Long playerId;

    public MaId() {
    }

    public MaId(Long cardId, Long playerId) {
        this.cardId = cardId;
        this.playerId = playerId;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaId entity = (MaId) o;
        return Objects.equals(this.cardId, entity.cardId) &&
                Objects.equals(this.playerId, entity.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardId, playerId);
    }
}