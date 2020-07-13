Feature: Admin

  Background:
    Given I have a connection to the database
    And there is no data already existing in the database

    @U13-administrators
    Scenario: Promote an user to become an administrator
      Given I have an account with name "James", email "james@mail.com" and ID 1
      And I have the authorization cookie "c00ki3"
      And My account has permission level 1
      When There is an account with name "Jacinda", email "jacinda@govt.nz", ID 2 and permission level 0
      And I try to promote that account to be an admin using token "c00ki3"
      Then the response code is 200 and the status message is "User successfully promoted"


    @U13-administrators
    Scenario: Attempting to promote an user who is already an administrator
      Given I have an account with name "James", email "james@mail.com" and ID 1
      And I have the authorization cookie "c00ki3"
      And My account has permission level 1
      When There is an account with name "Jacinda", email "jacinda@govt.nz", ID 2 and permission level 1
      And I try to promote that account who is already an admin using token "c00ki3"
      Then the response code is 400 and the status message is "User to promote is already an admin"


    @U13-administrators
    Scenario: Attempting to promote an user without being an administrator
      Given I have an account with name "James", email "james@mail.com" and ID 1
      And I have the authorization cookie "c00ki3"
      And My account has permission level 0
      When There is an account with name "Jacinda", email "jacinda@govt.nz", ID 2 and permission level 0
      And I try to promote that account who without being an admin using token "c00ki3"
      Then the response code is 403 and the status message is "Unauthorized user"
