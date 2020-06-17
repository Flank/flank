package com.example.test_app

import junit.framework.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ParameterizedTest(private val paramOne: Int, private val paramTwo: String) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
                arrayOf(1, "1"),
                arrayOf(666, "666"),
                arrayOf(54321, "54321")
        )
    }

    @Test
    fun shouldHopefullyPass() {
        assertTrue(paramOne == paramTwo.toInt())
    }
}
