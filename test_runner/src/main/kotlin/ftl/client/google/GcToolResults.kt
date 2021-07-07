package ftl.client.google

import com.google.api.services.toolresults.ToolResults
import com.google.api.services.toolresults.ToolResultsRequest
import com.google.api.services.toolresults.ToolResultsRequestInitializer
import com.google.api.services.toolresults.model.Environment
import com.google.api.services.toolresults.model.Execution
import com.google.api.services.toolresults.model.History
import com.google.api.services.toolresults.model.ListEnvironmentsResponse
import com.google.api.services.toolresults.model.ListStepsResponse
import com.google.api.services.toolresults.model.ListTestCasesResponse
import com.google.api.services.toolresults.model.PerfMetricsSummary
import com.google.api.services.toolresults.model.Step
import com.google.api.services.toolresults.model.TestCase
import com.google.testing.model.TestExecution
import com.google.testing.model.ToolResultsExecution
import com.google.testing.model.ToolResultsHistory
import com.google.testing.model.ToolResultsStep
import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.config.FtlConstants.JSON_FACTORY
import ftl.config.FtlConstants.applicationName
import ftl.config.FtlConstants.httpTransport
import ftl.http.executeWithRetry
import ftl.run.exception.FTLProjectError
import ftl.run.exception.FailureToken
import ftl.run.exception.FlankGeneralError
import ftl.run.exception.PermissionDenied
import ftl.run.exception.ProjectNotFound
import ftl.util.applicationInfo

object GcToolResults {

    val service: ToolResults by lazy {
        val builder = ToolResults.Builder(httpTransport, JSON_FACTORY, httpCredential)

        if (FtlConstants.useMock) builder.rootUrl = FtlConstants.localhost

        builder.setApplicationName(applicationName)

        builder.setToolResultsRequestInitializer(object : ToolResultsRequestInitializer() {
            override fun initializeToolResultsRequest(request: ToolResultsRequest<*>?) {
                super.initializeToolResultsRequest(request)
                val (clientDetails, clientDetailsInfo) = applicationInfo
                request?.requestHeaders?.set(clientDetails, clientDetailsInfo)
            }
        })
        builder.build()
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

    fun getPerformanceMetric(
        toolResultsStep: ToolResultsStep
    ): PerfMetricsSummary = service
        .projects()
        .histories()
        .executions()
        .steps()
        .getPerfMetricsSummary(
            toolResultsStep.projectId,
            toolResultsStep.historyId,
            toolResultsStep.executionId,
            toolResultsStep.stepId
        )
        .executeWithRetry()

    // Lists Test Cases attached to a Step
    fun listTestCases(
        toolResultsStep: ToolResultsStep,
        pageToken: String? = null
    ): ListTestCasesResponse {
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
            )
            .setPageToken(pageToken)
            .executeWithRetry()
    }

    fun listAllTestCases(results: ToolResultsStep): List<TestCase> {
        var response = listTestCases(results)
        val testCases = response.testCases.orEmpty().toMutableList()
        while (response.nextPageToken != null) {
            response = listTestCases(results, response.nextPageToken)
            testCases += response.testCases ?: emptyList()
        }
        return testCases
    }

    fun getDefaultBucket(projectId: String, source: String? = null): String? = try {
        service.Projects().initializeSettings(projectId).executeWithRetry().defaultBucket
    } catch (ftlProjectError: FTLProjectError) {
        // flank needs to rewrap the exception with additional info about project
        when (ftlProjectError) {
            is PermissionDenied -> throw FlankGeneralError(
                permissionDeniedErrorMessage(
                    projectId,
                    source,
                    ftlProjectError.message
                )
            )
            is ProjectNotFound -> throw FlankGeneralError(
                projectNotFoundErrorMessage(
                    projectId,
                    ftlProjectError.message
                )
            )
            is FailureToken -> UserAuth.throwAuthenticationError()
        }
    }

    fun listAllEnvironments(results: ToolResultsExecution): List<Environment> {
        var response = listEnvironments(results)
        val environments = response.environments.toMutableList()
        while (response.nextPageToken != null) {
            response = listEnvironments(results, response.nextPageToken)
            environments += response.environments ?: emptyList()
        }
        return environments
    }

    private fun listEnvironments(
        results: ToolResultsExecution,
        pageToken: String? = null
    ): ListEnvironmentsResponse = service
        .projects()
        .histories()
        .executions()
        .environments()
        .list(
            results.projectId,
            results.historyId,
            results.executionId
        )
        .setPageToken(pageToken)
        .setPageSize(100)
        .executeWithRetry()

    fun listAllSteps(results: ToolResultsExecution): MutableList<Step> {
        var response = listSteps(results)
        val steps = response.steps.toMutableList()
        while (response.nextPageToken != null) {
            response = listSteps(results, response.nextPageToken)
            steps += response.steps ?: emptyList()
        }
        return steps
    }

    private fun listSteps(
        results: ToolResultsExecution,
        pageToken: String? = null
    ): ListStepsResponse = service
        .projects()
        .histories()
        .executions()
        .steps()
        .list(
            results.projectId,
            results.historyId,
            results.executionId
        )
        .setPageToken(pageToken)
        .setPageSize(100)
        .executeWithRetry()
}

private val permissionDeniedErrorMessage = { projectId: String, projectIdSource: String?, message: String? ->
    """Flank encountered a 403 error when running on project $projectId${projectIdSource?.let { " (from $it)" } ?: ""}. Please verify this credential is authorized for the project and has the required permissions.
Consider authentication with a Service Account https://github.com/Flank/flank#authenticate-with-a-service-account
or with a Google account https://github.com/Flank/flank#authenticate-with-a-google-account

$message
    """.trimIndent()
}

private val projectNotFoundErrorMessage = { projectId: String, message: String? ->
    """Flank was unable to find project $projectId. Please verify the project id.

$message
    """.trimIndent()
}
