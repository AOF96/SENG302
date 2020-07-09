Feature: Admin

  Background:
    Given I have a connection to the database
    And there is no data already existing in the database

    @U13-administrators
#    Scenario: Promote an user to become an administrator
#      Given I create an account with name "James", email "james@mail.com" and ID 1
#      And I have the authorization token "t0k3n"
#      And My account has permission level 1
#      When There is an account with name "Jacinda", email "jacinda@govt.nz", ID 2 and permission level 0
#      And I try to promote that account to be an admin using token "t0k3n"
#      Then the response status is 200
#      And The status message is "User successfully promoted"
