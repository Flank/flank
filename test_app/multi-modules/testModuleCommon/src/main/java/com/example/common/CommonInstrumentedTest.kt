package com.example.common

import org.junit.Assert

fun testSampleMethod() {
    Assert.assertEquals(1, "1".toInt())
}

fun anotherSampleTestMethod() {
    Assert.assertEquals(2, "2".toInt())
}

fun ignoredTest() {
    Assert.assertEquals(1, 1)
}
