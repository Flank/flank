import com.google.api.services.testing.model.ToolResultsStep
import com.google.api.services.toolresults.ToolResults
import com.google.api.services.toolresults.model.Step
import com.google.api.services.toolresults.model.TestCase
import com.google.api.services.toolresults.model.TestSuiteOverview
import com.google.gson.GsonBuilder
import ftl.gc.GcToolResults
import ftl.reports.xml.model.JUnitTestCase
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.model.JUnitTestSuite
import ftl.reports.xml.xmlToString
import java.nio.file.Files
import java.nio.file.Paths

object Tmp {

    fun Step.testSuiteName(): String {
        val map = mutableMapOf<String, String>()
        this.dimensionValue.map { map[it.key] = it.value }
        return listOf(map["Model"], map["Version"], map["Locale"], map["Orientation"]).joinToString("-")
    }

    fun TestSuiteOverview.tests(): String {
        return (this.totalCount ?: 0).toString()
    }

    fun TestSuiteOverview.failures(): String {
        return (this.failureCount ?: 0).toString()
    }

    fun TestSuiteOverview.errors(): String {
        return (this.errorCount ?: 0).toString()
    }

    fun TestSuiteOverview.skipped(): String {
        return (this.skippedCount ?: 0).toString()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val gson = GsonBuilder().setPrettyPrinting().create()!!
//        Files.write(Paths.get("/tmp/tool_results_step${it.matrixId}.json"), gson.toJson(it.toolResultsStep).toByteArray())

        val content = String(Files.readAllBytes(Paths.get("../tool_results_stepmatrix-3l3x3k8qzjmgg.json")))
        val toolResult = gson.fromJson<ToolResultsStep>(content, ToolResultsStep::class.java)
        val tests = GcToolResults.listTestCases(toolResult)
        val result = GcToolResults.getResults(toolResult)

        // todo: handle multiple overviews
        val overview = result.testExecutionStep.testSuiteOverviews.first()

        val testCases = mutableListOf<JUnitTestCase>()
        tests.testCases.forEach {
            testCases.add(JUnitTestCase(
                name =  it.testCaseReference.name,
                classname = it.testCaseReference.className,
                time = (it.endTime.nanos - it.startTime.nanos).toString(),
                failures = null,
                errors = null,
                skipped = "absent"
            ))
        }

        val testSuite = JUnitTestSuite(
            name = result.testSuiteName(),
            tests = overview.tests(),
            failures = overview.failures(),
            errors = overview.errors(),
            skipped = overview.skipped(),
            time = "",
            timestamp = "",
            hostname = "localhost",
            testcases = testCases)

        val xmlTestResult = JUnitTestResult(mutableListOf(testSuite))
        println(xmlTestResult.xmlToString())

        // result.testExecutionStep.testTiming.testProcessDuration // 5 seconds
        // result.runDuration // 190 seconds

        /*

        Time: 0.839 -- from instrumentation.results

Real JUnit XML:
<?xml version='1.0' encoding='UTF-8' ?>
<testsuite name="" tests="1" failures="0" errors="0" skipped="0" time="0.839" timestamp="2019-05-17T19:02:03" hostname="localhost">
  <properties />
  <testcase name="testPasses" classname="com.example.app.ExampleUiTest" time="0.026" />
</testsuite>

XML from API:
<?xml version='1.0' encoding='UTF-8' ?>
<testsuites>
  <testsuite name="NexusLowRes-28-en-portrait" tests="1" failures="0" errors="0" skipped="0" time="" timestamp="" hostname="localhost">
    <testcase name="testPasses" classname="com.example.app.ExampleUiTest" time="18000000"/>
  </testsuite>
</testsuites>


// /tmp/tool_results_stepmatrix-3l3x3k8qzjmgg.json
{
  "executionId": "6759853542268185970",
  "historyId": "bh.58317d9cd7ab9ba2",
  "projectId": "delta-essence-114723",
  "stepId": "bs.e00d74d95caed746"
}
        */
    }
}
