package com.example.test_app

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test

class BaseInstrumentedTest {


    @get:Rule
    val intentsTestRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testMethod() {
        Espresso.onView(ViewMatchers.withText(R.id.button1))
            .check(ViewAssertions.matches(ViewMatchers.withText("toast")))
    }
}
