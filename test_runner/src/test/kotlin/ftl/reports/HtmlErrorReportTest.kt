package ftl.reports

import com.google.common.truth.Truth.assertThat
import ftl.reports.xml.JUnitXmlTest
import ftl.reports.xml.parseAllSuitesXml
import ftl.reports.xml.parseOneSuiteXml
import org.junit.Test

class HtmlErrorReportTest {

    @Test
    fun `reactJson androidPassXml`() {
        val results = HtmlErrorReport.groupItemList(parseOneSuiteXml(JUnitXmlTest.androidPassXml))
        assertThat(results).isNull()
    }

    @Test
    fun `reactJson androidFailXml`() {
        val results = HtmlErrorReport.groupItemList(parseOneSuiteXml(JUnitXmlTest.androidFailXml))
            ?: throw RuntimeException("null")

        val group = results.first
        assertThat(group.size).isEqualTo(1)

        with(group.first()) {
            assertThat(key).isEqualTo("group-0")
            assertThat(name).isEqualTo("com.example.app.ExampleUiTest#testFails")
            assertThat(startIndex).isEqualTo(0)
            assertThat(count).isEqualTo(1)
        }

        val item = results.second
        assertThat(item.size).isEqualTo(1)
        with(item.first()) {
            assertThat(key).isEqualTo("item-0")
            assertThat(name).isEqualTo("junit.framework.AssertionFailedError: expected:<true> but was:<false>")
            assertThat(link).isEqualTo("")
        }
    }

    @Test
    fun `reactJson androidFailXml merged`() {
        // 4 tests - 2 pass, 2 fail. we should have 2 failures in the report
        val mergedXml = parseOneSuiteXml(JUnitXmlTest.androidFailXml)
        mergedXml.merge(mergedXml)

        assertThat(mergedXml.testsuites?.first()?.testcases?.size).isEqualTo(4)

        val results = HtmlErrorReport.groupItemList(mergedXml)
            ?: throw RuntimeException("null")

        val group = results.first
        assertThat(group.size).isEqualTo(1)

        with(group.first()) {
            assertThat(key).isEqualTo("group-0")
            assertThat(name).isEqualTo("com.example.app.ExampleUiTest#testFails")
            assertThat(startIndex).isEqualTo(0)
            assertThat(count).isEqualTo(2)
        }

        val items = results.second
        assertThat(items.size).isEqualTo(2)
        items.forEachIndexed { index, item ->
            with(item) {
                assertThat(key).isEqualTo("item-$index")
                assertThat(name).isEqualTo("junit.framework.AssertionFailedError: expected:<true> but was:<false>")
                assertThat(link).isEqualTo("")
            }
        }
    }

    @Test
    fun `reactJson iosPassXml`() {
        val results = HtmlErrorReport.groupItemList(parseAllSuitesXml(JUnitXmlTest.iosPassXml))
        assertThat(results).isNull()
    }

    @Test
    fun `reactJson iosFailXml`() {
        val results =
            HtmlErrorReport.groupItemList(parseAllSuitesXml(JUnitXmlTest.iosFailXml)) ?: throw RuntimeException("null")

        val group = results.first
        assertThat(group.size).isEqualTo(1)

        with(group.first()) {
            assertThat(key).isEqualTo("group-0")
            assertThat(name).isEqualTo("EarlGreyExampleSwiftTests EarlGreyExampleSwiftTests#testBasicSelectionAndAction()")
            assertThat(startIndex).isEqualTo(0)
            assertThat(count).isEqualTo(1)
        }

        val item = results.second
        assertThat(item.size).isEqualTo(1)
        with(item.first()) {
            assertThat(key).isEqualTo("item-0")
            assertThat(name).isEqualTo("Exception: NoMatchingElementException, failed: caught \"EarlGreyInternalTestInterruptException\", \"Immediately halt execution of testcase\"null")
            assertThat(link).isEqualTo("")
        }
    }
}
