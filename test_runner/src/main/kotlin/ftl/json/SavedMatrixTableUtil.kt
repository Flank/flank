package ftl.json

import ftl.api.TestMatrix
import ftl.util.Align
import ftl.util.StepOutcome.failure
import ftl.util.StepOutcome.flaky
import ftl.util.StepOutcome.success
import ftl.util.SystemOutColor
import ftl.util.TableColumn
import ftl.util.buildTable

fun TestMatrix.Data.asPrintableTable(): String = listOf(this).asPrintableTable()

fun List<TestMatrix.Data>.asPrintableTable(): String = buildTable(
    TableColumn(
        header = OUTCOME_COLUMN_HEADER,
        data = flatMapTestAxis { outcome },
        dataColor = flatMapTestAxis { outcomeColor },
        align = Align.LEFT
    ),
    TableColumn(
        header = MATRIX_ID_COLUMN_HEADER,
        data = flatMapTestAxis { matrix -> matrix.matrixId },
        align = Align.LEFT
    ),
    TableColumn(
        header = APP_NAME_COLUMN_HEADER,
        data = flatMapTestAxis { matrix -> matrix.appFileName },
        align = Align.LEFT
    ),
    TableColumn(
        header = TEST_APP_NAME_COLUMN_HEADER,
        data = flatMapTestAxis { matrix -> matrix.testFileName },
        align = Align.LEFT
    ),
    TableColumn(
        header = TEST_AXIS_VALUE_HEADER,
        data = flatMapTestAxis { device },
        align = Align.LEFT
    ),
    TableColumn(
        header = OUTCOME_DETAILS_COLUMN_HEADER,
        data = flatMapTestAxis { details },
        align = Align.LEFT
    )
)

private fun <T> List<TestMatrix.Data>.flatMapTestAxis(transform: TestMatrix.Outcome.(TestMatrix.Data) -> T) =
    flatMap { matrix -> matrix.axes.map { axis -> axis.transform(matrix) } }

private val TestMatrix.Outcome.outcomeColor
    get() = when (outcome) {
        failure -> SystemOutColor.RED
        success -> SystemOutColor.GREEN
        flaky -> SystemOutColor.BLUE
        else -> SystemOutColor.DEFAULT
    }

private const val OUTCOME_COLUMN_HEADER = "OUTCOME"
private const val MATRIX_ID_COLUMN_HEADER = "MATRIX ID"
private const val APP_NAME_COLUMN_HEADER = "APP NAME"
private const val TEST_APP_NAME_COLUMN_HEADER = "TEST FILE NAME"
private const val TEST_AXIS_VALUE_HEADER = "TEST AXIS VALUE"
private const val OUTCOME_DETAILS_COLUMN_HEADER = "TEST DETAILS"
