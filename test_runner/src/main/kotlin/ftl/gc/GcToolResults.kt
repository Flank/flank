package ftl.gc

import com.google.api.services.testing.model.TestExecution
import com.google.api.services.testing.model.ToolResultsHistory
import com.google.api.services.testing.model.ToolResultsStep
import com.google.api.services.toolresults.ToolResults
import com.google.api.services.toolresults.model.Execution
import com.google.api.services.toolresults.model.History
import com.google.api.services.toolresults.model.ListTestCasesResponse
import com.google.api.services.toolresults.model.Step
import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.config.FtlConstants.JSON_FACTORY
import ftl.config.FtlConstants.applicationName
import ftl.config.FtlConstants.httpCredential
import ftl.config.FtlConstants.httpTransport
import ftl.http.executeWithRetry
import ftl.util.FlankCommonException
import ftl.util.FTLProjectError
import ftl.util.PermissionDenied
import ftl.util.ProjectNotFound

object GcToolResults {

    val service: ToolResults by lazy {
        val builder = ToolResults.Builder(httpTransport, JSON_FACTORY, httpCredential)

        if (FtlConstants.useMock) builder.rootUrl = FtlConstants.localhost

        builder.setApplicationName(applicationName)
            .build()
    }

    // https://github.com/bootstraponline/gcloud_cli/blob/0752e88b155a417a18d244c242b4ab3fb9aa1c1f/google-cloud-sdk/lib/googlecloudsdk/api_lib/firebase/test/history_picker.py#L45
    private fun listHistoriesByName(args: IArgs): List<History> {
        val result = service
            .projects()
            .histories()
            .list(args.project)
            .setFilterByName(args.resultsHistoryName)
            .executeWithRetry()
        return result?.histories ?: emptyList()
    }

    private fun createHistory(args: IArgs): History {
        val history = History()
            .setName(args.resultsHistoryName)
            .setDisplayName(args.resultsHistoryName)
        return service
            .projects()
            .histories()
            .create(args.project, history)
            .execute()
    }

    fun createToolResultsHistory(args: IArgs): ToolResultsHistory {
        return ToolResultsHistory()
            .setHistoryId(createHistoryId(args))
            .setProjectId(args.project)
    }

    private fun createHistoryId(args: IArgs): String? {
        if (args.resultsHistoryName == null) return null
        val histories = listHistoriesByName(args)
        if (histories.isNotEmpty()) return histories.first().historyId

        return createHistory(args).historyId
    }

    // FetchMatrixRollupOutcome - https://github.com/bootstraponline/gcloud_cli/blob/137d864acd5928baf25434cf59b0225c4d1f9319/google-cloud-sdk/lib/googlecloudsdk/api_lib/firebase/test/results_summary.py#L122
    // Get the rolled-up outcome for a test execution from the Tool Results service.
    // Prefer execution rollUp outcome over individual step outcome
    // https://github.com/bootstraponline/gcloud_cli/blob/137d864acd5928baf25434cf59b0225c4d1f9319/google-cloud-sdk/lib/googlecloudsdk/third_party/apis/toolresults_v1beta3.json#L992
    fun getExecutionResult(testExecution: TestExecution): Execution {
        val toolResultsStep = testExecution.toolResultsStep

        // Toolresults.Projects.Histories.Executions.GetRequest
        return service
            .projects()
            .histories()
            .executions()
            .get(
                toolResultsStep.projectId,
                toolResultsStep.historyId,
                toolResultsStep.executionId
            )
            .executeWithRetry()
    }

    fun getStepResult(toolResultsStep: ToolResultsStep): Step {
        return service
            .projects()
            .histories()
            .executions()
            .steps()
            .get(
                toolResultsStep.projectId,
                toolResultsStep.historyId,
                toolResultsStep.executionId,
                toolResultsStep.stepId
            )
            .executeWithRetry()
    }

    // Lists Test Cases attached to a Step
    fun listTestCases(toolResultsStep: ToolResultsStep): ListTestCasesResponse {
        return service
            .projects()
            .histories()
            .executions()
            .steps()
            .testCases()
            .list(
                toolResultsStep.projectId,
                toolResultsStep.historyId,
                toolResultsStep.executionId,
                toolResultsStep.stepId
            ).executeWithRetry()
    }

    fun getDefaultBucket(projectId: String): String? = try {
        service.Projects().initializeSettings(projectId).executeWithRetry().defaultBucket
    } catch (ftlProjectError: FTLProjectError) {
        // flank needs to rewrap the exception with additional info about project
        when (ftlProjectError) {
            is PermissionDenied -> throw FlankCommonException(permissionDeniedErrorMessage(projectId, ftlProjectError.message))
            is ProjectNotFound -> throw FlankCommonException(projectNotFoundErrorMessage(projectId, ftlProjectError.message))
        }
    }
}

private val permissionDeniedErrorMessage = { projectId: String, message: String? ->
    """Flank encountered a 403 error when running on project $projectId. Please verify this credential is authorized for the project and has the required permissions.
Consider authentication with a Service Account https://github.com/Flank/flank#authenticate-with-a-service-account
or with a Google account https://github.com/Flank/flank#authenticate-with-a-google-account

$message""".trimIndent()
}

private val projectNotFoundErrorMessage = { projectId: String, message: String? ->
    """Flank was unable to find project $projectId. Please verify the project id.

$message""".trimIndent()
}
