package ftl.client.junit

import com.google.api.services.toolresults.model.Step
import com.google.api.services.toolresults.model.TestCase
import com.google.api.services.toolresults.model.Timestamp
import com.google.testing.model.TestExecution

data class TestExecutionData(
    val testExecution: TestExecution,
    val testCases: List<TestCase>,
    val step: Step,
    val timestamp: Timestamp
)
