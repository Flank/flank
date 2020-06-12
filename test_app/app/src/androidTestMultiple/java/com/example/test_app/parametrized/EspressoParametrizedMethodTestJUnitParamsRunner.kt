package com.example.test_app.parametrized

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import com.example.test_app.MainActivity
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class EspressoParametrizedMethodTestJUnitParamsRunner {
    @Rule
    @JvmField
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Test
    @Parameters(method = "methodValuesProvider")
    fun clickRightButtonFromMethod(textButton: String, expectedText: String) {
        Espresso.onView(ViewMatchers.withText(textButton))
            .check(ViewAssertions.matches(ViewMatchers.withText(expectedText)))
    }

    @Test
    @Parameters(value = ["toast, toast", "alert, alert", "exception, exception"])
    fun clickRightButtonFromAnnotation(textButton: String, expectedText: String) {
        Espresso.onView(ViewMatchers.withText(textButton))
            .check(ViewAssertions.matches(ViewMatchers.withText(expectedText)))
    }

    private fun methodValuesProvider(): Array<Any>? {
        return arrayOf(
            arrayOf<Any>("toast", "toast"),
            arrayOf<Any>("alert", "alert"),
            arrayOf<Any>("exception", "exception")
        )
    }
}