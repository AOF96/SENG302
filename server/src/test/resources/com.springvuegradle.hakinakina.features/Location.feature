Feature: Location

  Background:
    Given I have a connection to the database
    And there is no data already existing in the database

#  @U39-ActivityGeolocation
#  Scenario: Retrieving activities that are within the given range
#    Given I own an account with name "John", email "john@doe.com" and ID 1
#    And I create an activity with ID 1
#    And the activity is located in latitude -43 and longitude 172
#    When I retrieve activities that are within latitudes -45, -40 and longitudes 170, 175
#    Then the response code is 200

  @U38-UserGeolocation
  Scenario: Creating new location
    Given I am logged into an account with name "John", email "john@doe.com" and ID 1
    And I set my location to latitude -43, longitude 172
    When I retrieve my location
    Then It's latitude is -43 and it's longitude is 172

  Scenario: Editing my location
    Given I am logged into an account with name "John", email "john@doe.com" and ID 1 and have location with latitude -43, longitude 172
    And I edit my location so that the latitude is now -100 and longitude is now 200
    When I retrieve my new location
    Then My location's latitude is now -100 and longitude is now 200

  Scenario: Editing my location with mocking
    Given I have account with name "John", email "john@doe.com" and ID 1 and have location with latitude -43, longitude 172
    And I make a call to the edit location endpoint so that my location is changed to -100 and 200
    When I retrieve my new location using the get endpoint
    Then The new lat is equal to -100 and new long is equal to 200

