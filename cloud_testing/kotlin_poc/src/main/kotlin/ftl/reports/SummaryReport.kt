package ftl.reports

import ftl.json.MatrixMap
import ftl.reports.util.IReport
import ftl.reports.util.TestSuite
import java.io.File

/**

List failure count for each test in CSV format.

Example:

com.instructure.teacher.ui.SpeedGraderPageTest#displaySubmissionPickerDialog,31
com.instructure.teacher.ui.SpeedGraderCommentsPageTest#addsNewTextComment,5
com.instructure.teacher.ui.AssignmentSubmissionListPageTest#displaysAssignmentStatusLate,3
com.instructure.teacher.ui.QuizSubmissionListPageTest#filterLateSubmissions,2
com.instructure.teacher.ui.AssignmentSubmissionListPageTest#filterLateSubmissions,2
com.instructure.teacher.ui.EditQuizDetailsPageTest#editQuizTitle,1

 **/
object SummaryReport : IReport {

    override fun run(matrices: MatrixMap, testSuite: TestSuite) {
        File("${reportPath(matrices)}.csv").printWriter().use { writer ->
            testSuite.testCases.forEach { test ->
                val testName = test.key
                val failureCount = test.value.failures.size
                writer.println("$testName,$failureCount")
            }
        }
    }
}
