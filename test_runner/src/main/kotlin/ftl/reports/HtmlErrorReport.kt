package ftl.reports

import com.google.gson.Gson
import ftl.args.IArgs
import ftl.json.MatrixMap
import ftl.reports.util.IReport
import ftl.reports.util.ReportManager
import ftl.reports.xml.model.JUnitTestCase
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.model.JUnitTestSuite
import ftl.util.readTextResource
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Outputs HTML report for Bitrise based on JUnit XML. Only run on failures.
 * */
object HtmlErrorReport : IReport {

    override val extension = ".html"

    internal data class Group(val label: String, val items: List<Item>)
    internal data class Item(val label: String, val url: String)

    override fun run(
        matrices: MatrixMap,
        result: JUnitTestResult?,
        printToStdout: Boolean,
        args: IArgs
    ) {
        val suites = result?.testsuites?.process()?.takeIf { it.isNotEmpty() } ?: return
        Paths.get(reportPath(matrices, args)).let {
            val htmlReport = suites.createHtmlReport()
            Files.write(it, htmlReport.toByteArray())
            ReportManager.uploadReportResult(htmlReport, args, it.fileName.toString())
        }
    }
}

internal fun List<JUnitTestSuite>.process(): List<HtmlErrorReport.Group> = this
    .getFailures()
    .groupByLabel()
    .createGroups()

private fun List<JUnitTestSuite>.getFailures(): List<Pair<String, List<JUnitTestCase>>> =
    mapNotNull { suite ->
        suite.testcases?.let { testCases ->
            suite.name to testCases.filter { it.failed() }
        }
    }

private fun List<Pair<String, List<JUnitTestCase>>>.groupByLabel(): Map<String, List<JUnitTestCase>> = this
    .map { (suiteName, testCases) ->
        testCases.map { testCase ->
            "$suiteName ${testCase.classname}#${testCase.name}".trim() to testCase
        }
    }
    .flatten()
    .groupBy({ (label: String, _) -> label }) { (_, useCase) -> useCase }

private fun Map<String, List<JUnitTestCase>>.createGroups(): List<HtmlErrorReport.Group> =
    map { (label, testCases: List<JUnitTestCase>) ->
        HtmlErrorReport.Group(
            label = label,
            items = testCases.createItems()
        )
    }

private fun List<JUnitTestCase>.createItems(): List<HtmlErrorReport.Item> = map { testCase ->
    HtmlErrorReport.Item(
        label = testCase.stackTrace().split("\n").firstOrNull() ?: "",
        url = testCase.webLink ?: ""
    )
}

private fun List<HtmlErrorReport.Group>.createHtmlReport(): String =
    readTextResource("inline.html").replace(
        oldValue = "\"INJECT-DATA-HERE\"",
        newValue = "`${Gson().toJson(this)}`"
    )
