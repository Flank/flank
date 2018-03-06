package ftl.reports

import com.google.gson.Gson
import ftl.json.MatrixMap
import ftl.reports.util.IReport
import ftl.reports.util.TestSuite
import java.nio.file.Files
import java.nio.file.Paths

/** Outputs HTML report for Bitrise based on JUnit XML */
object HtmlReport : IReport {
    data class Group(val key: String, val name: String, val startIndex: Int, val count: Int)
    data class Item(val key: String, val name: String)

    private val gson = Gson()

    // todo: create merged JUnit XML file. then use that to feed into the reports
    // instead of parsing on disk xml each time
    private fun newGroupJson(): String {
        // startIndex / count controls what data items belong to the group.
        // key must be unique
        val dataGroup = listOf(
                Group("group-0",
                        "AssigneeListPageTest#displaysStudentItems",
                        0,
                        1),
                Group("group-1",
                        "SpeedGraderCommentsPageTest#displaysAuthorName",
                        1,
                        1)
        )
        return gson.toJson(dataGroup)
    }

    private fun newItemsJson(): String {
        val dataItems = listOf(
                Item("item-0",
                        "android.support.test.espresso.NoMatchingViewException: No views in hierarchy found matching: (with text: is \"Everyone\" and has sibling: with id: com.instructure.teacher:id/assigneeSubtitleView)"),
                Item("item-1",
                        "java.lang.RuntimeException: Waited for the root of the view hierarchy to have window focus and not request layout for 10 seconds. If you specified a non default root matcher, it may be picking a root that never takes focus.")
        )

        return gson.toJson(dataItems)
    }

    override fun run(matrices: MatrixMap, testSuite: TestSuite) {
        // todo: embed inline.html as a resource in the jar so it works on the CLI
        val templatePath = Paths.get("../junit_html_report/bitrise/inline.html").toAbsolutePath().normalize()
        var templateData = String(Files.readAllBytes(templatePath))

        templateData = replaceRange(templateData, findGroupRange(templateData), newGroupJson())
        templateData = replaceRange(templateData, findItemRange(templateData), newItemsJson())

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
