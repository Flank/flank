package com.example.test_app.steps;

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.test_app.MainActivity
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import org.junit.Rule

class Steps {

    @get:Rule
    lateinit var intentsTestRule: ActivityScenarioRule<MainActivity>

    @Given("I have a MainActivity")
    fun launchActivity() {
        intentsTestRule = ActivityScenarioRule(MainActivity::class.java)
    }

    @When("^I found a button with (\\\\S+)\$")
    fun findButtonWithText(text: String) {
        Espresso.onView(ViewMatchers.withText(text))
            .check(matches(isDisplayed()))
    }

    @Then("^I should see (\\\\S+)\$")
    fun check_text_on_button(text: String) {
        Espresso.onView(ViewMatchers.withText(text))
            .check(matches(ViewMatchers.withText(text)))
    }
}
