package ftl.reports

import com.google.gson.Gson
import ftl.json.MatrixMap
import ftl.reports.util.IReport
import ftl.reports.xml.model.JUnitTestCase
import ftl.reports.xml.model.JUnitTestResult
import ftl.util.Utils.readTextResource
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Outputs HTML report for Bitrise based on JUnit XML. Only run on failures.
 * */
object HtmlErrorReport : IReport {
    override val extension = ".html"

    data class Group(val key: String, val name: String, val startIndex: Int, val count: Int)
    data class Item(val key: String, val name: String, val link: String)

    private val gson = Gson()

    fun groupItemList(testSuites: JUnitTestResult): Pair<List<HtmlErrorReport.Group>, List<HtmlErrorReport.Item>>? {
        val groupList = mutableListOf<Group>()
        val itemList = mutableListOf<Item>()

        var groupId = 0
        var itemId = 0

        val failures = mutableMapOf<String, MutableList<JUnitTestCase>>()
        testSuites.testsuites?.forEach { suite ->
            suite.testcases?.forEach testCase@{ testCase ->
                if (!testCase.failed()) return@testCase
                val key = "${suite.name} ${testCase.classname}#${testCase.name}".trim()

                if (failures[key] == null) {
                    failures[key] = mutableListOf(testCase)
                } else {
                    failures[key]?.add(testCase)
                }
            }
        }

        if (failures.isEmpty()) return null

        failures.forEach { testName, testResults ->
            groupList.add(
                Group(
                    "group-$groupId",
                    testName,
                    groupId,
                    testResults.size
                )
            )
            groupId += 1

            testResults.forEach { failure ->
                itemList.add(
                    Item(
                        "item-$itemId",
                        failure.stackTrace().split("\n").firstOrNull() ?: "",
                        failure.webLink ?: ""
                    )
                )
                itemId += 1
            }
        }

        return groupList to itemList
    }

    private fun reactJson(testSuites: JUnitTestResult): Pair<String, String>? {
        val groupItemList = groupItemList(testSuites) ?: return null

        val groupJson = gson.toJson(groupItemList.first)
        val itemJson = gson.toJson(groupItemList.second)
        return groupJson to itemJson
    }

    override fun run(matrices: MatrixMap, testSuite: JUnitTestResult?, printToStdout: Boolean) {
        if (testSuite == null) return
        val reactJson = reactJson(testSuite) ?: return
        val newGroupJson = reactJson.first
        val newItemsJson = reactJson.second

        var templateData = readTextResource("inline.html")

        templateData = replaceRange(templateData, findGroupRange(templateData), newGroupJson)
        templateData = replaceRange(templateData, findItemRange(templateData), newItemsJson)

        val writePath = Paths.get(reportPath(matrices))
        Files.write(writePath, templateData.toByteArray())
    }

    private fun replaceRange(data: String, deleteRange: IntRange, insert: String): String {
        val before = data.substring(0, deleteRange.start)
        val after = data.substring(deleteRange.endInclusive)

        return before + insert + after
    }

    private fun findItemRange(data: String): IntRange {
        return findJsonBounds(data, startPattern = "=[{key:\"item-0\",")
    }

    private fun findGroupRange(data: String): IntRange {
        return findJsonBounds(data, startPattern = "=[{key:\"group-0\",")
    }

    // return start/stop index for the matched JSON object
    private fun findJsonBounds(data: String, startPattern: String): IntRange {
        val startIndex = data.indexOf(startPattern) + 1
        if (startIndex == -1) throw RuntimeException("failed to find $startPattern")

        val endIndex = data.indexOf(']', startIndex) + 1

        return IntRange(startIndex, endIndex)
    }
}
