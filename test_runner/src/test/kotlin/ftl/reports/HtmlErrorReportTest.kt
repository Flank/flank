package ftl.reports

import com.google.common.truth.Truth.assertThat
import ftl.reports.xml.JUnitXmlTest
import ftl.reports.xml.parseAndroidXml
import ftl.reports.xml.parseIosXml
import org.junit.Test

class HtmlErrorReportTest {

    @Test
    fun reactJson_androidPassXml() {
        val results = HtmlErrorReport.reactJson(parseAndroidXml(JUnitXmlTest.androidPassXml))
        assertThat(results).isNull()
    }

    @Test
    fun reactJson_androidFailXml() {
        val results = HtmlErrorReport.reactJson(parseAndroidXml(JUnitXmlTest.androidFailXml))

        assertThat(results?.first).isEqualTo("[{\"key\":\"group-0\",\"name\":\"com.example.app.ExampleUiTest#testFails\",\"startIndex\":0,\"count\":1}]")
        assertThat(results?.second).isEqualTo("[{\"key\":\"item-0\",\"name\":\"junit.framework.AssertionFailedError: expected:\\u003ctrue\\u003e but was:\\u003cfalse\\u003e\",\"link\":\"\"}]")
    }

    @Test
    fun reactJson_iosPassXml() {
        val results = HtmlErrorReport.reactJson(parseIosXml(JUnitXmlTest.iosPassXml))
        assertThat(results).isNull()
    }

    @Test
    fun reactJson_iosFailXml() {
        val results = HtmlErrorReport.reactJson(parseIosXml(JUnitXmlTest.iosFailXml))
        assertThat(results?.first).isEqualTo("[{\"key\":\"group-0\",\"name\":\"EarlGreyExampleSwiftTests EarlGreyExampleSwiftTests#testBasicSelectionAndAction()\",\"startIndex\":0,\"count\":1}]")
        assertThat(results?.second).isEqualTo("[{\"key\":\"item-0\",\"name\":\"Exception: NoMatchingElementException, failed: caught \\\"EarlGreyInternalTestInterruptException\\\", \\\"Immediately halt execution of testcase\\\"null\",\"link\":\"\"}]")
    }
}
