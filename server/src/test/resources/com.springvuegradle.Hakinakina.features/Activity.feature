Feature: Activity

  @U8-activity
  Scenario: Add a new activity
    Given that user 1 adds the following activity
      | activity_name   | description | activity_type | continuous | start_time | end_time | location         |
      | Botanic Gardens | Nature      | Fun           | true       | null       | null     | Christchurch, NZ |
    When user 1 activities are retrieved
    Then exactly 1 activity should be returned
