package com.springvuegradle.hakinakina.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for Activity Search response and displays a generic location when available
 */
public class SearchActivityLocationDto {
    @JsonProperty("street_address")
    private String streetAddress;
    private String city;
    private String country;

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
