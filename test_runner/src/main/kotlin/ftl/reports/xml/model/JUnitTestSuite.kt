package ftl.reports.xml.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class JUnitTestSuite(
    @JacksonXmlProperty(isAttribute = true)
    var name: String,

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
    var testcases: MutableCollection<JUnitTestCase>?,

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

    /** Call after setting testcases manually to update the statistics (error count, skip count, etc.) */
    fun updateTestStats() {
        this.tests = testcases?.size.toString()
        this.failures = testcases?.count { it.failures?.isNotEmpty() == true }.toString()
        this.errors = testcases?.count { it.errors?.isNotEmpty() == true }.toString()
        this.skipped = testcases?.count { it.skipped() }.toString()
        this.time = testcases?.fold("0") { acc, test -> mergeDouble(acc, test.time) } ?: "0"
    }

    /**
     * Strips all characters except numbers and a period
     * Returns 0 when the string is null
     *
     * Example: z1,23.45 => 123.45 */
    private fun String?.clean(): String {
        if (this == null) return "0"
        return this.replace(Regex("""[^0-9\\.]"""), "")
    }

    private fun mergeInt(a: String?, b: String?): String {
        return (a.clean().toInt() + b.clean().toInt()).toString()
    }

    private fun mergeDouble(a: String?, b: String?): String {
        return "%.3f".format((a.clean().toDouble() + b.clean().toDouble()))
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
            // if test was skipped or empty, then continue to skip it.
            if (testcase.skipped() || testcase.empty()) return@forEach

            // if the test succeeded, use the new time value
            if (testcase.successful() && testcase.time != null) {
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

            if (lastSuccessfulRun.time != null) mergedTime += lastSuccessfulRun.time.toDouble()
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
