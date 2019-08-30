package ftl.reports.utils

import com.google.common.truth.Truth.assertThat
import ftl.reports.util.JUnitDedupe
import ftl.reports.xml.parseAllSuitesXml
import ftl.reports.xml.xmlToString
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.normalizeLineEnding
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class JUnitDedupeTest {

    @Test
    fun `Dedupes multiple tests in a suite`() {
        val inputXml = """
<?xml version='1.0' encoding='UTF-8' ?>
<testsuites>
  <testsuite name="" tests="6" failures="3" errors="0" skipped="0" time="2.1" timestamp="2019-03-14T19:21:26" hostname="localhost">
    <testcase name="testFails" classname="com.example.app.ExampleUiTest" time="0.6">
      <failure>junit.framework.AssertionFailedError</failure>
      <webLink>matrices/7494574344413871385</webLink>
    </testcase>
    <testcase name="testPasses" classname="com.example.app.ExampleUiTest" time="0.5">
      <webLink>matrices/7494574344413871385</webLink>
    </testcase>
    <testcase name="testFails" classname="com.example.app.ExampleUiTest" time="0.4">
      <failure>junit.framework.AssertionFailedError</failure>
      <webLink>matrices/7494574344413871385</webLink>
    </testcase>
    <testcase name="testPasses" classname="com.example.app.ExampleUiTest" time="0.3">
      <webLink>matrices/7494574344413871385</webLink>
    </testcase>
    <testcase name="testFlaky" classname="com.example.app.ExampleUiTest" time="0.2">
      <webLink>matrices/7494574344413871385</webLink>
    </testcase>
    <testcase name="testFlaky" classname="com.example.app.ExampleUiTest" time="0.1">
      <failure>junit.framework.AssertionFailedError</failure>
      <webLink>matrices/7494574344413871385</webLink>
    </testcase>
  </testsuite>
</testsuites>

        """.trimIndent()

        val expectedXml = """
<?xml version='1.0' encoding='UTF-8' ?>
<testsuites>
  <testsuite name="" tests="3" failures="1" errors="0" skipped="0" time="1.100" timestamp="2019-03-14T19:21:26" hostname="localhost">
    <testcase name="testFails" classname="com.example.app.ExampleUiTest" time="0.6">
      <failure>junit.framework.AssertionFailedError</failure>
      <webLink>matrices/7494574344413871385</webLink>
    </testcase>
    <testcase name="testPasses" classname="com.example.app.ExampleUiTest" time="0.3">
      <webLink>matrices/7494574344413871385</webLink>
    </testcase>
    <testcase name="testFlaky" classname="com.example.app.ExampleUiTest" time="0.2">
      <webLink>matrices/7494574344413871385</webLink>
    </testcase>
  </testsuite>
</testsuites>

        """.trimIndent()

        val suites = parseAllSuitesXml(inputXml)
        JUnitDedupe.modify(suites)

        assertThat(suites.xmlToString().normalizeLineEnding()).isEqualTo(expectedXml)
    }
}
