package com.example.test_app

import android.Manifest
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.rule.GrantPermissionRule
import com.example.test_app.screenshot.ScreenshotTestRule
import org.junit.Assert.assertTrue
import org.junit.Rule
import kotlin.random.Random

abstract class BaseInstrumentedTest {

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

    fun testMethod() {
        val result: Boolean = when (BuildConfig.FLAVOR_type) {
            "success" -> true
            "error" -> false
            "flaky" -> Random.nextBoolean()
            else -> throw Error("Invalid flavour type")
        }
        assertTrue(result)
    }
}
