package ftl.gc

import com.google.api.client.googleapis.testing.auth.oauth2.MockGoogleCredential
import com.google.api.services.testing.model.ToolResultsStep
import com.google.api.services.toolresults.ToolResults
import com.google.api.services.toolresults.model.Step
import ftl.config.FtlConstants
import ftl.config.FtlConstants.JSON_FACTORY
import ftl.config.FtlConstants.applicationName
import ftl.config.FtlConstants.credential
import ftl.config.FtlConstants.httpTransport

object GcToolResults {

    val service: ToolResults by lazy {
        val builder = ToolResults.Builder(httpTransport, JSON_FACTORY, credential)

        if (FtlConstants.useMock) builder.rootUrl = FtlConstants.localhost

        builder.setApplicationName(applicationName)
                .build()
    }

    fun getResults(toolResultsStep: ToolResultsStep): Step {
        return GcToolResults.service
                .projects()
                .histories()
                .executions()
                .steps()
                .get(
                        toolResultsStep.projectId,
                        toolResultsStep.historyId,
                        toolResultsStep.executionId,
                        toolResultsStep.stepId)
                .execute()
    }
}
