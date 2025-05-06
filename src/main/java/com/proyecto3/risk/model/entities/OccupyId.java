package com.proyecto3.risk.model.entities;

import java.io.Serializable;
import java.util.Objects;

public class OccupyId implements Serializable {
    public Long countryId;

    public Long playerId;

    public OccupyId() {
    }

    public OccupyId(Long countryId, Long playerId) {
        this.countryId = countryId;
        this.playerId = playerId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
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
        OccupyId entity = (OccupyId) o;
        return Objects.equals(this.countryId, entity.countryId) &&
                Objects.equals(this.playerId, entity.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryId, playerId);
    }
}