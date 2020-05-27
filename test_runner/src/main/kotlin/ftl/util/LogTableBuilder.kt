package ftl.util

data class TableColumn(
    val header: String,
    val data: String,
    val columnSize: Int = header.length + DEFAULT_COLUMN_PADDING
)

data class DataWithSize(
    val data: String,
    val columnSize: Int
)

private const val DEFAULT_COLUMN_PADDING = 2
const val TABLE_HORIZONTAL_LINE = '─'
const val TABLE_VERTICAL_LINE = '│'
const val START_TABLE_START_CHAR = '┌'
const val START_TABLE_MIDDLE_CHAR = '┬'
const val START_TABLE_END_CHAR = '┐'
const val MIDDLE_TABLE_START_CHAR = '├'
const val MIDDLE_TABLE_MIDDLE_CHAR = '┼'
const val MIDDLE_TABLE_END_CHAR = '┤'
const val END_TABLE_START_CHAR = '└'
const val END_TABLE_MIDDLE_CHAR = '┴'
const val END_TABLE_END_CHAR = '┘'

fun buildTable(vararg tableColumns: TableColumn): String {
    val rowSizes = tableColumns.map { it.columnSize }
    val builder = StringBuilder().apply {
        startTable(rowSizes)
        tableColumns.map { DataWithSize(it.header, it.columnSize) }.apply { appendData(this) }
        rowSeparator(rowSizes)
        tableColumns.map { DataWithSize(it.data, it.columnSize) }.apply { appendData(this) }
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

private fun StringBuilder.appendData(data: List<DataWithSize>) {
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
