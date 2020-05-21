package ftl.shard

import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.reports.xml.model.JUnitTestCase
import ftl.reports.xml.model.JUnitTestResult

fun createTestMethodDurationMap(junitResult: JUnitTestResult, args: IArgs): Map<String, Double> {
    // Create a map with information from previous junit run
    return mutableMapOf<String, Double>().apply {
        junitResult.testsuites?.forEach { testsuite ->
            testsuite.testcases
                ?.asSequence()
                ?.filter { testcase -> !testcase.empty() && testcase.time != null }
                ?.map { testcase ->
                    val key = if (args is AndroidArgs) testcase.androidKey() else testcase.iosKey()
                    val time = testcase.time!!.toDouble()
                    key to time
                }
                ?.filter { (_, time) -> time >= 0 }
                ?.forEach { (key, time) -> this[key] = time }
        }
    }
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
