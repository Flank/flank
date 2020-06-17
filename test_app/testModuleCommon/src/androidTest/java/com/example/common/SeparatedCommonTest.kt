package com.example.common

import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

class SeparatedCommonTest  {

    @Test
    fun foo() {
        Assert.assertEquals(true, "true".toBoolean())
    }

    @Test
    fun bar() {
        Assert.assertEquals(false, "false".toBoolean())
    }

    @Test
    @Ignore("For testing #818")
    fun ignore() {
        Assert.assertEquals(true, true)
    }
}