package ftl.reports.xml.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class JUnitTestSuite(
    @JacksonXmlProperty(isAttribute = true)
    val name: String,

    @JacksonXmlProperty(isAttribute = true)
    var tests: String, // Int

    @JacksonXmlProperty(isAttribute = true)
    var failures: String, // Int

    @JacksonXmlProperty(isAttribute = true)
    var errors: String, // Int

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(isAttribute = true)
    var skipped: String?, // Int. Android only

    @JacksonXmlProperty(isAttribute = true)
    var time: String, // Double

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(isAttribute = true)
    val timestamp: String?, // String. Android only

    @JacksonXmlProperty(isAttribute = true)
    val hostname: String, // String.

    @JacksonXmlProperty(localName = "testcase")
    var testcases: MutableList<JUnitTestCase>?,

    // not used
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val properties: Any?, // <properties />

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = "system-out")
    val systemOut: Any?, // <system-out />

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JacksonXmlProperty(localName = "system-err")
    val systemErr: Any? // <system-err />
) {

    private fun mergeInt(a: String?, b: String?): String {
        return ((a ?: "0").toInt() + (b ?: "0").toInt()).toString()
    }

    private fun mergeDouble(a: String?, b: String?): String {
        return "%.3f".format(((a ?: "0").toDouble() + (b ?: "0").toDouble()))
    }

    fun merge(other: JUnitTestSuite): JUnitTestSuite {
        if (this.name != other.name) throw RuntimeException("Attempted to merge ${other.name} into ${this.name}")

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

    fun mergeTestTimes(other: JUnitTestSuite): JUnitTestSuite {
        if (this.name != other.name) throw RuntimeException("Attempted to merge ${other.name} into ${this.name}")

        // For each new JUnitTestCase:
        //  * if it failed then pull timing info from old
        //  * remove if not successful in either new or old

        // if we ran no test cases then don't bother merging old times.
        if (this.testcases == null) return this

        val mergedTestCases = mutableListOf<JUnitTestCase>()
        var mergedTime = 0.0

        this.testcases?.forEach { testcase ->
            // if test was skipped, then continue to skip it.
            if (testcase.skipped()) return@forEach

            // if the test succeeded, use the new time value
            if (testcase.successful()) {
                mergedTime += testcase.time.toDouble()
                mergedTestCases.add(
                    JUnitTestCase(
                        name = testcase.name,
                        classname = testcase.classname,
                        time = testcase.time
                    )
                )
                return@forEach
            }

            // if the test we ran failed, copy timing from the last successful run
            val lastSuccessfulRun = other.testcases?.firstOrNull {
                it.successful() && it.name == testcase.name && it.classname == testcase.classname
            } ?: return@forEach

            mergedTime += lastSuccessfulRun.time.toDouble()
            mergedTestCases.add(
                JUnitTestCase(
                    name = testcase.name,
                    classname = testcase.classname,
                    time = lastSuccessfulRun.time
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
}
