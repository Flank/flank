package ftl.reports.api.data

import com.google.api.services.testing.model.TestExecution
import com.google.api.services.toolresults.model.ListTestCasesResponse
import com.google.api.services.toolresults.model.Step

data class TestExecutionData(
    val testExecution: TestExecution,
    val response: ListTestCasesResponse,
    val step: Step
)
