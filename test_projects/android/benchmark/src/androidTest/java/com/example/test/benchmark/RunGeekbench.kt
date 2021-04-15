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
class RunGeekbench {

    private object Text {
        const val search = "Search"
        const val appName = "Geek"
        const val accept = "ACCEPT"
        const val benchmarkType = "RUN CPU BENCHMARK"
        const val runningDialog = "Geekbench 5"
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

            // Wait for 3d mark launcher icon
            wait(Until.hasObject(By.textContains(Text.appName)), TIMEOUT)

            waitForIdle(5000)

            // Click 3d mark launcher icon
            findObject(UiSelector().textContains(Text.appName)).click()

            waitForIdle(5000)

            Thread.sleep(1000)

            // Check permissions dialog
            findObject(UiSelector().text(Text.accept)).run {
                if (exists()) click()
            }

            waitForIdle(5000)
            Thread.sleep(2000)

            // Choose proper benchmark screen
            findObject(UiSelector().text(Text.benchmarkType)).click()

            waitForIdle(5000)
            Thread.sleep(2000)

            // Wait until benchmark finish
            wait(Until.hasObject(By.text(Text.runningDialog)), TIMEOUT)
            wait(Until.gone(By.text(Text.runningDialog)), BENCHMARK_TIMEOUT)

            // Assert that benchmark results screen is visible
            wait(Until.hasObject(By.text(Text.results)), TIMEOUT)

            // Make sure that results was recorded
            Thread.sleep(5000)
        }
    }
}
