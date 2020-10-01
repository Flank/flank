package cucumber.cukeulator.test;

import android.content.Intent;

import androidx.test.filters.SmallTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Test;

import cucumber.cukeulator.CalculatorActivity;
import cucumber.cukeulator.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * The aim of this test is to make sure that it is possible to run non cucumber instrumentation tests.
 */
public class InstrumentationNonCucumberTest {

    private ActivityTestRule<CalculatorActivity> activityTestRule = new ActivityTestRule<>(CalculatorActivity.class, false, false);

    @Before
    public void setUp() throws Exception {
        activityTestRule.launchActivity(new Intent());
    }

    @SmallTest
    @Test
    public void assert_that_click_on_0_is_visible_in_the_text_cal_display() {
        onView(withId(R.id.btn_d_0))
                .perform(click());

        onView(withId(R.id.txt_calc_display))
                .check(matches(withText("0")));
    }
}
