Feature: Location

  Background:
    Given I have a connection to the database
    And there is no data already existing in the database

  @U39-ActivityGeolocation
  Scenario: Retrieving activities that are within the given range
    Given I own an account with name "John", email "john@doe.com" and ID 1
    And I create an activity with ID 1
    And the activity is located in latitude -43 and longitude 172
    When I retrieve activities that are within latitudes -45, -40 and longitudes 170, 175
    Then the response code is 200