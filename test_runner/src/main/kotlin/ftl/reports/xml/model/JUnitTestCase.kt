package ftl.reports.xml.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

private class FilterNotNull {
    override fun equals(other: Any?): Boolean {
        // other is null     = present
        // other is not null = absent (default value)
        return other != null
    }
}

// https://android.googlesource.com/platform/tools/base/+/tools_r22/ddmlib/src/main/java/com/android/ddmlib/testrunner/XmlTestRunListener.java#256
data class JUnitTestCase(
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var webLink: String? = null

    fun empty(): Boolean {
        return name == null || classname == null || time == null
    }

    fun failed(): Boolean {
        return failures?.isNotEmpty() == true || errors?.isNotEmpty() == true
    }

    fun skipped(): Boolean {
        return skipped == null
    }

    fun successful(): Boolean {
        return failed().not().and(skipped().not())
    }

    fun stackTrace(): String {
        return failures?.joinToString() + errors?.joinToString()
    }
}
