package ftl.reports

import ftl.adapter.google.toApiModel
import ftl.api.JUnitTest
import ftl.client.junit.parseAllSuitesXml
import ftl.client.junit.parseOneSuiteXml
import ftl.client.xml.JUnitXmlTest
import ftl.domain.junit.merge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HtmlErrorReportTest {

    @Test
    fun `reactJson androidPassXml`() {
        val results = parseOneSuiteXml(JUnitXmlTest.androidPassXml).toApiModel().testsuites!!.process()
        assertTrue(results.isEmpty())
    }

    @Test
    fun `reactJson androidFailXml`() {
        val results: List<HtmlErrorReport.Group> = parseOneSuiteXml(JUnitXmlTest.androidFailXml).toApiModel().testsuites!!.process()

        val group = results.first()
        assertEquals(1, results.size)

        assertEquals("com.example.app.ExampleUiTest#testFails", group.label)
        assertEquals(1, group.items.size)

        val item = group.items.first()
        assertEquals("junit.framework.AssertionFailedError: expected:<true> but was:<false>", item.label)
        assertEquals("", item.url)
    }

    @Test
    fun `reactJson androidFailXml merged`() {
        // 4 tests - 2 pass, 2 fail. we should have 2 failures in the report
        val mergedXml: JUnitTest.Result = parseOneSuiteXml(JUnitXmlTest.androidFailXml).toApiModel()
        mergedXml.merge(mergedXml)

        assertEquals(4, mergedXml.testsuites?.first()?.testcases?.size)

        val results: List<HtmlErrorReport.Group> = mergedXml.testsuites!!.process()

        val group = results.first()
        assertEquals(1, results.size)

        assertEquals("com.example.app.ExampleUiTest#testFails", group.label)
        assertEquals(2, group.items.size)

        group.items.forEach { item ->
            assertEquals("junit.framework.AssertionFailedError: expected:<true> but was:<false>", item.label)
            assertEquals("", item.url)
        }
    }

    @Test
    fun `reactJson iosPassXml`() {
        val results = parseAllSuitesXml(JUnitXmlTest.iosPassXml).toApiModel().testsuites!!.process()
        assertTrue(results.isEmpty())
    }

    @Test
    fun `reactJson iosFailXml`() {
        val results = parseAllSuitesXml(JUnitXmlTest.iosFailXml).toApiModel().testsuites!!.process()

        val group = results.first()
        assertEquals(1, results.size)

        assertEquals("EarlGreyExampleSwiftTests EarlGreyExampleSwiftTests#testBasicSelectionAndAction()", group.label)
        assertEquals(1, group.items.size)

        val item = group.items.first()
        assertEquals(
            "Exception: NoMatchingElementException, failed: caught \"EarlGreyInternalTestInterruptException\", \"Immediately halt execution of testcase\"null",
            item.label
        )
        assertEquals("", item.url)
    }
}
