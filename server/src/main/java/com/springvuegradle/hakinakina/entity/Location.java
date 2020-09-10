package com.springvuegradle.hakinakina.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Set;

/**
 * Location entity class
 */
@Entity
@Table(name = "Location")
public class Location {
    @Id
    @GeneratedValue
    @Column(name = "location_id")
    private Long id;

    @JsonProperty("street_address")
    @Column(name = "street_address")
    private String streetAddress;

    @JsonProperty("suburb")
    @Column(name = "suburb")
    private String suburb;

    @JsonProperty("city")
    @Column(name = "city")
    private String city;

    @JsonProperty("postcode")
    @Column(name = "postcode")
    private int postcode;

    @JsonProperty("state")
    @Column(name = "state")
    private String state;

    @JsonProperty("country")
    @Column(name = "country")
    private String country;

    @JsonProperty("latitude")
    @Column(name = "latitude")
    private double latitude;

    @JsonProperty("longitude")
    @Column(name = "longitude")
    private double longitude;

    @OneToOne(mappedBy = "location")
    private Activity activity;

    @OneToOne(mappedBy = "location")
    private User user;

    protected Location() {}

    public Location(String streetAddress, String suburb, String city, int postcode, String state, String country, double latitude, double longitude) {
        this.streetAddress = streetAddress;
        this.suburb = suburb;
        this.city = city;
        this.postcode = postcode;
        this.state = state;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getPostcode() {
        return postcode;
    }

    public void setPostcode(int postcode) {
        this.postcode = postcode;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return this.activity;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

}
