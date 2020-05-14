package com.example.test_app.test;

import android.app.Activity;
import android.util.Log;
import androidx.test.rule.ActivityTestRule;
import com.example.test_app.MainActivity;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Dominik Barwacz <dombar1@gmail.com> on 2020-04-24.
 */
public class DeeplinkingSteps {

    private final ActivityTestRule<MainActivity> rule;

    public DeeplinkingSteps() {
        this.rule = new ActivityTestRule<>(MainActivity.class, false, false);
    }

    @Given("I have a MainActivity")
    public void I_have_a_MainActivity() {
        assertNotNull(getActivity());
    }

    @When("I press {string}")
    public void I_press(String label) {
        Log.e("Cucumber", "When: " + label);
    }

    @Then("I should see {string}")
    public void I_should_see_s_as_a_screen_title(final String label) {
        Log.e("Cucumber", "Then: " + label);
    }

    /**
     * We launch the activity in Cucumber's {@link Before} hook.
     * See the notes above for the reasons why we are doing this.
     *
     * @throws Exception any possible Exception
     */
    @Before
    public void launchActivity() throws Exception {
        rule.launchActivity(null);
    }

    /**
     * All the clean up of application's data and state after each scenario must happen here
     */
    @After
    public void finishActivity() throws Exception {
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    /**
     * Gets the activity from our test rule.
     *
     * @return the activity
     */
    protected Activity getActivity() {
        return rule.getActivity();
    }
}
