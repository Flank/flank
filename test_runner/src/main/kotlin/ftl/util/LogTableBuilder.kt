package ftl.util

import com.google.common.annotations.VisibleForTesting

data class TableColumn(
    val header: String,
    val data: List<String>,
    val columnSize: Int = (data + header).maxBy { it.length }!!.length + DEFAULT_COLUMN_PADDING,
    val dataColor: List<SystemOutColor> = listOf()
)

private data class DataWithSize(
    val data: String,
    val columnSize: Int
)

private const val DEFAULT_COLUMN_PADDING = 2
@VisibleForTesting const val TABLE_HORIZONTAL_LINE = '─'
@VisibleForTesting const val TABLE_VERTICAL_LINE = '│'
@VisibleForTesting const val START_TABLE_START_CHAR = '┌'
@VisibleForTesting const val START_TABLE_MIDDLE_CHAR = '┬'
@VisibleForTesting const val START_TABLE_END_CHAR = '┐'
@VisibleForTesting const val MIDDLE_TABLE_START_CHAR = '├'
@VisibleForTesting const val MIDDLE_TABLE_MIDDLE_CHAR = '┼'
@VisibleForTesting const val MIDDLE_TABLE_END_CHAR = '┤'
@VisibleForTesting const val END_TABLE_START_CHAR = '└'
@VisibleForTesting const val END_TABLE_MIDDLE_CHAR = '┴'
@VisibleForTesting const val END_TABLE_END_CHAR = '┘'

fun buildTable(vararg tableColumns: TableColumn): String {
    val rowSizes = tableColumns.map { it.columnSize }
    val builder = StringBuilder().apply {
        startTable(rowSizes)
        tableColumns.map { DataWithSize(it.header, it.columnSize) }.apply { appendDataRow(this) }
        rowSeparator(rowSizes)
        appendData(tableColumns)
        endTable(rowSizes)
    }

    return builder.toString()
}

private fun StringBuilder.startTable(rowSizes: List<Int>) {
    appendTableSeparator(
        startChar = START_TABLE_START_CHAR,
        middleChar = START_TABLE_MIDDLE_CHAR,
        endChar = START_TABLE_END_CHAR,
        rowSizes = rowSizes
    )
    newLine()
}

private fun StringBuilder.rowSeparator(rowSizes: List<Int>) {
    appendTableSeparator(
        startChar = MIDDLE_TABLE_START_CHAR,
        middleChar = MIDDLE_TABLE_MIDDLE_CHAR,
        endChar = MIDDLE_TABLE_END_CHAR,
        rowSizes = rowSizes
    )
    newLine()
}

private fun StringBuilder.appendData(tableColumns: Array<out TableColumn>) {
    val rowCount = (tableColumns.maxBy { it.data.size } ?: tableColumns.first()).data.size

    (0 until rowCount)
        .map { rowNumber ->
            tableColumns.map {
                val color = it.dataColor.getOrNull(rowNumber) ?: SystemOutColor.DEFAULT
                val data = it.data.getOrNull(rowNumber).orEmpty()
                val columnSize = it.columnSize
                if (color == SystemOutColor.DEFAULT) DataWithSize(data, columnSize)
                else DataWithSize(color.applyTo(data), columnSize + color.additionalLengthWhenApplied)
            }
        }
        .forEach { appendDataRow(it) }
}

private fun StringBuilder.endTable(rowSizes: List<Int>) {
    appendTableSeparator(
        startChar = END_TABLE_START_CHAR,
        middleChar = END_TABLE_MIDDLE_CHAR,
        endChar = END_TABLE_END_CHAR,
        rowSizes = rowSizes
    )
}

private fun StringBuilder.appendTableSeparator(startChar: Char, middleChar: Char, endChar: Char, rowSizes: List<Int>) {
    append(startChar)
    rowSizes.forEachIndexed { index, rowSize ->
        append(TABLE_HORIZONTAL_LINE.toString().repeat(rowSize))
        append(if (rowSizes.lastIndex == index) endChar else middleChar)
    }
}

private fun StringBuilder.appendDataRow(data: List<DataWithSize>) {
    append(TABLE_VERTICAL_LINE)
    data.forEach { (data, size) ->
        append(data.center(size))
        append(TABLE_VERTICAL_LINE)
    }
    newLine()
}

private fun String.center(columnSize: Int): String? {
    return String.format(
        "%-" + columnSize + "s",
        String.format("%" + (length + (columnSize - length) / 2) + "s", this)
    )
}

private fun StringBuilder.newLine() {
    append(System.lineSeparator())
}
