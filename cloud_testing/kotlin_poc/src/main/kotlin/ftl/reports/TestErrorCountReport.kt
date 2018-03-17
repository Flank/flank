package ftl.reports

import ftl.json.MatrixMap
import ftl.reports.util.IReport
import ftl.reports.util.TestSuite
import java.io.File

/**

List failure count for each test in TSV format. Only run on failures.

Example:

com.instructure.teacher.ui.SpeedGraderPageTest#displaySubmissionPickerDialog  31
com.instructure.teacher.ui.SpeedGraderCommentsPageTest#addsNewTextComment  5
com.instructure.teacher.ui.AssignmentSubmissionListPageTest#displaysAssignmentStatusLate  3
com.instructure.teacher.ui.QuizSubmissionListPageTest#filterLateSubmissions  2
com.instructure.teacher.ui.AssignmentSubmissionListPageTest#filterLateSubmissions  2
com.instructure.teacher.ui.EditQuizDetailsPageTest#editQuizTitle  1

 **/
object TestErrorCountReport : IReport {

    override fun run(matrices: MatrixMap, testSuite: TestSuite, print: Boolean) {
        if (testSuite.failures <= 0) return
        val report = File("${reportPath(matrices)}.csv")
        report.printWriter().use { writer ->
            testSuite.testCases.forEach { test ->
                val testName = test.key
                val failureCount = test.value.failures.size
                if (failureCount <= 0) return@forEach
                writer.println("$testName\t$failureCount")
            }
        }
    }
}
