package ftl.reports.xml

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.nio.file.Paths

class JUnitXmlTest {
    private val xmlRoot = "./src/test/kotlin/ftl/fixtures/ftl_junit_xml"
    private val androidPassXml = Paths.get("$xmlRoot/android_pass.xml")
    private val androidFailXml = Paths.get("$xmlRoot/android_fail.xml")
    private val iosPassXml = Paths.get("$xmlRoot/ios_pass.xml")
    private val iosFailXml = Paths.get("$xmlRoot/ios_fail.xml")

    @Test
    fun merge_android() {
        val mergedXml = parseAndroidXml(androidPassXml).merge(parseAndroidXml(androidFailXml))
        val merged = mergedXml.xmlToString()

        val testSuite = mergedXml.testsuites?.first() ?: throw java.lang.RuntimeException("no test suite")
        assertThat(testSuite.name).isEqualTo("")
        assertThat(testSuite.tests).isEqualTo("3")
        assertThat(testSuite.failures).isEqualTo("1")
        assertThat(testSuite.errors).isEqualTo("0")
        assertThat(testSuite.skipped).isEqualTo("0")
        assertThat(testSuite.time).isEqualTo((3.87 + 2.278).toString())
        assertThat(testSuite.timestamp).isEqualTo("2018-09-14T20:45:55")
        assertThat(testSuite.hostname).isEqualTo("localhost")

        val expected = """
<?xml version='1.0' encoding='UTF-8' ?>
<testsuites>
  <testsuite name="" tests="3" failures="1" errors="0" skipped="0" time="6.148" timestamp="2018-09-14T20:45:55" hostname="localhost">
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
    fun merge_ios() {
        val merged = parseIosXml(iosPassXml).merge(parseIosXml(iosFailXml)).xmlToString()
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
    fun junitXmlToString_androidPassXml() {
        val parsed = parseAndroidXml(androidPassXml).xmlToString()
        val expected = """
<?xml version='1.0' encoding='UTF-8' ?>
<testsuites>
  <testsuite name="" tests="1" failures="0" errors="0" skipped="0" time="2.278" timestamp="2018-09-14T20:45:55" hostname="localhost">
    <testcase name="testPasses" classname="com.example.app.ExampleUiTest" time="0.328"/>
  </testsuite>
</testsuites>

        """.trimIndent()

        assertThat(parsed).isEqualTo(expected)
    }

    @Test
    fun junitXmlToString_androidFailXml() {
        val parsed = parseAndroidXml(androidFailXml).xmlToString()
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
    fun junitXmlToString_iosPassXml() {
        val parsed = parseIosXml(iosPassXml).xmlToString()
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
    fun junitXmlToString_iosFailXml() {
        val parsed = parseIosXml(iosFailXml).xmlToString()
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
    fun parse_androidPassXml() {
        val testSuite = parseAndroidXml(androidPassXml)
            .testsuites?.first() ?: throw RuntimeException("Missing test suite")

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
    fun parse_androidFailXml() {
        val testSuite = parseAndroidXml(androidFailXml)
            .testsuites?.first() ?: throw RuntimeException("Missing test suite")

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
    fun parse_iosPassXml() {
        val testSuites = parseIosXml(iosPassXml)
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
    fun parse_iosFailXml() {
        val testSuites = parseIosXml(iosFailXml)
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
}
