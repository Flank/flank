package xml

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

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

data class TestSuite(
    @JacksonXmlProperty(isAttribute = true)
    val hostname: String = "",
    @JacksonXmlProperty(isAttribute = true)
    val failures: String = "",
    @JacksonXmlProperty(isAttribute = true)
    val flakes: String = "",
    @JacksonXmlProperty(isAttribute = true)
    val tests: String = "",
    @JacksonXmlProperty(isAttribute = true)
    val name: String = "",
    @JacksonXmlProperty(isAttribute = true)
    val time: String = "",
    @JacksonXmlProperty(isAttribute = true)
    val errors: String = "",

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "testcase")
    val testcase: List<TestCase> = listOf(TestCase(null, null, null, null, null)),

    @JacksonXmlProperty(isAttribute = true)
    val skipped: String = "",
    @JacksonXmlProperty(isAttribute = true)
    val timestamp: String = ""
)

@JacksonXmlRootElement(localName = "testsuites")
data class TestSuites(
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "testsuite")
    val testsuites: List<TestSuite> = listOf(TestSuite())
)

