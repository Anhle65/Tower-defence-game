Feature: U2 - As Alex I want to create a round so that I can combine a group of enemies to face with my towers.

  Scenario: AC1.1 - A round must be a strictly positive number.
    Given I create a player named "Jane"
    And Player "Jane" has no rounds with the number 1
    When I create the round with number 1 and distance 100 for "Jane"
    Then The round is created with number 1 for "Jane"

  Scenario Outline: AC1.2 - A round cannot have an empty, non-alphanumeric, or numeric only name
    Given I create a player named "Te Ariki"
    When I create an invalid round with number <number> and distance 100 for "Te Ariki"
    Then I cannot create that round
    Examples:
      | number |
      | 0      |
      | -1     |

  Scenario: AC2 - A round must be a strictly positive number.
    Given I create a player named "Yiyang"
    And I create the round with number 1 and distance 100 for "Yiyang"
    And I create a player named "Fran"
    When I create a duplicate round with number 1 and distance 100 for "Yiyang"
    But Player "Fran" has no rounds with the number 1
    Then I cannot create that round
    And I create the round with number 1 and distance 100 for "Fran"
    And The round is created with number 1 for "Fran"

  Scenario: AC3 - A round must be able to store many enemies
    Given I create a player named "Rohan"
    When I create the round with number 1 and distance 100 for "Rohan"
    And I add an enemy named "Kererū" with speed 7 and health 3 in round 1 for "Rohan"
    And I add an enemy named "Hamster" with speed 2 and health 1 in round 1 for "Rohan"
    Then The round 1 of "Rohan" includes an enemy named "Kererū"
    Then The round 1 of "Rohan" includes an enemy named "Hamster"

  Scenario: AC4 - A round must have a distance strictly greater than 0
    Given I create a player named "Javier"
    When I create an invalid round with number 1 and distance <distance> for "Javier"
    Then I cannot create that round
    Examples:
      | distance |
      | 0        |
      | -1       |
