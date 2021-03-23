package ftl.json

import ftl.reports.outcome.TestOutcome
import ftl.util.StepOutcome.failure
import ftl.util.StepOutcome.flaky
import ftl.util.StepOutcome.success
import ftl.util.SystemOutColor
import ftl.util.TableColumn
import ftl.util.buildTable

fun SavedMatrix.asPrintableTable(): String = listOf(this).asPrintableTable()

fun List<SavedMatrix>.asPrintableTable(): String = buildTable(
    TableColumn(
        header = OUTCOME_COLUMN_HEADER,
        data = flatMapTestAxis { outcome },
        dataColor = flatMapTestAxis { outcomeColor }
    ),
    TableColumn(
        header = MATRIX_ID_COLUMN_HEADER,
        data = flatMapTestAxis { matrix -> matrix.matrixId }
    ),
    TableColumn(
        header = APP_NAME_COLUMN_HEADER,
        data = flatMapTestAxis { matrix -> matrix.appFileName }
    ),
    TableColumn(
        header = TEST_AXIS_VALUE_HEADER,
        data = flatMapTestAxis { device }
    ),
    TableColumn(
        header = OUTCOME_DETAILS_COLUMN_HEADER,
        data = flatMapTestAxis { details }
    )
)

private fun <T> List<SavedMatrix>.flatMapTestAxis(transform: TestOutcome.(SavedMatrix) -> T) =
    flatMap { matrix -> matrix.testAxises.map { axis -> axis.transform(matrix) } }

private val TestOutcome.outcomeColor
    get() = when (outcome) {
        failure -> SystemOutColor.RED
        success -> SystemOutColor.GREEN
        flaky -> SystemOutColor.BLUE
        else -> SystemOutColor.DEFAULT
    }

private const val OUTCOME_COLUMN_HEADER = "OUTCOME"
private const val MATRIX_ID_COLUMN_HEADER = "MATRIX ID"
private const val APP_NAME_COLUMN_HEADER = "APP NAME"
private const val TEST_AXIS_VALUE_HEADER = "TEST AXIS VALUE"
private const val OUTCOME_DETAILS_COLUMN_HEADER = "TEST DETAILS"
