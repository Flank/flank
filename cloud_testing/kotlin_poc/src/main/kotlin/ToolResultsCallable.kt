import com.google.testing.model.ToolResultsStep
import java.util.concurrent.Callable

import Utils.fatalError

internal class ToolResultsCallable(private val toolResultsValue: MutableList<ToolResultsValue>,
                                   private val toolResultsStep: ToolResultsStep) : Callable<Any> {

    private fun run() {
        try {
            val step = GcToolResults.get()!!
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

            toolResultsValue.add(ToolResultsValue(step, toolResultsStep))
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
