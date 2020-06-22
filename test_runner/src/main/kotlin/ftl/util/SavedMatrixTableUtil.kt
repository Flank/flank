package ftl.util

import ftl.json.SavedMatrix
import ftl.util.StepOutcome.failure
import ftl.util.StepOutcome.success

fun SavedMatrix.asPrintableTable(): String = listOf(this).asPrintableTable()

fun List<SavedMatrix>.asPrintableTable(): String = buildTable(
    TableColumn(OUTCOME_COLUMN_HEADER, map { it.outcome }, dataColor = map { getOutcomeColor(it.outcome) }),
    TableColumn(MATRIX_ID_COLUMN_HEADER, map { it.matrixId }),
    TableColumn(OUTCOME_DETAILS_COLUMN_HEADER, map { it.outcomeDetails })
)

private fun getOutcomeColor(outcome: String): SystemOutColor {
    // inconclusive is treated as a failure, flaky as a success
    return when (outcome) {
        failure -> SystemOutColor.RED
        success -> SystemOutColor.GREEN
        else -> SystemOutColor.DEFAULT
    }
}

private const val OUTCOME_COLUMN_HEADER = "OUTCOME"
private const val MATRIX_ID_COLUMN_HEADER = "MATRIX ID"
private const val OUTCOME_DETAILS_COLUMN_HEADER = "TEST DETAILS"
