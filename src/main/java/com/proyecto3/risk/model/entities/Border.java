package com.proyecto3.risk.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@IdClass(BorderId.class)
@Entity
@Table(name = "border", indexes = {
        @Index(name = "country2_id", columnList = "country2_id")
})
public class Border {
    @Id
    @NotNull
    @Column(name = "country1_id", nullable = false)
    private Long country1Id;

    @Id
    @NotNull
    @Column(name = "country2_id", nullable = false)
    private Long country2Id;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country1_id", nullable = false)
    private Country country1;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country2_id", nullable = false)
    private Country country2;

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

    public Country getCountry1() {
        return country1;
    }

    public void setCountry1(Country country1) {
        this.country1 = country1;
    }

    public Country getCountry2() {
        return country2;
    }

    public void setCountry2(Country country2) {
        this.country2 = country2;
    }

}