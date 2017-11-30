package com.example.app;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import org.junit.Rule;
import org.junit.Test;

import android.support.test.rule.ActivityTestRule;

public class ExampleUiTest {

    public @Rule ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testHelloWorld() {
        onView(withText("Hello World!")).check(matches(isDisplayed()));
    }
}
