package com.proyecto3.risk.model.dtos;

public class BorderResponseDto {

    private CountryResponseDto country1;
    private CountryResponseDto country2;

    public CountryResponseDto getCountry1() {
        return country1;
    }

    public void setCountry1(CountryResponseDto country1) {
        this.country1 = country1;
    }

    public CountryResponseDto getCountry2() {
        return country2;
    }

    public void setCountry2(CountryResponseDto country2) {
        this.country2 = country2;
    }
}
