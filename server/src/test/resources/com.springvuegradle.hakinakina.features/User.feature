Feature: User

  Background:
    Given I have a connection to the database
    And there is no data already existing in the database

  @U8-user
  Scenario: Get user by email
    Given I create a new account with email "actests@testing.ac.nz"
    When I save the user to the database
    And I retrieve all users with the email "actests@testing.ac.nz" from the database
    Then Exactly one account is returned

  @U8-user
  Scenario: Get user by id
    Given I create a new account with id 75
    When I save the user to the database
    And I retrieve all users with the id 75 from the database
    Then Exactly one account is returned

  @U8-user
  Scenario: Add multiple users
    Given I create three new users and save all users to the database
    When I retrieve all users
    Then Exactly 3 accounts are returned

  @U8-user
  Scenario: Deleting user
    Given I create a new account with id 75
    When I save the user to the database
    And I delete the data from the database
    And I retrieve all users with the id 75 from the database
    Then Exactly 0 users are returned
