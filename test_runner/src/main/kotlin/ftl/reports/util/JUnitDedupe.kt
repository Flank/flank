package ftl.reports.util

import ftl.reports.xml.model.JUnitTestCase
import ftl.reports.xml.model.JUnitTestResult

// Read in JUnitReport.xml and remove duplicate results when `flaky-test-attempts` is > 0
// for each test `name="testFails" classname="com.example.app.ExampleUiTest"`
// Keep first result. If next result for the same test is successful, keep last successful result.
object JUnitDedupe {

    private fun JUnitTestCase.key(): String {
        return "${this.classname}#${this.name}"
    }

    fun modify(testResult: JUnitTestResult?) {
        testResult?.testsuites?.forEach { suite ->
            val testCaseMap = mutableMapOf<String, JUnitTestCase>()

            suite.testcases?.forEach { testcase ->
                if (testCaseMap[testcase.key()] == null || testcase.successful()) {
                    testCaseMap[testcase.key()] = testcase
                }
            }

            suite.testcases = testCaseMap.values
            suite.updateTestStats()
        }
    }
}
