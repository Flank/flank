package ftl.adapter.google

import ftl.api.JUnitTest
import ftl.client.junit.JUnitTestCase
import ftl.client.junit.JUnitTestResult
import ftl.client.junit.JUnitTestSuite

fun JUnitTestResult?.toApiModel(): JUnitTest.Result = JUnitTest.Result(
    this?.testsuites?.map { it.toApiModel() }.orEmpty().toMutableList()
)

private fun JUnitTestSuite.toApiModel(): JUnitTest.Suite {
    return JUnitTest.Suite(
        name = name,
        tests = tests,
        failures = failures,
        flakes = flakes,
        errors = errors,
        skipped = skipped,
        time = time,
        timestamp = timestamp,
        hostname = hostname,
        testLabExecutionId = testLabExecutionId,
        testcases = testcases?.toApiModel(),
        properties = properties,
        systemOut = systemOut,
        systemErr = systemErr
    )
}

private fun MutableCollection<JUnitTestCase>.toApiModel(): MutableCollection<JUnitTest.Case> {
    return map(JUnitTestCase::toApiModel).toMutableList()
}

private fun JUnitTestCase.toApiModel() = JUnitTest.Case(
    name = name,
    classname = classname,
    time = time,
    failures = failures,
    errors = errors,
    skipped = skipped
)
