Feature: FitnessPassport

  @U2-Passport
  Scenario: Adding the new passport country to the user
    Given User creates a new account with email "actests1@testing.ac.nz" with no passport country
    When User adds a new country to the user
    Then country is added to the user passports


  Scenario: Adding the second passport country to the user
    Given User exists with single passport country
    When User adds another country "New Zealand" to his passport countries
    Then User has 2 passport countries added
