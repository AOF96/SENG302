Feature: Activity

  @U8-activity
  Scenario: Add a new activity
    Given that user 1 adds the following activity
      | activity_name   | description | activity_type | continuous | start_time | end_time | location         |
      | Botanic Gardens | Nature      | Fun           | true       | null       | null     | Christchurch, NZ |
    When user 1 activities are retrieved
    Then exactly 1 activity should be returned

  @U8-activity
  Scenario: Delete an activity
    Given I create an account with email "john@doe.com"
    And I create an activity with name "Park Race", description "Exciting race at the park" and ID 1
    When I delete the created activity
    Then The created activity with ID 1 no longer exists