package com.proyecto3.risk.model.entities;

import java.io.Serializable;
import java.util.Objects;

public class BorderId implements Serializable {
    public Long country1Id;

    public Long country2Id;

    public BorderId() {
    }

    public BorderId(Long country1Id, Long country2Id) {
        this.country1Id = country1Id;
        this.country2Id = country2Id;
    }

    public Long getCountry1Id() {
        return country1Id;
    }

    public void setCountry1Id(Long country1Id) {
        this.country1Id = country1Id;
    }

    public Long getCountry2Id() {
        return country2Id;
    }

    public void setCountry2Id(Long country2Id) {
        this.country2Id = country2Id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorderId entity = (BorderId) o;
        return Objects.equals(this.country1Id, entity.country1Id) &&
                Objects.equals(this.country2Id, entity.country2Id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country1Id, country2Id);
    }
}