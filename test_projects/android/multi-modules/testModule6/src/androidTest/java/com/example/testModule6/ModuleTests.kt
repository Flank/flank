package com.example.testModule6

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.common.anotherSampleTestMethod
import com.example.common.ignoredTest
import com.example.common.testSampleMethod
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ModuleTests {

    @Test
    fun sampleTest() {
        testSampleMethod()
    }

    @Test
    fun sampleTest2() {
        anotherSampleTestMethod()
    }

    @Test
    @Ignore("For testing #818")
    fun sampleTest3() {
        ignoredTest()
    }
}
