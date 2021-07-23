package com.example.test_app

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.filters.Suppress
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InstrumentedTestSdkSuppress : BaseInstrumentedTest() {

    @Test
    @SdkSuppress(minSdkVersion = 27)
    fun test27() = testMethod()

    @Test
    @SdkSuppress(minSdkVersion = 27, maxSdkVersion = 29)
    fun test2729() = testMethod()

    @Test
    @SdkSuppress(maxSdkVersion = 29)
    fun test29() = testMethod()
}
