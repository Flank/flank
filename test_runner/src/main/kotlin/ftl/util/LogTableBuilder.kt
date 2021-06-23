package ftl.util

import com.google.common.annotations.VisibleForTesting
import java.util.Locale

enum class TableStyle {
    DEFAULT,
    ROW_SEPARATOR
}

data class TableColumn(
    val header: String,
    val data: List<String>,
    val dataColor: List<SystemOutColor> = listOf(),
    val columnSize: Int = ((data + header).maxByOrNull { it.length }?.length ?: 0) + DEFAULT_COLUMN_PADDING,
    val alignment: Alignment
)

enum class Alignment {
    LEFT, CENTER
}

private data class DataWithSize(
    val data: String,
    val columnSize: Int,
    val alignment: Alignment
)

private const val DEFAULT_COLUMN_PADDING = 2

@VisibleForTesting
const val TABLE_HORIZONTAL_LINE = '─'

@VisibleForTesting
const val TABLE_VERTICAL_LINE = '│'

@VisibleForTesting
const val START_TABLE_START_CHAR = '┌'

@VisibleForTesting
const val START_TABLE_MIDDLE_CHAR = '┬'

@VisibleForTesting
const val START_TABLE_END_CHAR = '┐'

@VisibleForTesting
const val MIDDLE_TABLE_START_CHAR = '├'

@VisibleForTesting
const val MIDDLE_TABLE_MIDDLE_CHAR = '┼'

@VisibleForTesting
const val MIDDLE_TABLE_END_CHAR = '┤'

@VisibleForTesting
const val END_TABLE_START_CHAR = '└'

@VisibleForTesting
const val END_TABLE_MIDDLE_CHAR = '┴'

@VisibleForTesting
const val END_TABLE_END_CHAR = '┘'

fun buildTable(vararg tableColumns: TableColumn, tableStyle: TableStyle = TableStyle.DEFAULT): String {
    val rowSizes = tableColumns.map { it.columnSize }
    val builder = StringBuilder().apply {
        startTable(rowSizes)
        tableColumns
            .map { DataWithSize(data = it.header, columnSize = it.columnSize, alignment = Alignment.CENTER) }
            .apply { appendDataRow(this) }
        rowSeparator(rowSizes)
        appendData(tableColumns, rowSizes, tableStyle)
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
    appendLine()
}

private fun StringBuilder.rowSeparator(rowSizes: List<Int>) {
    appendTableSeparator(
        startChar = MIDDLE_TABLE_START_CHAR,
        middleChar = MIDDLE_TABLE_MIDDLE_CHAR,
        endChar = MIDDLE_TABLE_END_CHAR,
        rowSizes = rowSizes
    )
    appendLine()
}

private fun StringBuilder.appendData(
    tableColumns: Array<out TableColumn>,
    rowSizes: List<Int>,
    tableStyle: TableStyle
) {
    val rowCount = (tableColumns.maxByOrNull { it.data.size } ?: tableColumns.first()).data.size
    (0 until rowCount)
        .map { rowNumber ->
            tableColumns.map {
                val color = it.dataColor.getOrNull(rowNumber) ?: SystemOutColor.DEFAULT
                val data = it.data.getOrNull(rowNumber).orEmpty()
                val columnSize = it.columnSize
                if (color == SystemOutColor.DEFAULT) DataWithSize(data, columnSize, it.alignment)
                else DataWithSize(color.applyTo(data), columnSize + color.additionalLengthWhenApplied, it.alignment)
            }
        }
        .forEachIndexed { index, list ->
            appendDataRow(list)
            if (tableStyle == TableStyle.ROW_SEPARATOR && index < rowCount - 1 && list.first().data.isNotBlank())
                rowSeparator(rowSizes)
        }
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
    data.forEach {
        append(
            when (it.alignment) {
                Alignment.LEFT -> it.leftAligned()
                Alignment.CENTER -> it.center()
            }
        )
        append(TABLE_VERTICAL_LINE)
    }
    appendLine()
}

private fun DataWithSize.leftAligned() = String.format(Locale.getDefault(), "%-${columnSize}s", " $data")

private fun DataWithSize.center() = String.format(
    "%-" + columnSize + "s",
    String.format("%" + (data.length + (columnSize - data.length) / 2) + "s", this.data)
)

inline fun TableColumn.applyColorsUsing(mapper: (String) -> SystemOutColor) = copy(dataColor = data.map(mapper))

// RESOLUTION column needs dedicated alignment logic and data pre-processing
fun TableColumn.alignToTheXMark() = copy(
    data = data.map {
        val dimensions = it.split("x")
        if (dimensions[0].length + 1 == dimensions[1].length) " $it" else it
    }
)
