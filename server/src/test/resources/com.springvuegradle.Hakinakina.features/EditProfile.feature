Feature: EditProfile

  @U4-Editing
  Scenario: Checking if the user is logged in and can edit profile
    Given User creates a new account with primary email "actests1@testing.ac.nz"
    When User when logged in sets fitness level to 2 and sets non mandatory passport countries to 0
    And User can swap initial primary email to "some@123.com"
    And User edits the name to "Julia"
    Then User has a new primary email "some@123.com", name "Julia", fitness level 2 and 0 countries"

  @U4-Editing
  Scenario: Checking if the user is logged in and cannot delete mandatory fields
    Given User creates a new account with first name "John", last name "Doe", primary email "actests1@testing.ac.nz", birth date "1985-12-20"
    When User cannot delete mandatory field such as the first name
    And User cannot delete mandatory field such as the last name
    And User cannot delete mandatory field such as the primary email
    And User cannot delete mandatory field such as the birth date
    Then The user still has first name "John", last name "Doe", primary email "actests1@testing.ac.nz", birth date "1985-12-20"

  @U5-EditingPassword
  Scenario: Checking if the user is logged in and wants to update password
    Given User creates a new account with primary email "actests2@testing.ac.nz" and password "Samsung01"
    When User provides "Samsung01" as the old password to update it to "Nokia6610" as a new password
    Then The user password is updated