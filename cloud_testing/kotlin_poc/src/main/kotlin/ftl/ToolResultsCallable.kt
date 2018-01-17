package ftl

import ftl.Utils.fatalError
import java.util.concurrent.Callable

internal class ToolResultsCallable(private val toolResultsValue: MutableList<ToolResultsValue>,
                                   private val toolResultsStepGcsPath: ToolResultsStepGcsPath) : Callable<Any> {

    private fun run() {
        try {
            val toolResultsStep = toolResultsStepGcsPath.toolResults
            val getStepResults = GcToolResults.get()!!
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

            toolResultsValue.add(ToolResultsValue(getStepResults, toolResultsStepGcsPath))
        } catch (e: Exception) {
            fatalError(e)
        }

    }

    @Throws(Exception::class)
    override fun call(): Any? {
        run()
        return null
    }
}
