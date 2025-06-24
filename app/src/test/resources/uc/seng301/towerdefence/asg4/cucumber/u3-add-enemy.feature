Feature: U3 - As Alex, I want to fetch random enemies from an external API so that I can build a round from them.
  Scenario Outline: AC1 - I can create a round with one random enemy.
    Given I create a player named <player name>
    And player <player name> has an empty round numbered <round number>
    When I add <num enemies> enemies to <player name> round <round number>
    Then the <num enemies> enemies are added to <player name> round <round number>
    Examples:
      | player name | round number | num enemies |
      | "Maia Q"    | 1            | 1           |
      | "Maia Q"    | 2            | 9           |

  Scenario Outline: AC2 - I cannot create a round with more than 10 random enemies.
    Given I create a player named <player name>
    And player <player name> has an empty round numbered <round number>
    When I add <num enemies> enemies to <player name> round <round number>
    Then I am told I must add between 1 and 10 enemies
    Examples:
      | player name | round number | num enemies |
      | "Steve"     | 1            | 0           |
      | "Steve"     | 2            | 11          |

  Scenario: AC3 - I cannot add an enemy to a round that already contains 10 enemies.
    Given I create a player named "Jackie"
    And player "Jackie" has an empty round numbered 3
    And There are already 3 enemies in "Jackie" round 3
    When I add 8 enemies to "Jackie" round 3
    Then I am told the total number of enemies in a round cannot exceed 10

  Scenario: AC4 - I cannot add twice the same enemy into a round.
    Given I create a player named "Bob"
    And player "Bob" has an empty round numbered 2
    When I add 2 duplicate enemies to "Bob" round 2
    Then I am told I cannot add the same enemy twice
