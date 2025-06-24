Feature: U1 - As Alex, I want to create an Enemy so that I can use it in a round.

  Scenario Outline: AC1 - An enemy has a unique non-empty name, a strictly positive attack, and positive health stats.
    Given There is no enemy with name <name>
    When I create an enemy named <name> with speed: <speed> and health: <health>
    Then The enemy is created with the name <name>, speed: <speed> and health: <health>
    Examples:
      | name    | speed | health |
      | "Bull"  | 100   | 2      |
      | "Eagle" | 50    | 0      |

  Scenario Outline: AC2 - An enemy name cannot contain non-alphanumeric or numeric-only values.
    Given There is no enemy with name <name>
    When I create an invalid enemy named <name> with speed: 10 and health: 100
    Then I cannot create the enemy
    Examples:
      | name      |
      | "673975"  |
      | "$*&ynsl" |

  Scenario Outline: AC3 - An enemy cannot have negative values for the attack or strictly negative for health stats.
    Given There is no enemy with name "Nāga"
    When I create an invalid enemy named "Nāga" with speed: <speed> and health: <health>
    Then I cannot create the enemy
    Examples:
      | speed | health |
      | 10    | -1     |
      | -1    | 100    |
      | 0     | 100    |
      | -10   | -100   |
