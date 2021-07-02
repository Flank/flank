package com.flank.parameterizedtestsapplication.nonparameterized

import android.R
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.ListView
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.filters.MediumTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.flank.parameterizedtestsapplication.MainActivity
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


// @RunWith is required only if you use a mix of JUnit3 and JUnit4.
@MediumTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @Rule
    var rule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Test
    @Throws(Exception::class)
    fun ensureListViewIsPresent() {
        val activity: MainActivity = rule.activity
        val viewById: View = activity.findViewById(R.id.list_container)
        assertThat(viewById, notNullValue())
        assertThat(viewById, instanceOf(ListView::class.java))
        val listView: ListView = viewById as ListView
        val adapter: ListAdapter = listView.adapter
        assertThat(adapter, instanceOf(ArrayAdapter::class.java))
        assertThat(adapter.count, greaterThan(5))
    }
}
