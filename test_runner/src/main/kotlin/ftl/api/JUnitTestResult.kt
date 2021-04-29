package ftl.api

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import ftl.adapter.GoogleJUnitTestFetch
import ftl.adapter.GoogleJUnitTestParse
import ftl.adapter.GoogleLegacyJunitTestParse
import java.io.File

val generateJUnitTestResultFromApi: JUnitTest.Result.GenerateFromApi get() = GoogleJUnitTestFetch
val parseJUnitTestResultFromFile: JUnitTest.Result.ParseFromFiles get() = GoogleJUnitTestParse
val parseJUnitLegacyTestResultFromFile: JUnitTest.Result.ParseFromFiles get() = GoogleLegacyJunitTestParse

object JUnitTest {

    @JacksonXmlRootElement(localName = "testsuites")
    data class Result(
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @JacksonXmlProperty(localName = "testsuite")
        var testsuites: MutableList<Suite>? = null
    ) {
        data class ApiIdentity(
            val projectId: String,
            val matrixIds: List<String>
        )

        interface GenerateFromApi : (ApiIdentity) -> Result

        interface ParseFromFiles : (File) -> Result
    }

    data class Suite(
        @JacksonXmlProperty(isAttribute = true)
        var name: String,

        @JacksonXmlProperty(isAttribute = true)
        var tests: String, // Int

        @JacksonXmlProperty(isAttribute = true)
        var failures: String, // Int

        @JacksonXmlProperty(isAttribute = true)
        var flakes: Int? = null,

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
        val hostname: String? = "localhost", // String.

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JacksonXmlProperty(isAttribute = true)
        val testLabExecutionId: String? = null, // String.

        @JacksonXmlProperty(localName = "testcase")
        var testcases: MutableCollection<Case>?,

        // not used
        @JsonInclude(JsonInclude.Include.NON_NULL)
        val properties: Any? = null, // <properties />

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JacksonXmlProperty(localName = "system-out")
        val systemOut: Any? = null, // <system-out />

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JacksonXmlProperty(localName = "system-err")
        val systemErr: Any? = null // <system-err />
    )

    data class Case(
        // name, classname, and time are always present except for empty test cases <testcase/>
        @JacksonXmlProperty(isAttribute = true)
        val name: String?,

        @JacksonXmlProperty(isAttribute = true)
        val classname: String?,

        @JacksonXmlProperty(isAttribute = true)
        val time: String?,

        // iOS contains multiple failures for a single test.
        // JUnit XML allows arbitrary amounts of failure/error tags
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JacksonXmlProperty(localName = "failure")
        val failures: List<String>? = null,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JacksonXmlProperty(localName = "error")
        val errors: List<String>? = null,

        @JsonInclude(JsonInclude.Include.CUSTOM, valueFilter = FilterNotNull::class)
        val skipped: String? = "absent" // used by FilterNotNull to filter out absent `skipped` values
    ) {

        // Consider to move all properties to constructor if will doesn't conflict with parser
        @JsonInclude(JsonInclude.Include.NON_NULL)
        var webLink: String? = null

        @JacksonXmlProperty(isAttribute = true)
        var flaky: Boolean? = null // use null instead of false
    }

    @Suppress("UnusedPrivateClass")
    private class FilterNotNull {
        override fun equals(other: Any?): Boolean {
            // other is null     = present
            // other is not null = absent (default value)
            return other != null
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
}
