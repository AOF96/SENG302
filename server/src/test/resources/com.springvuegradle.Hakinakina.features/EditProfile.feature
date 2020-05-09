Feature: EditProfile

  @U4-Editing
  Scenario: Checking if the user is logged in and can edit profile
    Given User creates a new account with primary email "actests1@testing.ac.nz"
    When User when logged in sets fitness level to 2 and sets non mandatory passport countries to 0
    And User can swap initial primary email to "some@123.com"
    And User edits the name to "Julia"
    Then User has a new primary email "some@123.com", name "Julia", fitness level 2 and 0 countries"
