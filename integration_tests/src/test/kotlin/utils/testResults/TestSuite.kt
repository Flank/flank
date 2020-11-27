package utils.testResults

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "testsuites")
data class TestSuites(
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "testsuite")
    val testSuites: List<TestSuite> = emptyList()
)

data class TestSuite(
    @JacksonXmlProperty(isAttribute = true)
    val hostname: String = "",
    @JacksonXmlProperty(isAttribute = true)
    val failures: Int = 0,
    @JacksonXmlProperty(isAttribute = true)
    val flakes: Int = 0,
    @JacksonXmlProperty(isAttribute = true)
    val tests: Int = 0,
    @JacksonXmlProperty(isAttribute = true)
    val name: String = "",
    @JacksonXmlProperty(isAttribute = true)
    val time: String = "",
    @JacksonXmlProperty(isAttribute = true)
    val errors: String = "",

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "testcase")
    val testCases: List<TestCase> = emptyList(),

    @JacksonXmlProperty(isAttribute = true)
    val skipped: Int = 0,
    @JacksonXmlProperty(isAttribute = true)
    val timestamp: String = ""
)

data class TestCase(
    @JacksonXmlProperty(isAttribute = true)
    val classname: String?,

    val webLink: String?,

    @JacksonXmlProperty(isAttribute = true)
    val name: String?,

    @JacksonXmlProperty(isAttribute = true)
    val time: String?,

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    val skipped: String?
)
