Feature: UserActivityRole

  Background:
    Given I have a connection to the database
    And there is no data already existing in the database

  @U17-activity-roles
  Scenario: Delete user activity role
    Given I am a participant of an activity
    When I opt out of that activity role
    Then I am no longer a participant of that activity

