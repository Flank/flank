package com.example.test.benchmark

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith


private const val TIMEOUT = 10_000L

private const val BENCHMARK_TIMEOUT = 15 * 60 * 1000L


@RunWith(AndroidJUnit4::class)
class RunPassMark {

    private object Res {
        const val splash = "android:id/content"
    }

    private object Text {
        const val search = "Search"
        const val appName = "Perform"
        const val accept = "Continue"
        const val ok = "OK"
        const val next = "NEXT"
        const val gotIt = "GOT IT!"
        const val cancel = "CANCEL"
        const val benchmarkType = "RUN BENCHMARK"
        const val results = "Benchmark Results"
    }

    @Test
    fun run() {

        UiDevice.getInstance(
            InstrumentationRegistry.getInstrumentation()
        ).run {
            // Start from the home screen
            pressHome()

            // Open apps menu on pixel launcher
            findObject(UiSelector().descriptionContains(Text.search)).apply {
                swipe(0, visibleBounds.centerY(), 0, 0, 10)
            }

            // Wait for app launcher icon
            wait(Until.hasObject(By.textContains(Text.appName)), TIMEOUT)

            waitForIdle(5000)

            // Click launcher icon
            findObject(UiSelector().textContains(Text.appName)).click()

            waitForIdle(5000)

            // Check version dialog
            findObject(UiSelector().text(Text.ok)).run {
                if (exists()) click()
            }

            Thread.sleep(1000)

            // Check version dialog
            findObject(UiSelector().text(Text.ok)).run {
                if (exists()) click()
            }

            // Check permissions dialog
            findObject(UiSelector().text(Text.accept)).run {
                if (exists()) click()
            }

            // Check version dialog
            findObject(UiSelector().text(Text.ok)).run {
                if (exists()) click()
            }

            // Touch to continue...
            findObject(UiSelector().resourceId(Res.splash)).click()

            // Check tooltip dialog
            findObject(UiSelector().text(Text.next)).run {
                if (exists()) click()
            }

            // Check tooltip dialog
            findObject(UiSelector().text(Text.gotIt)).run {
                if (exists()) click()
            }

            waitForIdle(5000)
            Thread.sleep(2000)

            // Choose proper benchmark screen
            findObject(UiSelector().text(Text.benchmarkType)).click()

            waitForIdle(5000)
            Thread.sleep(2000)

            // Wait until benchmark finish
            wait(Until.hasObject(By.text(Text.cancel)), TIMEOUT)
            wait(Until.gone(By.text(Text.cancel)), BENCHMARK_TIMEOUT)

            // Assert that benchmark results screen is visible
            wait(Until.hasObject(By.text(Text.results)), BENCHMARK_TIMEOUT)

            // Make sure that results was recorded
            Thread.sleep(5000)
        }
    }
}
