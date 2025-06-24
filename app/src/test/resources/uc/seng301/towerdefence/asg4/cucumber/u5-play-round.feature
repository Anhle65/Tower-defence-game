Feature: U5 As Alex, I want to play against a round with my towers to see if I win or lose
  Scenario: AC1 - I can not play a round if the round does not exist
    Given I create a player named "Morgan"
    And "Morgan" has towers with damage
      | Tower 1 | 10 |
    But "Morgan" has no round
    When "Morgan" tries to play against round 1
    Then I am told I can not play the round because the round 1 does not exist

  Scenario: AC2 - I can not play a round if the round does not have any enemies
    Given I create a player named "Matthew"
    And "Matthew" has towers with damage
      | Tower 1 | 10 |
      | Tower 2 | 10 |
      | Tower 3 | 10 |
    And player "Matthew" has an empty round numbered 1
    When "Matthew" tries to play against round 1
    Then I am told I can not play the round because the round 1 does not have any enemies

  Scenario: AC3 - I can not play a round if I have no registered towers
    Given I create a player named "Joseph"
    And "Joseph" has a round 1 with enemies
      | Enemy 1 | 10     | 10    |
    But "Joseph" has no towers
    When "Joseph" tries to play against round 1
    Then I am told I can not play the round because "Joseph" does not have any towers

  Scenario: AC4 - I win a round when my towers reduce all enemies' health to 0 before they cover the rounds' distance
    Given I create a player named "Yue"
    And "Yue" has towers with damage
      | Tower 1 | 100 |
    And "Yue" has a round 1 with enemies
      | Enemy 2 | 10     | 10    |
    When "Yue" tries to play against round 1
    Then the player wins the round

  Scenario: AC5 - I lose a round when my towers do not reduce all enemies' health to 0 before they cover the rounds' distance
    Given I create a player named "Marques"
    And "Marques" has towers with damage
      | Tower 1 | 1 |
    And "Marques" has a round 1 with enemies
      | Enemy 3 | 100    | 10    |
    When "Marques" tries to play against round 1
    Then the player loses the round