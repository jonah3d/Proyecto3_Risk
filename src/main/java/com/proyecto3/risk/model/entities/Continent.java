package com.proyecto3.risk.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="continent")
public class Continent {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "extra_tropes", nullable = false)
    private Integer extraTropes;

    @Column(name = "max_country")
    private Integer max_countries;

    @OneToMany(mappedBy = "continent")
    private List<Country> countries = new ArrayList<>();

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

    public Integer getExtraTropes() {
        return extraTropes;
    }

    public void setExtraTropes(Integer extraTropes) {
        this.extraTropes = extraTropes;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

    public Integer getMaxcountries() {
        return max_countries;
    }

    public void setMaxcountries(Integer maxcountries) {
        this.max_countries = maxcountries;
    }
}