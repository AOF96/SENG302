Feature: UserActivityRole

  Background:
    Given I have a connection to the database
    And there is no data already existing in the database

  @U17-activity-roles
  Scenario: Delete user activity role
    Given I am a participant of an activity
    When I opt out of that activity role
    Then I am no longer a participant of that activity

  @U28-activity-roles
  Scenario: Become a participant
    Given I have an account with the email "sam@gmail.com"
    And I am looking at another user's activity
    When I choose to become a participant of that activity
    Then I am a participant of the activity

  @U28-activity-roles
  Scenario: Follow an activity
    Given I have an account with the email "sam@gmail.com"
    And I am looking at another user's activity
    When I choose to follow that activity
    Then I am a follower of the activity

  @U28-activity-roles
  Scenario: Selecting an organiser
    Given I have an account with the email "sam@gmail.com"
    And I have an activity
    When I change a participant to be an organiser
    Then That user is an organiser