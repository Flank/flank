package ftl.reports

import com.google.gson.Gson
import ftl.api.JUnitTest
import ftl.args.IArgs
import ftl.domain.junit.failed
import ftl.json.MatrixMap
import ftl.reports.util.IReport
import ftl.reports.util.ReportManager
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
        result: JUnitTest.Result?,
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

internal fun List<JUnitTest.Suite>.process(): List<HtmlErrorReport.Group> = getFailures()
    .groupByLabel()
    .createGroups()

private fun List<JUnitTest.Suite>.getFailures(): List<Pair<String, List<JUnitTest.Case>>> =
    mapNotNull { suite ->
        suite.testcases?.let { testCases ->
            suite.name to testCases.filter { it.failed() }
        }
    }

private fun List<Pair<String, List<JUnitTest.Case>>>.groupByLabel(): Map<String, List<JUnitTest.Case>> =
    map { (suiteName, testCases) ->
        testCases.map { testCase ->
            "$suiteName ${testCase.classname}#${testCase.name}".trim() to testCase
        }
    }
        .flatten()
        .groupBy({ (label: String, _) -> label }) { (_, useCase) -> useCase }

private fun Map<String, List<JUnitTest.Case>>.createGroups(): List<HtmlErrorReport.Group> =
    map { (label, testCases: List<JUnitTest.Case>) ->
        HtmlErrorReport.Group(
            label = label,
            items = testCases.createItems()
        )
    }

private fun List<JUnitTest.Case>.createItems(): List<HtmlErrorReport.Item> = map { testCase ->
    HtmlErrorReport.Item(
        label = testCase.stackTrace().split("\n").firstOrNull() ?: "",
        url = testCase.webLink ?: ""
    )
}

private fun JUnitTest.Case.stackTrace(): String {
    return failures?.joinToString() + errors?.joinToString()
}

private fun List<HtmlErrorReport.Group>.createHtmlReport(): String =
    readTextResource("inline.html").replace(
        oldValue = "\"INJECT-DATA-HERE\"",
        newValue = "`${Gson().toJson(this)}`"
    )
