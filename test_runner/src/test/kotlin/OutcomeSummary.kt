import ftl.gc.GcTestMatrix
import ftl.reports.outcome.createMatrixOutcomeSummary
import ftl.reports.outcome.fetchTestOutcomeContext
import kotlinx.coroutines.runBlocking

// TODO remove before merge
fun main() {
    runBlocking {
        GcTestMatrix.refresh(
            testMatrixId = "matrix-1aspi39ikq5hy",
            projectId = "flank-open-source"
        ).fetchTestOutcomeContext().createMatrixOutcomeSummary().also {
            println(it)
        }
    }
}
