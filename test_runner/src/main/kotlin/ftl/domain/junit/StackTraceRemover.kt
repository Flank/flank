package ftl.domain.junit

import ftl.api.JUnitTest

fun JUnitTest.Result.removeStackTraces() = copy(testsuites = testsuites?.removeStackTraces())

private fun MutableCollection<JUnitTest.Suite>.removeStackTraces() = map { testSuite ->
    testSuite.copy(testcases = testSuite.testcases?.withRemovedStackTraces())
}.toMutableList()

private fun MutableCollection<JUnitTest.Case>.withRemovedStackTraces() =
    map { testCase -> testCase.removeFlakyStackTrace() }.toMutableList()

private fun JUnitTest.Case.removeFlakyStackTrace(): JUnitTest.Case =
    if (flaky == true) copy(errors = null, failures = null) else this
