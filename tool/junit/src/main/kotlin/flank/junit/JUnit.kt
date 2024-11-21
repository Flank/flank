package flank.junit

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import flank.junit.internal.calculateMedianDurations
import flank.junit.internal.mergeDurations
import flank.junit.internal.parseJUnitTestResults
import flank.junit.mapper.TimeSerializer
import flank.junit.mapper.mapToTestSuites
import flank.junit.mapper.xmlPrettyWriter
import java.io.Writer
import java.text.SimpleDateFormat

// ========================= Functions =========================

/**
 * The default way for generating the structural representation of XML JUnit report, from the list of raw test results.
 *
 * * This early implementation doesn't support flaky tests.
 * * The list of test case results should be sorted in same order as received from console output
 *
 * @receiver List of raw test cases results.
 * @return Structural representation of XML JUnit report
 */
fun List<JUnit.TestResult>.generateJUnitReport(): JUnit.Report =
    JUnit.Report(mapToTestSuites())

/**
 * Write JUnit report as formatted XML string.
 *
 * @receiver Structural representation of XML JUnit report.
 * @param writer The output where report will be written.
 */
fun JUnit.Report.writeAsXml(writer: Writer) {
    xmlPrettyWriter.writeValue(writer, this)
}

/**
 * Merge [JUnit.TestResult] test methods by class names to accumulate duration and return them as classes.
 */
fun List<JUnit.TestResult>.mergeTestCases(byClasses: Set<String>): List<JUnit.TestResult> =
    mergeDurations(byClasses)

/**
 * Calculate associate full test cases names to calculated duration.
 */
fun List<JUnit.TestResult>.calculateTestCaseDurations(): Map<String, Long> =
    calculateMedianDurations()

// ========================= Structures =========================

/**
 * The scope for JUnit structures
 *
 * references:
 * * [format description](https://help.catchsoftware.com/display/ET/JUnit+Format)
 * * [xsd schema](https://github.com/windyroad/JUnit-Schema/blob/master/JUnit.xsd)
 */
object JUnit {

    class Api(
        val parseTestResults: TestResult.Parse = parseJUnitTestResults
    )

    /**
     * Compact representation of test case execution result.
     * Contains all data required to generate JUnitReport.
     */
    data class TestResult(
        val suiteName: String,
        val testName: String,
        val className: String,
        val startAt: Long,
        val endsAt: Long,
        val stack: List<String>,
        val status: Status,
        val flaky: Boolean = false,
    ) {
        enum class Status { Passed, Failed, Error, Skipped }

        /**
         * Search given file or directory for [REPORT_FILE_NAME] and parse as TestResults
         */
        fun interface Parse : (String) -> Sequence<List<TestResult>>
    }

    /**
     * The complete representation of JUnitReport XML file.
     *
     * @property testsuites Contains an aggregation of testsuite results
     */
    @JacksonXmlRootElement(localName = "testsuites")
    data class Report(
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @JacksonXmlProperty(localName = "testsuite")
        val testsuites: List<Suite> = emptyList()
    )

    /**
     * Contains the results of the testsuite execution.
     *
     * @property name Full class name of the test for non-aggregated testsuite documents. Class name without the package for aggregated testsuites documents.
     * @property time Time taken (in seconds) to execute the tests in the suite.
     * @property timestamp when the test was executed. Timezone may not be specified. ISO8601 datetime pattern.
     * @property tests The total number of tests in the suite.
     * @property hostname Host on which the tests were executed. 'localhost' should be used if the hostname cannot be determined.
     * @property properties Properties (e.g., environment settings) set during test execution
     * @property failures The total number of tests in the suite that failed. A failure is a test which the code has explicitly failed by using the mechanisms for that purpose. e.g., via an assertEquals.
     * @property flakes The number of tests classified as flaky.
     * @property errors The total number of tests in the suite that errored. An errored test is one that had an unanticipated problem. e.g., an unchecked throwable; or a problem with the implementation of the test.
     * @property skipped The total number of ignored or skipped tests in the suite.
     * @property systemOut Data that was written to standard out while the test was executed.
     * @property systemErr Data that was written to standard error while the test was executed.
     */
    data class Suite(
        @JacksonXmlProperty(isAttribute = true)
        val name: String,

        @JacksonXmlProperty(isAttribute = true)
        val tests: Int,

        @JacksonXmlProperty(isAttribute = true)
        val failures: Int,

        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        @JacksonXmlProperty(isAttribute = true)
        val flakes: Int = 0,

        @JacksonXmlProperty(isAttribute = true)
        val errors: Int,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @JacksonXmlProperty(isAttribute = true)
        val skipped: Int = 0,

        @JacksonXmlProperty(isAttribute = true)
        @JsonSerialize(using = TimeSerializer::class)
        val time: Double,

        @JacksonXmlProperty(isAttribute = true)
        val timestamp: String,

        @JacksonXmlProperty(isAttribute = true)
        val hostname: String = "localhost",

        @JacksonXmlProperty(localName = "testcase")
        val testcases: Collection<Case>,

        // not used
        @JsonInclude(JsonInclude.Include.NON_NULL)
        val properties: Any? = null,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JacksonXmlProperty(localName = "system-out")
        val systemOut: Any? = null,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JacksonXmlProperty(localName = "system-err")
        val systemErr: Any? = null,
    )

    /**
     * Properties: name, classname, and time; are always present except for empty test cases <testcase/>
     *
     * @property name Name of the test method.
     * @property classname Full class name for the class the test method is in.
     * @property time Time taken (in seconds) to execute the test.
     * @property failure Indicates that the test failed. A failure is a test which the code has explicitly failed by using the mechanisms for that purpose. e.g., via an assertEquals. Contains as a text node relevant data for the failure, e.g., a stack trace
     * @property error Indicates that the test errored. An errored test is one that had an unanticipated problem. e.g., an unchecked throwable; or a problem with the implementation of the test. Contains as a text node relevant data for the error, e.g., a stack trace
     * @property skipped The default value `absent` is used by FilterNotNull to filter out absent `skipped` values.
     * @property flaky Indicates that the test if flaky. Use null instead of false values.
     */
    data class Case(
        @JacksonXmlProperty(isAttribute = true)
        val name: String,

        @JacksonXmlProperty(isAttribute = true)
        val classname: String,

        @JacksonXmlProperty(isAttribute = true)
        @JsonSerialize(using = TimeSerializer::class)
        val time: Double,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        val error: List<String> = emptyList(),

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        val failure: List<String> = emptyList(),

        @JsonInclude(JsonInclude.Include.CUSTOM, valueFilter = FilterNotNull::class)
        val skipped: Unit? = Unit,

        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        @JacksonXmlProperty(isAttribute = true)
        val flaky: Boolean = false,
    )

    @Suppress("UnusedPrivateClass")
    private class FilterNotNull {
        // other != null -> absent (default value)
        // other == null -> present
        override fun equals(other: Any?) = other != null
        override fun hashCode() = javaClass.hashCode()
    }

    private const val ISO8601_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX"

    internal val dateFormat = SimpleDateFormat(ISO8601_DATETIME_PATTERN)

    const val REPORT_FILE_NAME = "JUnitReport.xml"
    const val COST_REPORT_FILE_NAME = "CostReport.json"
}
