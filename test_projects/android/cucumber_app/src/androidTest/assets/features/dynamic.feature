Feature: Programmatic access
  Run deeplink activity from programmatically built intent

  Scenario Outline: Check text on buttons
    Given I have a MainActivity
    When I found a button with <buttonText>
    Then I should see <text>
    Examples:
      | buttonText | text |
      | toast      | toast |
      | alert      | alert |
      | exception  | exception |
