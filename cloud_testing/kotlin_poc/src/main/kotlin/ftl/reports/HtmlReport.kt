package ftl.reports

import com.google.gson.Gson
import ftl.json.MatrixMap
import ftl.reports.util.IReport
import ftl.reports.util.TestSuite
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Outputs HTML report for Bitrise based on JUnit XML. Only run on failures.
 * */
object HtmlReport : IReport {
    data class Group(val key: String, val name: String, val startIndex: Int, val count: Int)
    data class Item(val key: String, val name: String)

    private val gson = Gson()


    fun reactJson(testSuite: TestSuite): Pair<String, String> {
        val groupList = mutableListOf<Group>()
        val itemList = mutableListOf<Item>()

        var groupId = 0
        var itemId = 0

        testSuite.testCases.forEach { testCase ->
            val testName = testCase.key
            val testResults = testCase.value

            val failures = testResults.failures
            if (failures.isEmpty()) return@forEach

            groupList.add(Group("group-$groupId",
                    testName,
                    groupId,
                    failures.size))
            groupId += 1

            failures.forEach { failure ->
                itemList.add(Item("item-$itemId",
                        failure.stackTrace.split("\n").firstOrNull() ?: ""
                ))
                itemId += 1
            }
        }

        val groupJson = gson.toJson(groupList)
        val itemJson = gson.toJson(itemList)
        return Pair(groupJson, itemJson)
    }

    override fun run(matrices: MatrixMap, testSuite: TestSuite, print: Boolean) {
        if (testSuite.failures <= 0) return
        val reactJson = reactJson(testSuite)
        val newGroupJson = reactJson.first
        val newItemsJson = reactJson.second

        var templateData = this::class.java.getResourceAsStream("/inline.html").bufferedReader().use { it.readText() }

        templateData = replaceRange(templateData, findGroupRange(templateData), newGroupJson)
        templateData = replaceRange(templateData, findItemRange(templateData), newItemsJson)

        val writePath = Paths.get(reportPath(matrices) + ".html")
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
