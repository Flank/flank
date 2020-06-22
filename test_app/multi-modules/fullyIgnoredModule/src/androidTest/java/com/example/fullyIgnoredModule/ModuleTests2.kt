package com.example.fullyIgnoredModule

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.common.anotherSampleTestMethod
import com.example.common.ignoredTest
import com.example.common.testSampleMethod
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ModuleTests2 {

    @Test
    @Ignore("For testing #818")
    fun sampleFullyIgnoredModuleAdvancedTest() {
        testSampleMethod()
        anotherSampleTestMethod()
    }

    @Test
    @Ignore("For testing #818")
    fun sampleFullyIgnoredModuleAdvancedTest2() {
        anotherSampleTestMethod()
        testSampleMethod()
    }

    @Test
    @Ignore("For testing #818")
    fun sampleFullyIgnoredModuleIgnoredTest3() {
        ignoredTest()
    }
}
