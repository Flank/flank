package ftl.reports

import com.google.common.truth.Truth.assertThat
import ftl.reports.xml.JUnitXmlTest
import ftl.reports.xml.parseAndroidXml
import ftl.reports.xml.parseIosXml
import org.junit.Test

class HtmlErrorReportTest {

    @Test
    fun reactJson_androidPassXml() {
        val results = HtmlErrorReport.groupItemList(parseAndroidXml(JUnitXmlTest.androidPassXml))
        assertThat(results).isNull()
    }

    @Test
    fun reactJson_androidFailXml() {
        val results = HtmlErrorReport.groupItemList(parseAndroidXml(JUnitXmlTest.androidFailXml))
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
    fun reactJson_androidFailXml_merged() {
        // 4 tests - 2 pass, 2 fail. we should have 2 failures in the report
        val mergedXml = parseAndroidXml(JUnitXmlTest.androidFailXml)
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
    fun reactJson_iosPassXml() {
        val results = HtmlErrorReport.groupItemList(parseIosXml(JUnitXmlTest.iosPassXml))
        assertThat(results).isNull()
    }

    @Test
    fun reactJson_iosFailXml() {
        val results =
            HtmlErrorReport.groupItemList(parseIosXml(JUnitXmlTest.iosFailXml)) ?: throw RuntimeException("null")

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
