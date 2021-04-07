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

private const val DOWNLOAD_TIMEOUT = 5 * 60 * 1000L

private object Text {
    const val search = "Search"
    const val ok = "OK"
    const val allow = "Allow"
    const val permTitle = "Before we start…"
    const val appName = "3DMark"
    const val benchmarkType = "SLING SHOT"
}

private object Res {
    const val tabLayout = "com.futuremark.dmandroid.application:id/flm_tab_layout_benchmarks"
    const val btnSkip = "com.futuremark.dmandroid.application:id/flm_bt_tutorial_skip"
    const val fabBenchmark = "com.futuremark.dmandroid.application:id/flm_fab_benchmark"
    const val fabSettings = "com.futuremark.dmandroid.application:id/flm_fab_settings"
    const val scoreDetails = "com.futuremark.dmandroid.application:id/flm_ll_score_details_container"
}


@RunWith(AndroidJUnit4::class)
class Run3DMark {
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
            wait(Until.hasObject(By.text(Text.appName)), TIMEOUT)

            waitForIdle(5000)

            // Click 3d mark launcher icon
            findObject(UiSelector().text(Text.appName)).click()

            waitForIdle(5000)

            // Check permissions dialog
            if (findObject(UiSelector().text(Text.permTitle)).exists()) {
                findObject(UiSelector().text(Text.ok)).click()
                findObject(UiSelector().text(Text.allow)).click()
            }

            waitForIdle(5000)
            Thread.sleep(2000)

            // Skip tutorial if needed
            findObject(UiSelector().resourceId(Res.btnSkip)).apply {
                if (exists()) click()
            }

            waitForIdle(5000)

            if (findObject(UiSelector().text(Text.benchmarkType)).exists().not()) {
                findObject(UiSelector().resourceId(Res.tabLayout)).swipeLeft(10)
                waitForIdle(1000)
            }

            // Choose proper benchmark screen
            findObject(UiSelector().text(Text.benchmarkType)).click()
//            click(318, 104)

            waitForIdle(5000)

            // Settings fab is not visible if the additional software is not installed
            if (findObject(UiSelector().resourceId(Res.fabSettings)).exists().not()) {

                // Install additional software
                findObject(UiSelector().resourceId(Res.fabBenchmark)).click()

                // Wait until download finish
                wait(
                    Until.hasObject(By.res(Res.fabSettings)),
                    DOWNLOAD_TIMEOUT
                )
            }

            // Run benchmark
            findObject(UiSelector().resourceId(Res.fabBenchmark)).click()


            waitForIdle(5000)

            // Wait until benchmark finish
            wait(Until.hasObject(By.res(Res.scoreDetails)), BENCHMARK_TIMEOUT)

            // Assert that benchmark results screen is visible
            Assert.assertTrue(findObject(UiSelector().resourceId(Res.scoreDetails)).exists())

            // Make sure that results was recorded
            Thread.sleep(5000)
        }
    }
}
