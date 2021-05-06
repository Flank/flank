package ftl.reports.util

import ftl.api.JUnitTest
import ftl.domain.junit.mergeDouble
import ftl.domain.junit.skipped
import ftl.domain.junit.successful
import ftl.util.stripNotNumbers

// Read in JUnitReport.xml and remove duplicate results when `num-flaky-test-attempts` is > 0
// for each test `name="testFails" classname="com.example.app.ExampleUiTest"`
// Keep first result. If next result for the same test is successful, keep last successful result.
object JUnitDedupe {

    private fun JUnitTest.Case.key(): String {
        return "${this.classname}#${this.name}"
    }

    fun modify(testResult: JUnitTest.Result?) {
        testResult?.testsuites?.forEach { suite ->
            val testCaseMap = mutableMapOf<String, JUnitTest.Case>()

            suite.testcases?.forEach { testcase ->
                if (testCaseMap[testcase.key()] == null || testcase.successful()) {
                    testCaseMap[testcase.key()] = testcase
                }
            }

            suite.testcases = testCaseMap.values
            suite.updateTestStats()
        }
    }

    /** Call after setting testcases manually to update the statistics (error count, skip count, etc.) */
    private fun JUnitTest.Suite.updateTestStats() {
        this.tests = testcases?.size.toString()
        this.failures = testcases?.count { it.failures?.isNotEmpty() == true }.toString()
        this.errors = testcases?.count { it.errors?.isNotEmpty() == true }.toString()
        this.skipped = testcases?.count { it.skipped() }.toString()
        this.time = testcases?.fold("0") { acc, test -> mergeDouble(acc, test.time.stripNotNumbers()) } ?: "0"
    }
}
