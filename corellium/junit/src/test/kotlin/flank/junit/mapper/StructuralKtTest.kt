package flank.junit.mapper

import flank.junit.JUnit
import org.junit.Assert.assertEquals
import org.junit.Test

class StructuralKtTest {

    @Test
    fun mapToTestSuitesTest() {
        val expected = listOf(
            JUnit.Suite(
                name = "suite1",
                tests = 3,
                failures = 1,
                errors = 1,
                skipped = 0,
                time = 8.0,
                timestamp = JUnit.dateFormat.format(1_000),
                testcases = listOf(
                    JUnit.Case(
                        name = "test1",
                        classname = "test1.Test1",
                        time = 4.0,
                        error = listOf("some error")
                    ),
                    JUnit.Case(
                        name = "test2",
                        classname = "test1.Test1",
                        time = 2.0,
                        failure = listOf("some assertion failed"),
                    ),
                    JUnit.Case(
                        name = "test1",
                        classname = "test1.Test2",
                        time = 3.0,
                    )
                )
            ),
            JUnit.Suite(
                name = "suite2",
                tests = 1,
                failures = 0,
                errors = 0,
                skipped = 1,
                time = 0.0,
                timestamp = JUnit.dateFormat.format(0),
                testcases = listOf(
                    JUnit.Case(
                        name = "test1",
                        classname = "test1.Test1",
                        time = 0.0,
                        skipped = null,
                    ),
                )
            ),
        )

        val testCases = listOf(
            JUnit.TestResult(
                testName = "test1",
                className = "test1.Test1",
                suiteName = "suite1",
                startAt = 1_000,
                endsAt = 5_000,
                status = JUnit.TestResult.Status.Error,
                stack = listOf("some error")
            ),
            JUnit.TestResult(
                testName = "test2",
                className = "test1.Test1",
                suiteName = "suite1",
                startAt = 6_000,
                endsAt = 8_000,
                status = JUnit.TestResult.Status.Failed,
                stack = listOf("some assertion failed")
            ),
            JUnit.TestResult(
                testName = "test1",
                className = "test1.Test2",
                suiteName = "suite1",
                startAt = 6_000,
                endsAt = 9_000,
                status = JUnit.TestResult.Status.Passed,
                stack = emptyList()
            ),
            JUnit.TestResult(
                testName = "test1",
                className = "test1.Test1",
                suiteName = "suite2",
                startAt = 0,
                endsAt = 0,
                status = JUnit.TestResult.Status.Skipped,
                stack = emptyList()
            ),
        )

        val actual = testCases.mapToTestSuites()

        println(xmlPrettyWriter.writeValueAsString(actual))

        assertEquals(expected, actual)
    }
}
