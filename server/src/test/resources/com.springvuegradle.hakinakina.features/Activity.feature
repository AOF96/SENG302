Feature: Activity

  Background:
    Given I have a connection to the database
    And there is no data already existing in the database

  @U8-activity
  Scenario: Add a new activity that is continuous
    Given I create an account with name "John", email "john@doe.com" and ID 1
    And I have the authorization token "Aw3s0m3 T0k3n"
    When I create the following activity: 'Hagley Park Marathon' 'Race at the park' 'Fun' 'true' 'null' 'null' 'Christchurch, NZ' 1
    Then the response status is 201

  @U8-activity
  Scenario: Add a new activity that has a duration
    Given I create an account with name "John", email "john@doe.com" and ID 1
    And I have the authorization token "Aw3s0m3 T0k3n"
    When I create the following activity: 'Hagley Park Marathon' 'Race at the park' 'Fun' 'false' '2021-01-20T10:00:00' '2021-02-20T22:00:00' 'Christchurch, NZ' 1
    Then the response status is 201

  @U8-activity
  Scenario: Add a new activity with more than one activity types
    Given I create an account with name "John", email "john@doe.com" and ID 1
    And I have the authorization token "Aw3s0m3 T0k3n"
    When I create the following activity: 'Hagley Park Marathon' 'Race at the park' 'Fun, Relaxing' 'true' 'null' 'null' 'Christchurch, NZ' 1
    Then the response status is 201

  @U8-activity
  Scenario: Add a new activity with no activity types
    Given I create an account with name "John", email "john@doe.com" and ID 1
    And I have the authorization token "Aw3s0m3 T0k3n"
    When I create the following activity with no activity types: 'Hagley Park Marathon' 'Race at the park' 'null' 'true' 'null' 'null' 'Christchurch, NZ' 1
    Then the response status is 400

  @U8-activity
  Scenario: Add a new activity with wrong credentials
    Given I create an account with name "Sarah", email "sarah@doe.com" and ID 2
    And I have the authorization token "N1c3 T0k3n"
    When I create the activity: 'Drinking competition' 'Drink till you drop' 'Extreme' 'true' 'null' 'null' 'Christchurch, NZ' with token "Aw3s0m3 T0k3n"
    Then the response status is 403


#    Examples:
#      | activity_name          | description                       | activity_type        | continuous | start_time               | end_time                 | location         | code |
#      | Botanic Gardens        | Nature                            | Fun                  | true       | null                     | null                     | Christchurch, NZ | 201  |
#      | New Brighton Beach     | Good place to catch some waves    | Adventurous          | false      | 2021-02-20T08:00:00+1300 | 2020-06-20T08:00:00+1300 | Christchurch, NZ | 201  |
#      | Castle Hill            | Narnia                            | Adventurous, Extreme | true       | null                     | null                     | Christchurch, NZ | 201  |
#      | Hanmer Springs         | Enjoy a day at the hot pools      | Relaxing             | false      | null                     | null                     | Christchurch, NZ | 400  |
#      | Rainbows End           | Come try the awesome rides        | null                 | true       | null                     | null                     | Auckland, NZ     | 400  |

#  @U8-activity
#  Scenario: Add a new activity with no authentication
#    Given I create an account with name "Sarah", email "sarah@doe.com" and ID 2
#    And I have the authorization token "N1c3 T0k3n"
#    When I create the activity: 'Drinking competition' 'Drink till you drop' 'Extreme' 'true' 'null' 'null' 'Christchurch, NZ' with no token
#    Then the response status is 401

  @U8-activity
  Scenario: Edit an activity
    Given I create an account with name "Greg", email "greg@doe.com" and ID 3
    And I have the authorization token "L0v3 T0k3ns"
    When I create the following activity: 'Running Marathon' 'Challenge yourself in this race' 'Fun' 'true' 'null' 'null' 'Red pit, Mars' 2
    And I edit the activity with ID 2, token "L0v3 T0k3ns" and new values: 'Running Marathon', 'Challenge yourself in this race', 'Fun', 'true', 'null', 'null', 'Christchurch, NZ'
    Then the response status is 200

  @U8-activity
  Scenario: Editing the activity of another user
    Given I create an account with name "Greg", email "greg@doe.com" and ID 3
    And I have the authorization token "L0v3 T0k3ns"
    When I create the following activity: 'Camping at Castle Hill' 'Pass the night at Narnia' 'Adventurous' 'false' '2020-12-10T19:00:00' '2020-12-11T12:00:00' 'Christchurch, NZ' 3
    And A different user with token "N1c3 T0k3n" tries to edit my activity with values: 'Party at Castle Hill', 'Drink in Narnia', 'Extreme', 'false', '2020-12-10T19:00:00', '2020-12-11T12:00:00', 'Christchurch, NZ'
    Then the response status is 403

  @U8-activity
  Scenario: Editing an activity with invalid activity type
    Given I create an account with name "Greg", email "greg@doe.com" and ID 3
    And I have the authorization token "L0v3 T0k3ns"
    When I create the following activity: 'Camping at Castle Hill' 'Pass the night at Narnia' 'Adventurous' 'false' '2020-12-10T19:00:00' '2020-12-11T12:00:00' 'Christchurch, NZ' 3
    And I edit my activity with token "L0v3 T0k3ns" and new values: 'Camping at Castle Hill', 'Pass the night at Narnia', 'Magical', 'true', '2020-12-10T19:00:00', '2020-12-11T12:00:00', 'Christchurch, NZ'
    Then the response status is 400

  @U8-activity
  Scenario: Delete an activity
    Given I create an account with name "Jane", email "jane@doe.com" and ID 4
    And I have the authorization token "T0k3n"
    When I create the following activity: 'Surf at Sumner' 'Group surfing at Sumner' 'Fun' 'true' 'null' 'null' 'Christchurch, NZ' 3
    And I delete the activity with ID 3 and token "T0k3n"
    Then the response status is 200
    And The created activity with ID 3 no longer exists