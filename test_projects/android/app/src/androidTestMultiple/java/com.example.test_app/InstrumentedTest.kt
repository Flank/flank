package com.example.test_app

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

annotation class Annotation

@RunWith(AndroidJUnit4::class)
class InstrumentedTest : BaseInstrumentedTest() {

    @Test
    @Annotation
    fun test0() = testMethod()

    @Test
    fun test1() = testMethod()

    @Test
    fun test2() = testMethod()

    @Test
    @Ignore("For testing purpose: https://github.com/Flank/flank/issues/852")
    fun ignoredTest1() = testMethod()

    @Test
    @Ignore("For testing purpose: https://github.com/Flank/flank/issues/852")
    fun ignoredTest2() = testMethod()
}
