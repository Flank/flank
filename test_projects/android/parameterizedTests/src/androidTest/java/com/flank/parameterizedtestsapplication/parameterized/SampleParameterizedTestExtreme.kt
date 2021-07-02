package com.flank.parameterizedtestsapplication.parameterized

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized


@RunWith(Parameterized::class)
class SampleParameterizedTestExtreme {

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

    @Test
    fun sample_addition_1() {
        assertEquals(mTestInteger + 1, mTestString!!.toInt() + 1)
    }

    @Test
    fun sample_addition_2() {
        assertEquals(mTestInteger + 2, mTestString!!.toInt() + 2)
    }

    @Test
    fun sample_addition_3() {
        assertEquals(mTestInteger + 11, mTestString!!.toInt() + 11)
    }

    @Test
    fun sample_addition_4() {
        assertEquals(mTestInteger + 12, mTestString!!.toInt() + 12)
    }

    @Test
    fun sample_addition_5() {
        assertEquals(mTestInteger + 13, mTestString!!.toInt() + 13)
    }

    @Test
    fun sample_addition_6() {
        assertEquals(mTestInteger + 14, mTestString!!.toInt() + 14)
    }

    @Test
    fun sample_addition_7() {
        assertEquals(mTestInteger + 15, mTestString!!.toInt() + 15)
    }

    @Test
    fun sample_addition_8() {
        assertEquals(mTestInteger + 16, mTestString!!.toInt() + 16)
    }

    @Test
    fun sample_addition_9() {
        assertEquals(mTestInteger + 17, mTestString!!.toInt() + 17)
    }

    @Test
    fun sample_addition_10() {
        assertEquals(mTestInteger + 18, mTestString!!.toInt() + 18)
    }

    @Test
    fun sample_addition_11() {
        assertEquals(mTestInteger + 19, mTestString!!.toInt() + 19)
    }

    @Test
    fun sample_addition_12() {
        assertEquals(mTestInteger + 20, mTestString!!.toInt() + 20)
    }
}
