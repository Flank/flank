package cucumber.cukeulator.test

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import io.cucumber.java.en.Then
import cucumber.cukeulator.R


class KotlinSteps {


    @Then("I should see {string} on the display")
    fun I_should_see_s_on_the_display(s: String?) {
        Espresso.onView(withId(R.id.txt_calc_display)).check(ViewAssertions.matches(ViewMatchers.withText(s)))
    }
}
