package com.example.test_app

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InstrumentedTest : BaseInstrumentedTest() {

    @Test
    fun test() = testMethod()

    @Test
    @Ignore("For testing purpose: https://github.com/Flank/flank/issues/852")
    fun ignoredTest() = testMethod()

    @Test
    @Ignore("For testing purpose: https://github.com/Flank/flank/issues/852")
    fun ignoredTest2() = testMethod()
}
