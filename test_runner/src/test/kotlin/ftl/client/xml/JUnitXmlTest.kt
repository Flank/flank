package ftl.client.xml

import com.google.common.truth.Truth.assertThat
import flank.common.normalizeLineEnding
import ftl.client.junit.parseAllSuitesXml
import ftl.client.junit.parseOneSuiteXml
import ftl.doctor.assertEqualsIgnoreNewlineStyle
import ftl.domain.junit.merge
import ftl.domain.junit.mergeTestTimes
import ftl.reports.toXmlString
import ftl.run.exception.FlankGeneralError
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.nio.file.Paths

class JUnitXmlTest {

    companion object {
        private const val xmlRoot = "./src/test/kotlin/ftl/fixtures/ftl_junit_xml"
        val androidPassXml = Paths.get("$xmlRoot/android_pass.xml")!!
        val androidFailXml = Paths.get("$xmlRoot/android_fail.xml")!!
        val iosPassXml = Paths.get("$xmlRoot/ios_pass.xml")!!
        val iosFailXml = Paths.get("$xmlRoot/ios_fail.xml")!!
        val iosLargeNum = Paths.get("$xmlRoot/ios_large_num.xml")!!
        val androidSkipped = """
<testsuite name="" tests="2" failures="0" errors="0" skipped="1" time="0.026" timestamp="2018-10-26T19:57:28" hostname="localhost">
    <properties/>
    <testcase name="testFails" classname="com.example.app.ExampleUiTest" time="0.0">
        <skipped/>
    </testcase>
    <testcase name="testPasses" classname="com.example.app.ExampleUiTest" time="0.001"/>
</testsuite>
        """.trimIndent()
    }

    @Test
    fun `empty testcase`() {
        val xml = """
<testsuites>
  <testsuite name="" tests="0" failures="0" errors="0" skipped="0" time="0.0" timestamp="2018-11-22T00:56:07" hostname="localhost">
    <testcase/>
  </testsuite>
</testsuites>
        """.trimIndent()

        parseAllSuitesXml(xml.writeToTempFile())
    }

    @Test
    fun `empty testcase -- infrastructure error result`() {
        val xml = "<testsuites/>"

        parseAllSuitesXml(xml.writeToTempFile()).run {
            assertThat(testsuites).isNull()
        }
    }

    @Test
    fun `merge android`() {
        val mergedXml = parseOneSuiteXml(androidPassXml)
            .merge(parseOneSuiteXml(androidFailXml))
        val merged = mergedXml.toXmlString().normalizeLineEnding()

        val testSuite = mergedXml.testsuites?.first() ?: throw java.lang.RuntimeException("no test suite")
        assertThat(testSuite.name).isEqualTo("")
        assertThat(testSuite.tests).isEqualTo("3")
        assertThat(testSuite.failures).isEqualTo("1")
        assertThat(testSuite.errors).isEqualTo("0")
        assertThat(testSuite.skipped).isEqualTo("0")
        assertThat(testSuite.time).isEqualTo((3.87 + 2.278).toString())
        assertThat(testSuite.timestamp).isEqualTo("2018-09-14T20:45:55")
        assertThat(testSuite.hostname).isEqualTo("localhost")
        assertThat(testSuite.testLabExecutionId).isEqualTo("matrix-1234_execution-asdf")

        val expected = """
<?xml version='1.0' encoding='UTF-8' ?>
<testsuites>
  <testsuite name="" tests="3" failures="1" errors="0" skipped="0" time="6.148" timestamp="2018-09-14T20:45:55" hostname="localhost" testLabExecutionId="matrix-1234_execution-asdf">
    <testcase name="testPasses" classname="com.example.app.ExampleUiTest" time="0.328"/>
    <testcase name="testFails" classname="com.example.app.ExampleUiTest" time="0.857">
      <failure>junit.framework.AssertionFailedError: expected:&lt;true> but was:&lt;false>
junit.framework.Assert.fail(Assert.java:50)</failure>
    </testcase>
    <testcase name="testPasses" classname="com.example.app.ExampleUiTest" time="0.276"/>
  </testsuite>
</testsuites>

        """.trimIndent()
        assertThat(merged).isEqualTo(expected)
    }

    @Test
    fun `merge ios`() {
        val merged =
            parseAllSuitesXml(iosPassXml)
                .merge(parseAllSuitesXml(iosFailXml))
                .toXmlString()
                .normalizeLineEnding()
        val expected = """
<?xml version='1.0' encoding='UTF-8' ?>
<testsuites>
  <testsuite name="EarlGreyExampleSwiftTests" tests="4" failures="1" errors="0" skipped="0" time="51.773" hostname="localhost">
    <testcase name="testBasicSelection()" classname="EarlGreyExampleSwiftTests" time="2.0"/>
    <testcase name="testBasicSelectionActionAssert()" classname="EarlGreyExampleSwiftTests" time="0.712"/>
    <testcase name="testBasicSelectionActionAssert()" classname="EarlGreyExampleSwiftTests" time="0.719"/>
    <testcase name="testBasicSelectionAndAction()" classname="EarlGreyExampleSwiftTests" time="0.584">
      <failure>Exception: NoMatchingElementException</failure>
      <failure>failed: caught "EarlGreyInternalTestInterruptException", "Immediately halt execution of testcase"</failure>
    </testcase>
  </testsuite>
</testsuites>

        """.trimIndent()
        assertThat(merged).isEqualTo(expected)
    }

    @Test
    fun `Merge iOS large time`() {
        val merged =
            parseAllSuitesXml(iosLargeNum)
                .merge(parseAllSuitesXml(iosLargeNum))
                .toXmlString()
                .normalizeLineEnding()

        val expected = """
<?xml version='1.0' encoding='UTF-8' ?>
<testsuites>
  <testsuite name="EarlGreyExampleSwiftTests" tests="199998" failures="199998" errors="199998" skipped="0" time="199999.762" hostname="localhost">
    <testcase name="testBasicSelectionActionAssert()" classname="EarlGreyExampleSwiftTests" time="0"/>
    <testcase name="testBasicSelectionActionAssert()" classname="EarlGreyExampleSwiftTests" time="0"/>
  </testsuite>
</testsuites>

        """.trimIndent()
        assertThat(merged).isEqualTo(expected)
    }

    @Test
    fun `parse androidSkipped`() {
        val parsed = parseOneSuiteXml(androidSkipped.writeToTempFile())
        assertThat(parsed.testsuites?.first()?.testcases?.first()?.skipped).isNull()
    }

    @Test
    fun `merge androidSkipped`() {
        val merged = parseOneSuiteXml(androidSkipped.writeToTempFile())
        merged.merge(merged)
        val actual = merged.toXmlString().normalizeLineEnding()

        assertThat(actual).isEqualTo(
            """
<?xml version='1.0' encoding='UTF-8' ?>
<testsuites>
  <testsuite name="" tests="4" failures="0" errors="0" skipped="2" time="0.052" timestamp="2018-10-26T19:57:28" hostname="localhost">
    <testcase name="testFails" classname="com.example.app.ExampleUiTest" time="0.0">
      <skipped/>
    </testcase>
    <testcase name="testPasses" classname="com.example.app.ExampleUiTest" time="0.001"/>
    <testcase name="testFails" classname="com.example.app.ExampleUiTest" time="0.0">
      <skipped/>
    </testcase>
    <testcase name="testPasses" classname="com.example.app.ExampleUiTest" time="0.001"/>
  </testsuite>
</testsuites>

            """.trimIndent()
        )
    }

    @Test
    fun `unknown xml property`() {
        val unknownXml = """
<?xml version='1.0' encoding='UTF-8' ?>
<testsuites>
  <testsuite random="prop" name="EarlGreyExampleSwiftTests" tests="4" failures="1" errors="0" skipped="0" time="51.773" hostname="localhost">
    <testcase name="a()" classname="a" time="1.0" random="prop"/>
  </testsuite>
</testsuites>
        """.trimIndent()

        val expected = """
<?xml version='1.0' encoding='UTF-8' ?>
<testsuites>
  <testsuite name="EarlGreyExampleSwiftTests" tests="4" failures="1" errors="0" skipped="0" time="51.773" hostname="localhost">
    <testcase name="a()" classname="a" time="1.0"/>
  </testsuite>
</testsuites>

        """.trimIndent()

        val parsed = parseAllSuitesXml(unknownXml.writeToTempFile()).toXmlString()

        assertEqualsIgnoreNewlineStyle(parsed, expected)
    }

    @Test
    fun `junitXmlToString androidPassXml`() {
        val parsed = parseOneSuiteXml(androidPassXml).toXmlString().normalizeLineEnding()
        val expected = """
<?xml version='1.0' encoding='UTF-8' ?>
<testsuites>
  <testsuite name="" tests="1" failures="0" errors="0" skipped="0" time="2.278" timestamp="2018-09-14T20:45:55" hostname="localhost" testLabExecutionId="matrix-1234_execution-asdf">
    <testcase name="testPasses" classname="com.example.app.ExampleUiTest" time="0.328"/>
  </testsuite>
</testsuites>

        """.trimIndent()

        assertThat(parsed).isEqualTo(expected)
    }

    @Test
    fun `junitXmlToString androidFailXml`() {
        val parsed = parseOneSuiteXml(androidFailXml).toXmlString().normalizeLineEnding()
        val expected = """
<?xml version='1.0' encoding='UTF-8' ?>
<testsuites>
  <testsuite name="" tests="2" failures="1" errors="0" skipped="0" time="3.87" timestamp="2018-09-09T00:16:36" hostname="localhost">
    <testcase name="testFails" classname="com.example.app.ExampleUiTest" time="0.857">
      <failure>junit.framework.AssertionFailedError: expected:&lt;true> but was:&lt;false>
junit.framework.Assert.fail(Assert.java:50)</failure>
    </testcase>
    <testcase name="testPasses" classname="com.example.app.ExampleUiTest" time="0.276"/>
  </testsuite>
</testsuites>

        """.trimIndent()

        assertThat(parsed).isEqualTo(expected)
    }

    @Test
    fun `junitXmlToString iosPassXml`() {
        val parsed = parseAllSuitesXml(iosPassXml).toXmlString().normalizeLineEnding()
        val expected = """
<?xml version='1.0' encoding='UTF-8' ?>
<testsuites>
  <testsuite name="EarlGreyExampleSwiftTests" tests="2" failures="0" errors="0" time="25.892" hostname="localhost">
    <testcase name="testBasicSelection()" classname="EarlGreyExampleSwiftTests" time="2.0"/>
    <testcase name="testBasicSelectionActionAssert()" classname="EarlGreyExampleSwiftTests" time="0.712"/>
  </testsuite>
</testsuites>

        """.trimIndent()

        assertThat(parsed).isEqualTo(expected)
    }

    @Test
    fun `junitXmlToString iosFailXml`() {
        val parsed = parseAllSuitesXml(iosFailXml).toXmlString().normalizeLineEnding()
        val expected = """
<?xml version='1.0' encoding='UTF-8' ?>
<testsuites>
  <testsuite name="EarlGreyExampleSwiftTests" tests="2" failures="1" errors="0" time="25.881" hostname="localhost">
    <testcase name="testBasicSelectionActionAssert()" classname="EarlGreyExampleSwiftTests" time="0.719"/>
    <testcase name="testBasicSelectionAndAction()" classname="EarlGreyExampleSwiftTests" time="0.584">
      <failure>Exception: NoMatchingElementException</failure>
      <failure>failed: caught "EarlGreyInternalTestInterruptException", "Immediately halt execution of testcase"</failure>
    </testcase>
  </testsuite>
</testsuites>

        """.trimIndent()

        assertThat(parsed).isEqualTo(expected)
    }

    @Test
    fun `parse androidPassXml`() {
        val testSuite = parseOneSuiteXml(androidPassXml)
            .testsuites?.first() ?: throw FlankGeneralError("Missing test suite")

        with(testSuite) {
            assertThat(name).isEqualTo("")
            assertThat(tests).isEqualTo("1")
            assertThat(failures).isEqualTo("0")
            assertThat(errors).isEqualTo("0")
            assertThat(skipped).isEqualTo("0")
            assertThat(time).isEqualTo("2.278")
            assertThat(timestamp).isEqualTo("2018-09-14T20:45:55")
            assertThat(hostname).isEqualTo("localhost")
        }

        val test = testSuite.testcases?.first()
        assertThat(test).isNotNull()
        if (test != null) {
            with(test) {
                assertThat(name).isEqualTo("testPasses")
                assertThat(classname).isEqualTo("com.example.app.ExampleUiTest")
                assertThat(time).isEqualTo("0.328")
                assertThat(failures).isNull()
                assertThat(errors).isNull()
            }
        }
    }

    @Test
    fun `parse androidFailXml`() {
        val testSuite = parseOneSuiteXml(androidFailXml)
            .testsuites?.first() ?: throw FlankGeneralError("Missing test suite")

        with(testSuite) {
            assertThat(name).isEqualTo("")
            assertThat(tests).isEqualTo("2")
            assertThat(failures).isEqualTo("1")
            assertThat(errors).isEqualTo("0")
            assertThat(skipped).isEqualTo("0")
            assertThat(time).isEqualTo("3.87")
            assertThat(timestamp).isEqualTo("2018-09-09T00:16:36")
            assertThat(hostname).isEqualTo("localhost")
        }

        val test = testSuite.testcases?.first()
        assertThat(test).isNotNull()
        if (test != null) {
            with(test) {
                assertThat(name).isEqualTo("testFails")
                assertThat(classname).isEqualTo("com.example.app.ExampleUiTest")
                assertThat(time).isEqualTo("0.857")

                assertThat(failures).isNotNull()

                val firstFailure = failures?.first() ?: "null"
                assertThat(failures?.size).isEqualTo(1)
                assertThat(firstFailure).contains("junit.framework.Assert.fail(Assert.java:50)")

                assertThat(errors).isNull()
            }
        }
    }

    @Test
    fun `parse iosPassXml`() {
        val testSuites = parseAllSuitesXml(iosPassXml)
        val testSuite = testSuites.testsuites?.first()
        assertThat(testSuite).isNotNull()

        if (testSuite != null) {
            with(testSuite) {
                assertThat(name).isEqualTo("EarlGreyExampleSwiftTests")
                assertThat(tests).isEqualTo("2")
                assertThat(failures).isEqualTo("0")
                assertThat(errors).isEqualTo("0")
                assertThat(skipped).isNull()
                assertThat(time).isEqualTo("25.892")
                assertThat(timestamp).isNull()
                assertThat(hostname).isEqualTo("localhost")
            }
        }

        val test = testSuite?.testcases?.first()
        assertThat(test).isNotNull()
        if (test != null) {
            with(test) {
                assertThat(name).isEqualTo("testBasicSelection()")
                assertThat(classname).isEqualTo("EarlGreyExampleSwiftTests")
                assertThat(time).isEqualTo("2.0")
                assertThat(failures).isNull()
                assertThat(errors).isNull()
            }
        }
    }

    @Test
    fun `parse iosFailXml`() {
        val testSuites = parseAllSuitesXml(iosFailXml)
        val testSuite = testSuites.testsuites?.first()
        assertThat(testSuite).isNotNull()

        if (testSuite != null) {
            with(testSuite) {
                assertThat(name).isEqualTo("EarlGreyExampleSwiftTests")
                assertThat(tests).isEqualTo("2")
                assertThat(failures).isEqualTo("1")
                assertThat(errors).isEqualTo("0")
                assertThat(skipped).isNull()
                assertThat(time).isEqualTo("25.881")
                assertThat(timestamp).isNull()
                assertThat(hostname).isEqualTo("localhost")
            }
        }

        val test = testSuite?.testcases?.last()
        assertThat(test).isNotNull()
        if (test != null) {
            with(test) {
                assertThat(name).isEqualTo("testBasicSelectionAndAction()")
                assertThat(classname).isEqualTo("EarlGreyExampleSwiftTests")
                assertThat(time).isEqualTo("0.584")

                assertThat(failures).isNotNull()

                val firstFailure = failures?.first() ?: "null"
                assertThat(failures?.size).isEqualTo(2)
                assertThat(firstFailure).contains("Exception: NoMatchingElementException")

                assertThat(errors).isNull()
            }
        }
    }

    @Test
    fun `merge testTimes`() {
        /**
         * 1. First run generates local merged JUnit XML
         *    - Firebase XML downloaded from each shard
         *    - Merged XML is saved locally
         *
         * 2. Time XML is generated from merged JUnit XML
         *    - Only passed tests
         *    - Failed tests from current run use timing data from last run
         *    - Uploaded to Google Cloud Storage
         *    * Feedback: instead of overwriting time ... use average?
         */

        val newRun = """
<?xml version='1.0' encoding='UTF-8' ?>
<testsuites>
  <testsuite name="EarlGreyExampleSwiftTests" tests="4" failures="1" errors="0" skipped="0" time="51.773" hostname="localhost">
    <testcase name="a()" classname="a" time="1.0"/>
    <testcase name="b()" classname="b" time="2.0"/>
    <testcase name="c()" classname="c" time="0.584">
      <failure>Exception: NoMatchingElementException</failure>
      <failure>failed: caught "EarlGreyInternalTestInterruptException", "Immediately halt execution of testcase"</failure>
    </testcase>
    <testcase name="d()" classname="d" time="0.0">
        <skipped/>
    </testcase>
  </testsuite>
</testsuites>
        """.trimIndent()

        val oldRun = """
<?xml version='1.0' encoding='UTF-8' ?>
<testsuites>
  <testsuite name="EarlGreyExampleSwiftTests" tests="4" failures="1" errors="0" skipped="0" time="51.773" hostname="localhost">
    <testcase name="a()" classname="a" time="5.0"/>
    <testcase name="b()" classname="b" time="6.0"/>
    <testcase name="c()" classname="c" time="7.0"/>
    <testcase name="d()" classname="d" time="8.0"/>
  </testsuite>
</testsuites>
        """.trimIndent()

        // new run has 2 passing, 1 failure, and 1 skipped
        // * a() and b() passed in newRun and are copied over
        // * c() failed in newRun and passed in oldRun. timing info copied over from oldRun
        // * d() was skipped in newRun and successful in oldRun. d() is excluded from the merged result

        val merged =
            parseAllSuitesXml(newRun.writeToTempFile())
                .mergeTestTimes(parseAllSuitesXml(oldRun.writeToTempFile())).toXmlString().normalizeLineEnding()
        val expected = """
<?xml version='1.0' encoding='UTF-8' ?>
<testsuites>
  <testsuite name="EarlGreyExampleSwiftTests" tests="3" failures="0" errors="0" skipped="0" time="10.0" hostname="localhost">
    <testcase name="a()" classname="a" time="1.0"/>
    <testcase name="b()" classname="b" time="2.0"/>
    <testcase name="c()" classname="c" time="7.0"/>
  </testsuite>
</testsuites>

        """.trimIndent()
        assertThat(merged).isEqualTo(expected)
    }

    @Test
    fun `parse ftl quirks in all suites`() {
        val crashingAllSuitesMessage = """
            <?xml version='1.0' encoding='UTF-8' ?>
            <testsuites>
              <testsuite name="EarlGreyExampleSwiftTests" tests="3" failures="0" errors="0" skipped="0" time="10.0" hostname="localhost">
                <testcase name="a()" classname="a" time="1.0">
                <failure> java.net.ConnectException: Failed to connect to ... at &#8;&#8;&#8;(Coroutine boundary.&#8;(&#8;)</failure>
                </testcase>
                <testcase name="b()" classname="b" time="2.0"/>
                <testcase name="c()" classname="c" time="7.0"/>
              </testsuite>
            </testsuites>
        """.trimIndent()

        val expectedAllSuitesMessage = """
            <?xml version='1.0' encoding='UTF-8' ?>
            <testsuites>
              <testsuite name="EarlGreyExampleSwiftTests" tests="3" failures="0" errors="0" skipped="0" time="10.0" hostname="localhost">
                <testcase name="a()" classname="a" time="1.0">
                  <failure> java.net.ConnectException: Failed to connect to ... at (Coroutine boundary.()</failure>
                </testcase>
                <testcase name="b()" classname="b" time="2.0"/>
                <testcase name="c()" classname="c" time="7.0"/>
              </testsuite>
            </testsuites>
        """.trimIndent()
        val allSuitesXml = parseAllSuitesXml(crashingAllSuitesMessage.writeToTempFile()).toXmlString().trimIndent()
        Assert.assertEquals("All Suite Messages should be the same!", expectedAllSuitesMessage, allSuitesXml)
    }

    @Test
    fun `parse ftl quirks in on suite`() {
        val crashingOneSuiteMessage = """
            <?xml version='1.0' encoding='UTF-8' ?>
              <testsuite name="EarlGreyExampleSwiftTests" tests="3" failures="0" errors="0" skipped="0" time="10.0" hostname="localhost">
                <testcase name="a()" classname="a" time="1.0">
                <failure> java.net.ConnectException: Failed to connect to ... at &#8;&#8;&#8;(Coroutine boundary.&#8;(&#8;)</failure>
                </testcase>
                <testcase name="b()" classname="b" time="2.0"/>
                <testcase name="c()" classname="c" time="7.0"/>
              </testsuite>
        """.trimIndent()

        val expectedOneSuiteMessage = """
            <?xml version='1.0' encoding='UTF-8' ?>
            <testsuites>
              <testsuite name="EarlGreyExampleSwiftTests" tests="3" failures="0" errors="0" skipped="0" time="10.0" hostname="localhost">
                <testcase name="a()" classname="a" time="1.0">
                  <failure> java.net.ConnectException: Failed to connect to ... at (Coroutine boundary.()</failure>
                </testcase>
                <testcase name="b()" classname="b" time="2.0"/>
                <testcase name="c()" classname="c" time="7.0"/>
              </testsuite>
            </testsuites>
        """.trimIndent()
        val oneSuiteXml = parseOneSuiteXml(crashingOneSuiteMessage.writeToTempFile()).toXmlString().trimIndent()
        Assert.assertEquals("One Suite Messages should be the same!", expectedOneSuiteMessage, oneSuiteXml)
    }
}

private fun String.writeToTempFile(): File = File.createTempFile("temp", "test")
    .apply { writeText(this@writeToTempFile) }
