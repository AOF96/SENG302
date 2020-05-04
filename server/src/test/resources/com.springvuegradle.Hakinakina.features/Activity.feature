Feature: Activity

  @U8-activity
  Scenario Outline: Add a new activity
    Given I create an account with email "john@doe.com"
    And they add the following activity: '<activity_name>' '<description>' '<activity_type>' '<continuous>' '<start_time>' '<end_time>' '<location>'
    When user 1 activities are retrieved
    Then exactly 1 activity should be returned
    And the response status is <code>
    Examples:
      | activity_name          | description                       | activity_type        | continuous | start_time               | end_time                 | location         | code |
      | Botanic Gardens        | Nature                            | Fun                  | true       | null                     | null                     | Christchurch, NZ | 201  |
      | New Brighton Beach     | Good place to catch some waves    | Adventurous          | false      | 2021-02-20T08:00:00+1300 | 2020-06-20T08:00:00+1300 | Christchurch, NZ | 201  |
      | Castle Hill            | Narnia                            | Adventurous, Extreme | true       | null                     | null                     | Christchurch, NZ | 201  |
      | Hanmer Springs         | Enjoy a day at the hot pools      | Relaxing             | false      | null                     | null                     | Christchurch, NZ | 400  |
      | Rainbows End           | Come try the awesome rides        | null                 | true       | null                     | null                     | Auckland, NZ     | 400  |


  @U8-activity
  Scenario: Delete an activity
    Given I create an account with email "john@doe.com"
    And I create an activity with name "Park Race", description "Exciting race at the park" and ID 1
    When I delete the created activity
    Then The created activity with ID 1 no longer exists