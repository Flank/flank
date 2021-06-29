package com.flank.parameterizedtestsapplication.parameterized

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized


@RunWith(Parameterized::class)
class SampleParameterizedTestLarge {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun inputs() = listOf(
            arrayOf(0, "0"),
            arrayOf(1, "1"),
            arrayOf(2, "2"),
            arrayOf(3, "3"),
            arrayOf(4, "4"),
            arrayOf(0, "0"),
            arrayOf(1, "1"),
            arrayOf(2, "2"),
            arrayOf(3, "3"),
            arrayOf(4, "4"),
            arrayOf(0, "0"),
            arrayOf(1, "1"),
            arrayOf(2, "2"),
            arrayOf(3, "3"),
            arrayOf(4, "4"),
            arrayOf(0, "0"),
            arrayOf(1, "1"),
            arrayOf(2, "2"),
            arrayOf(3, "3"),
            arrayOf(4, "4"),
            arrayOf(0, "0"),
            arrayOf(1, "1"),
            arrayOf(2, "2"),
            arrayOf(3, "3"),
            arrayOf(4, "4"),
            arrayOf(0, "0"),
            arrayOf(1, "1"),
            arrayOf(2, "2"),
            arrayOf(3, "3"),
            arrayOf(4, "4"),
            arrayOf(0, "0"),
            arrayOf(1, "1"),
            arrayOf(2, "2"),
            arrayOf(3, "3"),
            arrayOf(4, "4"),
            arrayOf(0, "0"),
            arrayOf(1, "1"),
            arrayOf(2, "2"),
            arrayOf(3, "3"),
            arrayOf(4, "4"),
            arrayOf(0, "0"),
            arrayOf(1, "1"),
            arrayOf(2, "2"),
            arrayOf(3, "3"),
            arrayOf(4, "4"),
        )
    }

    @Parameterized.Parameter(value = 0)
    @JvmField
    var mTestInteger = 0

    @Parameterized.Parameter(value = 1)
    @JvmField
    var mTestString: String? = null

    @Test
    fun sample_parseValue() {
        assertEquals(mTestString!!.toInt(), mTestInteger)
    }

    @Test
    fun sample_reverseParse() {
        assertEquals(mTestInteger, mTestString!!.toInt())
    }
}
