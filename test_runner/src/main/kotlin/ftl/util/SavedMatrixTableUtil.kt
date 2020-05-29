package ftl.util

import ftl.json.SavedMatrix

fun SavedMatrix.asPrintableTable(): String = listOf(this).asPrintableTable()

fun List<SavedMatrix>.asPrintableTable(): String = buildTable(
    TableColumn(OUTCOME_COLUMN_HEADER, map { it.outcome }, OUTCOME_COLUMN_SIZE),
    TableColumn(MATRIX_ID_COLUMN_HEADER, map { it.matrixId }, MATRIX_ID_COLUMN_SIZE),
    TableColumn(OUTCOME_DETAILS_COLUMN_HEADER, map { it.outcomeDetails }, OUTCOME_DETAILS_COLUMN_SIZE)
)

private const val OUTCOME_COLUMN_HEADER = "OUTCOME"
private const val OUTCOME_COLUMN_SIZE = 9
private const val MATRIX_ID_COLUMN_HEADER = "TEST_AXIS_VALUE"
private const val MATRIX_ID_COLUMN_SIZE = 24
private const val OUTCOME_DETAILS_COLUMN_HEADER = "TEST_DETAILS"
private const val OUTCOME_DETAILS_COLUMN_SIZE = 20
