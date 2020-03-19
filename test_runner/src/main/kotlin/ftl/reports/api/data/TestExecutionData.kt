package ftl.reports.api.data

import com.google.api.services.testing.model.TestExecution
import com.google.api.services.testing.model.ToolResultsStep
import com.google.api.services.toolresults.model.ListTestCasesResponse
import com.google.api.services.toolresults.model.Step
import com.google.api.services.toolresults.model.Timestamp

data class TestExecutionData(
    val testExecution: TestExecution,
    val toolResultsStep: ToolResultsStep,
    val response: ListTestCasesResponse,
    val step: Step,
    val timestamp: Timestamp
)
