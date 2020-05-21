package ftl.shard

import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.reports.xml.model.JUnitTestCase
import ftl.reports.xml.model.JUnitTestResult

fun createTestMethodDurationMap(junitResult: JUnitTestResult, args: IArgs): Map<String, Double> {
    val junitMap = mutableMapOf<String, Double>()

    // Create a map with information from previous junit run

    junitResult.testsuites?.forEach { testsuite ->
        testsuite.testcases?.forEach { testcase ->
            if (!testcase.empty() && testcase.time != null) {
                val key = if (args is AndroidArgs) testcase.androidKey() else testcase.iosKey()
                val time = testcase.time.toDouble()
                if (time >= 0) junitMap[key] = time
            }
        }
    }

    return junitMap
}

private fun JUnitTestCase.androidKey(): String {
    return "class $classname#$name"
}

private fun JUnitTestCase.iosKey(): String {
    // FTL iOS XML appends `()` to each test name. ex: `testBasicSelection()`
    // xctestrun file requires classname/name with no `()`
    val testName = name?.substringBefore('(')
    return "$classname/$testName"
}
