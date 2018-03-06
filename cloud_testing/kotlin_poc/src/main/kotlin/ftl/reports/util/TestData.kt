package ftl.reports.util

// <testsuite name="" tests="154" failures="1" errors="0" skipped="0"
//
//   <testcase name="displaysPageObjects" classname="com.instructure.teacher.ui.AnnouncementsListPageTest" time="0.0" />
//
//   <testcase name="displaysStudentItems" classname="com.instructure.teacher.ui.AssigneeListPageTest" time="0.001">
//     <failure> ... </failure>

data class TestSuite(
        val totalTests: Int,
        var failures: Int,
        var successes: Int,
        // Map testcase name to results. Single test may have many results when run more than once.
        val testCases: Map<String, TestResults>
)

data class TestResults(
        val successes: MutableList<TestSuccess> = mutableListOf(),
        val failures: MutableList<TestFailure> = mutableListOf()
)

// success shouldn't have stackTrace or screenshot
data class TestSuccess(
        val webLink: String
)

data class TestFailure(
        val stackTrace: String,
        val webLink: String
//            val screenshot: String? // TODO: screenshot support
)
