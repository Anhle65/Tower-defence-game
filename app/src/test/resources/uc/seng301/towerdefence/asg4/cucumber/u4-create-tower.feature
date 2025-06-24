Feature: U4 As Alex, I want to create towers so I can play against enemies in a round
  Scenario Outline: AC1 - I can create a tower with a unique (within my towers) non-empty name, and 'attack' stat greater than or equal to 1.
    Given I create a player named <player name>
    When I add a tower named <tower name> to player <player name> with attack <attack>
    Then the tower <tower name> is added to <player name> towers and has <attack> attack
    Examples:
      | player name | tower name  | attack |
      | "John"      | "Ice Tower" | 4      |
      | "John"      | "Tower"     | 5      |

  Scenario Outline: AC2 - I can not create a tower with a non-alphabetic name
    Given I create a player named <player name>
    When I add a tower named <tower name> to player <player name> with attack <attack>
    Then I am told I can not create the tower
    Examples:
      | player name | tower name    | attack |
      | "Paul"      | "123"         | 4      |
      | "Paul"      | "!@#EWSQE123" | 5      |

  Scenario Outline: AC3 - I can not create a tower with a 0 or negative attack stat
    Given I create a player named <player name>
    When I add a tower named <tower name> to player <player name> with attack <attack>
    Then I am told I can not create the tower
    Examples:
      | player name | tower name | attack |
      | "Paul"      | "Tower1"   | 0      |
      | "Paul"      | "Tower2"   | -1     |

  Scenario: AC4 - I can not create a tower with a duplicate name
    Given I create a player named "Joel"
    And I add a tower named "Duplicate Tower" to player "Joel" with attack 3
    When I add a tower named "Duplicate Tower" to player "Joel" with attack 3
    Then I am told I can not create the tower

  Scenario: AC5 - I can not create a tower when I already have 5 towers
    Given I create a player named "Zoey"
    And player "Zoey" already has 5 towers
    When I add a tower named "6th Tower" to player "Zoey" with attack 3
    Then I am told I can not create the tower
