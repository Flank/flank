package ftl

import com.google.testing.model.ToolResultsStep

data class ToolResultsStepGcsPath(
        val toolResults: ToolResultsStep,
        val gcsPath: String
)
