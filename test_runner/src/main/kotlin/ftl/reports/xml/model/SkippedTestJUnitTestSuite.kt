package ftl.reports.xml.model

import ftl.reports.api.utcDateFormat
import java.util.Date

fun getSkippedJUnitTestSuite(listOfJUnitTestCase: List<JUnitTestCase>) = JUnitTestSuite(
    name = "junit-ignored",
    tests = listOfJUnitTestCase.size.toString(),
    errors = "0",
    failures = "0",
    skipped = listOfJUnitTestCase.size.toString(),
    time = "0.0",
    timestamp = utcDateFormat.format(Date()),
    testcases = listOfJUnitTestCase.toMutableList()
)
