package flank.junit.internal

import flank.junit.JUnit
import flank.junit.calculateTestCaseDurations
import org.junit.Assert
import org.junit.Test

class CalculateMedianDurationsKtTest {

    @Test
    fun test() {
        // given
        val results = listOf(
            result("a", 5),
            result("a", 6),
            result("a", 100),
            result("b", 10),
            result("b", 20),
            result("c", 300),
        )

        val expected = mapOf(
            "a#a" to 6L,
            "b#b" to 15L,
            "c#c" to 300L,
        )

        // when
        val actual = results.calculateTestCaseDurations()

        // then
        Assert.assertEquals(expected, actual)
    }
}

private fun result(
    name: String,
    duration: Long
) = JUnit.TestResult(
    className = name,
    testName = name,
    startAt = 0,
    endsAt = duration,
    stack = emptyList(),
    status = JUnit.TestResult.Status.Passed,
    suiteName = ""
)

