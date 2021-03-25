package com.example.app.many.tests

import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ManyParametrizedTests(private val paramInt: Int, private val paramString: String) {

    @Test
    fun shouldHopefullyPass() {
        assertTrue(paramInt == paramString.toInt())
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
            arrayOf(1, "1"),
            arrayOf(2, "1"),
            arrayOf(3, "1"),
            arrayOf(4, "1"),
            arrayOf(5, "1"),
            arrayOf(6, "1"),
            arrayOf(7, "1"),
            arrayOf(8, "1"),
            arrayOf(9, "1"),
            arrayOf(10, "10"),
            arrayOf(11, "11"),
            arrayOf(12, "12"),
            arrayOf(13, "13"),
            arrayOf(14, "14"),
            arrayOf(15, "15"),
            arrayOf(16, "16"),
            arrayOf(17, "17"),
            arrayOf(18, "18"),
            arrayOf(19, "19"),
            arrayOf(20, "20"),
            arrayOf(21, "21"),
            arrayOf(22, "22"),
            arrayOf(23, "23"),
            arrayOf(24, "24"),
            arrayOf(25, "25"),
            arrayOf(26, "26"),
            arrayOf(27, "27"),
            arrayOf(28, "28"),
            arrayOf(29, "29"),
            arrayOf(30, "30"),
            arrayOf(31, "31"),
            arrayOf(32, "32"),
            arrayOf(33, "33"),
            arrayOf(34, "34"),
            arrayOf(35, "35"),
            arrayOf(36, "36"),
            arrayOf(37, "37"),
            arrayOf(38, "38"),
            arrayOf(39, "39"),
            arrayOf(40, "40"),
            arrayOf(41, "41"),
            arrayOf(42, "42"),
            arrayOf(43, "43"),
            arrayOf(44, "44"),
            arrayOf(45, "45"),
            arrayOf(46, "46"),
            arrayOf(47, "47"),
            arrayOf(48, "48"),
            arrayOf(49, "49"),
            arrayOf(50, "50"),
            arrayOf(51, "51"),
            arrayOf(52, "52"),
            arrayOf(53, "53"),
            arrayOf(54, "54"),
            arrayOf(55, "55"),
            arrayOf(56, "56"),
            arrayOf(57, "57"),
            arrayOf(58, "58"),
            arrayOf(59, "59")
        )
    }
}
