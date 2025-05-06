package com.proyecto3.risk.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "cards", indexes = {
        @Index(name = "c_types", columnList = "c_types"),
        @Index(name = "country_id", columnList = "country_id")
})
public class Card {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @Column(name = "image")
    private String image;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "c_types", nullable = false)
    private CardType cTypes;

    @OneToMany(mappedBy = "card")
    private List<Ma> players; // or maList

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public CardType getCTypes() {
        return cTypes;
    }

    public void setCTypes(CardType cTypes) {
        this.cTypes = cTypes;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

}