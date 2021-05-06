package ftl.client.junit

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

/**
 * Firebase generates testsuites for iOS.
 * .xctestrun file may contain multiple test bundles (each one is a testsuite) */
@JacksonXmlRootElement(localName = "testsuites")
data class JUnitTestResult(
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JacksonXmlProperty(localName = "testsuite")
    var testsuites: MutableList<JUnitTestSuite>? = null
)
