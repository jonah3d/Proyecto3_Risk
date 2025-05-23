package com.proyecto3.risk.model.dtos;

import java.util.List;

public class ContinentResponseDto {
    private Long id;
    private String name;
    private Integer extraTropes;
    private Integer max_countries;
    private List<CountryResponseDto> countries;

    public ContinentResponseDto() {
    }

    public Integer getMaxcountries() {
        return max_countries;
    }

    public void setMaxcountries(Integer maxcountries) {
        this.max_countries = maxcountries;
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

    public Integer getExtraTropes() {
        return extraTropes;
    }

    public void setExtraTropes(Integer extraTropes) {
        this.extraTropes = extraTropes;
    }

    public List<CountryResponseDto> getCountries() {
        return countries;
    }

    public void setCountries(List<CountryResponseDto> countries) {
        this.countries = countries;
    }
}
