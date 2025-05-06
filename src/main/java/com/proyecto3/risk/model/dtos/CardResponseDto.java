package com.proyecto3.risk.model.dtos;

public class CardResponseDto {

    private Long id;
    private String image;
    private Long cType;
    private Long countryId;

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

    public Long getcType() {
        return cType;
    }

    public void setcType(Long cType) {
        this.cType = cType;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }
}
