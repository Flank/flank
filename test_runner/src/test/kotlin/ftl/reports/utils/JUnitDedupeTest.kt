package ftl.reports.utils

import com.google.common.truth.Truth.assertThat
import flank.common.normalizeLineEnding
import ftl.adapter.google.toApiModel
import ftl.client.junit.parseAllSuitesXml
import ftl.reports.toXmlString
import ftl.reports.util.JUnitDedupe
import ftl.test.util.FlankTestRunner
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

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

        val suites = parseAllSuitesXml(
            File.createTempFile("test", "file").apply { writeText(inputXml) }
        ).toApiModel()
        JUnitDedupe.modify(suites)

        assertThat(suites.toXmlString().normalizeLineEnding()).isEqualTo(expectedXml)
    }
}
