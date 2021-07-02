package com.flank.parameterizedtestsapplication.parameterized

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized


@RunWith(Parameterized::class)
class SampleParameterizedTestSmall {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun inputs() = listOf(
            arrayOf(
                0,
                "0"
            ),
            arrayOf(
                1,
                "1"
            )
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
}
