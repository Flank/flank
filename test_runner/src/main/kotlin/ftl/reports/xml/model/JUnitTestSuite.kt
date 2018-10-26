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
}
