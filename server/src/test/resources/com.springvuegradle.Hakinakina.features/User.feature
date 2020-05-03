Feature: User

  @U8-user
  Scenario: Creating a new user
    Given I create a new account with email "actests@testing.ac.nz"
    When I get all the accounts the have been created
    Then Exactly one account is returned