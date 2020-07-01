package com.springvuegradle.hakinakina.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.hakinakina.dto.LocationDTO;
import com.springvuegradle.hakinakina.entity.Gender;
import com.springvuegradle.hakinakina.validator.Before;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.sql.Date;
import java.util.Set;

public class CreateProfileRequest {

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 50, message = "Last name cannot be over 50 characters")
    @JsonProperty("lastname")
    private String lastName;

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 50, message = "First name cannot be over 50 characters")
    @JsonProperty("firstname")
    private String firstName;

    @Size(max = 50, message = "Middle name cannot be over 50 characters")
    @JsonProperty("middlename")
    private String middleName;

    @Size(max = 50, message = "Nickname cannot be over 50 characters")
    @JsonProperty("nickname")
    private String nickname;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$",
            message = "Your password must include upper and lower case letters, " +
                        "and contains at least 1 number")
    @NotBlank(message = "Password cannot be blank")
    @JsonProperty("password")
    private String password;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be in a valid format")
    @JsonProperty("primary_email")
    private String primaryEmail;

    @Size(max = 4, message = "Maximum number of additional emails is 4")
    @JsonProperty("additional_email")
    private Set<@Email String> additionalEmail;

    @Size(max = 200, message = ("Bio cannot be over 200 characters"))
    @JsonProperty("bio")
    private String bio;

    @Before(years = 12)
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    @JsonProperty("date_of_birth")
    private java.sql.Date birthDate;

    @JsonProperty("gender")
    private Gender gender;

    @Min(value=0, message = "Fitness level must be greater than 0")
    @Max(value=4, message = "Fitness level must be less than or equal to 4")
    @JsonProperty("fitnessLevel")
    private int fitness;

    private LocationDTO locationDTO;



    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getAdditionalEmail() {
        return additionalEmail;
    }

    public void setAdditionalEmail(Set<String> additionalEmail) {
        this.additionalEmail = additionalEmail;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public LocationDTO getLocationDTO() {
        return locationDTO;
    }

    public void setLocationDTO(LocationDTO locationDTO) {
        this.locationDTO = locationDTO;
    }

    public Date getBirthDate() { return birthDate; }

    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }

}
