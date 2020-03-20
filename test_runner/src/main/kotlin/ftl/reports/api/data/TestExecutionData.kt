package ftl.reports.api.data

import com.google.api.services.testing.model.TestExecution
import com.google.api.services.toolresults.model.Step
import com.google.api.services.toolresults.model.TestCase
import com.google.api.services.toolresults.model.Timestamp

data class TestExecutionData(
    val testExecution: TestExecution,
    val testCases: List<TestCase>,
    val step: Step,
    val timestamp: Timestamp
)
