package ftl.domain.junit

import ftl.api.JUnitTest
import ftl.run.exception.FlankGeneralError
import ftl.util.stripNotNumbers
import java.util.Locale

fun JUnitTest.Result.mergeTestTimes(other: JUnitTest.Result?): JUnitTest.Result {
    if (other == null) return this
    if (this.testsuites == null) this.testsuites = mutableListOf()

    // newTestResult.mergeTestTimes(oldTestResult)
    //
    // for each new JUnitTestSuite, check if it exists on old
    // if JUnitTestSuite exists on both then merge test times
    this.testsuites?.forEach { testSuite ->
        val oldSuite = other.testsuites?.firstOrNull { it.name == testSuite.name }
        if (oldSuite != null) testSuite.mergeTestTimes(oldSuite)
    }

    return this
}

fun JUnitTest.Result.merge(other: JUnitTest.Result?): JUnitTest.Result {
    if (other == null) return this
    if (testsuites == null) testsuites = mutableListOf()

    other.testsuites?.forEach { testSuite ->
        val mergeCandidate = this.testsuites?.firstOrNull { it.name == testSuite.name }

        if (mergeCandidate == null) {
            this.testsuites?.add(testSuite)
        } else {
            mergeCandidate.merge(testSuite)
        }
    }

    return this
}

fun JUnitTest.Suite.merge(other: JUnitTest.Suite): JUnitTest.Suite {
    if (this.name != other.name) throw FlankGeneralError("Attempted to merge ${other.name} into ${this.name}")

    // tests, failures, errors
    this.tests = mergeInt(this.tests, other.tests)
    this.failures = mergeInt(this.failures, other.failures)
    this.errors = mergeInt(this.errors, other.errors)
    this.skipped = mergeInt(this.skipped, other.skipped)
    this.time = mergeDouble(this.time, other.time)

    if (this.testcases == null) this.testcases = mutableListOf()
    if (other.testcases?.isNotEmpty() == true) {
        this.testcases?.addAll(other.testcases!!)
    }

    return this
}

fun JUnitTest.Suite.mergeTestTimes(other: JUnitTest.Suite): JUnitTest.Suite {
    if (this.name != other.name) throw FlankGeneralError("Attempted to merge ${other.name} into ${this.name}")

    // For each new JUnitTestCase:
    //  * if it failed then pull timing info from old
    //  * remove if not successful in either new or old

    // if we ran no test cases then don't bother merging old times.
    if (this.testcases == null) return this

    val mergedTestCases = mutableListOf<JUnitTest.Case>()
    var mergedTime = 0.0

    this.testcases?.forEach { testcase ->
        // if test was skipped or empty, then continue to skip it.
        if (testcase.skipped() || testcase.empty()) return@forEach
        val testcaseTime = testcase.time.stripNotNumbers()

        // if the test succeeded, use the new time value
        if (testcase.successful() && testcase.time != null) {
            mergedTime += testcaseTime.toDouble()
            mergedTestCases.add(
                JUnitTest.Case(
                    name = testcase.name,
                    classname = testcase.classname,
                    time = testcaseTime
                )
            )
            return@forEach
        }

        // if the test we ran failed, copy timing from the last successful run
        val lastSuccessfulRun = other.testcases?.firstOrNull {
            it.successful() && it.name == testcase.name && it.classname == testcase.classname
        } ?: return@forEach

        val lastSuccessfulRunTime = lastSuccessfulRun.time.stripNotNumbers()
        if (lastSuccessfulRun.time != null) mergedTime += lastSuccessfulRunTime.toDouble()
        mergedTestCases.add(
            JUnitTest.Case(
                name = testcase.name,
                classname = testcase.classname,
                time = lastSuccessfulRunTime
            )
        )
    }

    this.testcases = mergedTestCases
    this.tests = mergedTestCases.size.toString()
    this.failures = "0"
    this.errors = "0"
    this.skipped = "0"
    this.time = mergedTime.toString()

    return this
}

private fun mergeInt(a: String?, b: String?): String {
    return (a.stripNotNumbers().toInt() + b.stripNotNumbers().toInt()).toString()
}

fun mergeDouble(a: String?, b: String?): String {
    return "%.3f".format(Locale.ROOT, (a.stripNotNumbers().toDouble() + b.stripNotNumbers().toDouble()))
}
