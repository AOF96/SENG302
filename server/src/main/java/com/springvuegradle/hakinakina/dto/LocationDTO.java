package com.springvuegradle.hakinakina.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class LocationDTO {

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$",
            message = "This name of a city is not valid")
    @JsonProperty("city")
    private String city;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$",
            message = "This name of a state is not valid")
    @JsonProperty("state")
    private String state;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$",
            message = "This name of a country is not valid")
    @JsonProperty("country")
    private String country;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}
