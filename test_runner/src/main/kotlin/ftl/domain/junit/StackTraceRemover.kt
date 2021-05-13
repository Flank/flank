package ftl.domain.junit

import ftl.api.JUnitTest

fun JUnitTest.Result.removeStackTraces() = JUnitTest.Result(testsuites?.removeStackTraces())

private fun MutableCollection<JUnitTest.Suite>.removeStackTraces() = map { testSuite ->
    testSuite.copy(testcases = testSuite.testcases?.withRemovedStackTraces()?.toMutableList())
}.toMutableList()

private fun MutableCollection<JUnitTest.Case>.withRemovedStackTraces() =
    map { testCase -> testCase.removeFlakyStackTrace() }

private fun JUnitTest.Case.removeFlakyStackTrace(): JUnitTest.Case = if (flaky == true) {
    copy(
        errors = null,
        failures = null
    )
} else this
