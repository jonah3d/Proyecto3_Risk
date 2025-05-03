package com.proyecto3.risk.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "country", indexes = {
        @Index(name = "continent_id", columnList = "continent_id")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Country {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull(message = "Name cannot be null")
    @Column(name = "name", nullable = false)
    @JsonProperty("name")
    private String name;

    @NotNull(message = "Continent cannot be null")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "continent_id", nullable = false)
    @JsonProperty("continent")
    private Continent continent;

    @Size(max = 255)
    @NotNull(message = "Image cannot be null")
    @Column(name = "image", nullable = false)
    @JsonProperty("image")
    private String image;

    @ManyToMany
    private List<Country> countries = new ArrayList<>();


    public Country() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Continent getContinent() {
        return continent;
    }

    public void setContinent(Continent continent) {
        this.continent = continent;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

}