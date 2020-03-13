package ftl.reports.api

import com.google.api.services.toolresults.model.StackTrace
import com.google.api.services.toolresults.model.TestCase
import ftl.reports.xml.model.JUnitTestCase

internal fun List<TestCase>.createJUnitTestCases(): List<JUnitTestCase> = map(TestCase::createJUnitTestCase)

private fun TestCase.createJUnitTestCase(): JUnitTestCase {
    val stackTraces = mapOf(
        status to stackTraces?.map(StackTrace::getException)
    )
    return JUnitTestCase(
        name = testCaseReference.name,
        classname = testCaseReference.className,
        time = elapsedTime.format(),
        failures = stackTraces["failed"],
        errors = stackTraces["error"],
        // skipped = true is represented by null. skipped = false is "absent"
        skipped = if (status == "skipped") null else "absent"
    )
}
