package com.example.test_app.foo

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.test_app.BaseInstrumentedTest
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FooInstrumentedTest : BaseInstrumentedTest() {

    @Test
    fun testFoo() = testMethod()

    @Test
    @Ignore("For testing purpose: https://github.com/Flank/flank/issues/852")
    fun ignoredTestFoo() = testMethod()
}
