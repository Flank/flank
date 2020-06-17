package com.example.test_app.parametrized

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import com.example.test_app.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class EspressoParametrizedClassTestParameterized(
    private val textButton: String,
    private val expectedText: String
) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
            arrayOf("toast", "toast"),
            arrayOf("alert", "alert"),
            arrayOf("exception", "exception")
        )
    }

    @Rule
    @JvmField
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Test
    fun clickRightButton() {
        Espresso.onView(ViewMatchers.withText(textButton))
            .check(ViewAssertions.matches(ViewMatchers.withText(expectedText)))
    }
}
