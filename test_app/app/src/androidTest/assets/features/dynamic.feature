Feature: Programmatic access
  Run deeplink activity from programmatically built intent

  Scenario: Launch new screens
    Given I have a MainActivity
    When I press "button"
    Then I should see "title"