package com.proyecto3.risk.model.dtos;

public class CountryResponseDto {

    private Long id;
    private String name;
    private Long continentId;
    private String image;

    public CountryResponseDto() {
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

    public Long getContinentId() {
        return continentId;
    }

    public void setContinentId(Long continentId) {
        this.continentId = continentId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
