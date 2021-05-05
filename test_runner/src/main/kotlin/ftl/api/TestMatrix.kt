package ftl.api

import ftl.adapter.TestMatrixCancel
import ftl.adapter.TestMatrixRefresh
import ftl.util.StepOutcome

val refreshTestMatrix: TestMatrix.Refresh get() = TestMatrixRefresh
val cancelTestMatrix: TestMatrix.Cancel get() = TestMatrixCancel
val update: TestMatrix.Summary.Fetch get() = TODO()

private const val fallbackAppName = "N/A"

object TestMatrix {

    data class Result(
        val runPath: String,
        val map: Map<String, Data>,
    )

    data class Data(
        val projectId: String = "",
        val matrixId: String = "",
        val state: String = "",
        val gcsPath: String = "",
        val webLink: String = "",
        val downloaded: Boolean = false,
        val billableMinutes: BillableMinutes = BillableMinutes(),
        val clientDetails: Map<String, String>? = null,
        val gcsPathWithoutRootBucket: String = "",
        val gcsRootBucket: String = "",
        val webLinkWithoutExecutionDetails: String? = "",
        val axes: List<Outcome> = emptyList(),
        val appFileName: String = fallbackAppName,
        val isCompleted: Boolean = false,
        val testExecutions: List<TestExecution> = emptyList(),
        val testTimeout: Long = 0,
        val isRoboTest: Boolean = false,
        val historyId: String = "",
        val executionId: String = "",
        val invalidMatrixDetails: String = ""
    ) {
        val outcome = axes.maxByOrNull { StepOutcome.order.indexOf(it.outcome) }?.outcome.orEmpty()
    }

    data class TestExecution(
        val id: String,
        val modelId: String,
        val deviceVersion: String,
        val shardIndex: Int?,
        val state: String,
        val errorMessage: String = "",
        val progress: List<String> = emptyList()
    )

    data class Outcome(
        val device: String = "",
        val outcome: String = "",
        val details: String = "",
        val suiteOverview: SuiteOverview = SuiteOverview()
    )

    data class SuiteOverview(
        val total: Int = 0,
        val errors: Int = 0,
        val failures: Int = 0,
        val flakes: Int = 0,
        val skipped: Int = 0,
        val elapsedTime: Double = 0.0,
        val overheadTime: Double = 0.0
    )

    data class BillableMinutes(
        val virtual: Long = 0,
        val physical: Long = 0
    )

    data class Summary(
        val billableMinutes: BillableMinutes,
        val axes: List<Outcome>,
    ) {
        data class Identity(
            val projectId: String,
            val historyId: String,
            val executionId: String,
        )

        interface Fetch : (Identity) -> Summary
    }

    data class Identity(
        val matrixId: String,
        val projectId: String,
        val historyId: String = "",
        val executionId: String = "",
    )

    interface Cancel : (Identity) -> Unit
    interface Refresh : (Identity) -> Data
}
