package ftl.client.junit

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
    var testcases: MutableCollection<JUnitTestCase>?,

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
