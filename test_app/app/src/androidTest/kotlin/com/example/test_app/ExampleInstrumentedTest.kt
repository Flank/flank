package com.example.test_app

import android.Manifest
import android.os.SystemClock
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.example.test_app.screenshot.ScreenshotTestRule
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Rule
    @JvmField
    val enableSavingScreenshots = GrantPermissionRule.grant(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )!!

    @Rule
    @JvmField
    val screenshotTestRule = ScreenshotTestRule()

    @Rule
    @JvmField
    val launchActivity = IntentsTestRule(MainActivity::class.java)

    @Test
    fun testPasses() {
        launchActivity.activity.foo()
        assertEquals(true, true)
    }

//    @Test
//    fun testFails() {
//        assertEquals(true, false)
//    }
//
//    @Test
//    fun testIsFlaky() {
//        if (SystemClock.uptimeMillis() % 2 == 0L) {
//            fail()
//        }
//    }
}
