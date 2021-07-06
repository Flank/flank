package flank.junit.internal

import flank.junit.JUnit
import flank.junit.mergeTestCases
import org.junit.Assert
import org.junit.Test

class MergeDurationsKtTest {

    /**
     * Only test case methods of classes specified in set will be merged into classes.
     */
    @Test
    fun mixed() {
        // given
        val classes = setOf("b", "d")

        val results = listOf(
            result("a", "a", 0, 10),
            result("b", "b[0]", 10, 110),
            result("b", "b[1]", 110, 120),
            result("b", "b[2]", 120, 140),
            result("c", "c", 140, 440),
            result("d", "d[name: a]", 440, 470),
            result("d", "d[name: b]", 470, 500),
        )

        val expected = listOf(
            result("a", "a", 0, 10),
            result("c", "c", 140, 440),
            result("b", "", 10, 140),
            result("d", "", 440, 500),
        )

        // when
        val actual = results.mergeTestCases(classes)

        // then
        Assert.assertEquals(expected, actual)
    }


    /**
     * All test case methods will be merged into test case classes.
     */
    @Test
    fun allToMerge() {
        // given
        val classes = setOf("a", "b", "c", "d")

        val results = listOf(
            result("a", "a", 0, 10),
            result("b", "b[0]", 10, 110),
            result("b", "b[1]", 110, 120),
            result("b", "b[2]", 120, 140),
            result("c", "c", 140, 440),
            result("d", "d[name: a]", 440, 470),
            result("d", "d[name: b]", 470, 500),
        )

        val expected = listOf(
            result("a", "", 0, 10),
            result("b", "", 10, 140),
            result("c", "", 140, 440),
            result("d", "", 440, 500),
        )

        // when
        val actual = results.mergeTestCases(classes)

        // then
        Assert.assertEquals(expected, actual)
    }

    /**
     * No test cases will be merged, because of missing class name.
     */
    @Test
    fun missingClassName() {
        // given
        val results = listOf(
            result("a", "a", 0, 10),
            result("c", "c", 20, 60),
        )

        val expected = listOf(
            result("a", "a", 0, 10),
            result("c", "c", 20, 60),
        )

        // when
        val actual = results.mergeTestCases(setOf("e"))

        // then
        Assert.assertEquals(expected, actual)
    }

    /**
     * No test cases will be merged, because of empty set.
     */
    @Test
    fun emptySet() {
        // given
        val results = listOf(
            result("a", "a", 0, 10),
            result("c", "c", 20, 60),
        )

        val expected = listOf(
            result("a", "a", 0, 10),
            result("c", "c", 20, 60),
        )

        // when
        val actual = results.mergeTestCases(setOf())

        // then
        Assert.assertEquals(expected, actual)
    }
}

private fun result(
    className: String,
    name: String,
    start: Long,
    end: Long,
) = JUnit.TestResult(
    className = className,
    testName = name,
    startAt = start,
    endsAt = end,
    stack = emptyList(),
    status = JUnit.TestResult.Status.Passed,
    suiteName = "",
)
