package ftl.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

internal class LogTableBuilderTest {

    private val sampleColumns = arrayOf(
        TableColumn("header1", "value1", 10),
        TableColumn("header2", "value2", 15),
        TableColumn("header3", "value3", 20),
        TableColumn("header4", "value4", 21)
    )

    @Test
    fun `all table lines should have equal size`() {
        // when
        val table = buildTable(*sampleColumns)

        // then
        val tableLines = table.split(System.lineSeparator())
        tableLines
            .map { it.length }
            .zipWithNext()
            .forEach { (previous, next) ->
                assert(previous == next)
            }
    }

    @Test
    fun `table first line should contains correct characters`() {
        // given
        val acceptedCharacters =
            arrayOf(START_TABLE_START_CHAR, START_TABLE_MIDDLE_CHAR, START_TABLE_END_CHAR, TABLE_HORIZONTAL_LINE)

        // when
        val table = buildTable(*sampleColumns)

        // then
        val tableLines = table.split(System.lineSeparator())
        val firstLine = tableLines.first()
        assertThat(firstLine.first()).isEqualTo(START_TABLE_START_CHAR)
        assertThat(firstLine.contains(START_TABLE_MIDDLE_CHAR)).isTrue()
        assertThat(firstLine.last()).isEqualTo(START_TABLE_END_CHAR)
        assertThat(firstLine.contains(TABLE_HORIZONTAL_LINE)).isTrue()
        assertThat(firstLine.filterNot { acceptedCharacters.contains(it) }).isEmpty()
    }

    @Test
    fun `table separator line should contains correct characters`() {
        // given
        val acceptedCharacters =
            arrayOf(MIDDLE_TABLE_START_CHAR, MIDDLE_TABLE_MIDDLE_CHAR, MIDDLE_TABLE_END_CHAR, TABLE_HORIZONTAL_LINE)

        // when
        val table = buildTable(*sampleColumns)

        // then
        val tableLines = table.split(System.lineSeparator())
        val firstLine = tableLines[2] // 0->table start, 1->data, 2-> row separator
        assertThat(firstLine.first()).isEqualTo(MIDDLE_TABLE_START_CHAR)
        assertThat(firstLine.contains(MIDDLE_TABLE_MIDDLE_CHAR)).isTrue()
        assertThat(firstLine.last()).isEqualTo(MIDDLE_TABLE_END_CHAR)
        assertThat(firstLine.contains(TABLE_HORIZONTAL_LINE)).isTrue()
        assertThat(firstLine.filterNot { acceptedCharacters.contains(it) }).isEmpty()
    }

    @Test
    fun `table last line should contains correct characters`() {
        // given
        val acceptedCharacters =
            arrayOf(END_TABLE_START_CHAR, END_TABLE_MIDDLE_CHAR, END_TABLE_END_CHAR, TABLE_HORIZONTAL_LINE)

        // when
        val table = buildTable(*sampleColumns)

        // then
        val tableLines = table.split(System.lineSeparator())
        val firstLine = tableLines.last()
        assertThat(firstLine.first()).isEqualTo(END_TABLE_START_CHAR)
        assertThat(firstLine.contains(END_TABLE_MIDDLE_CHAR)).isTrue()
        assertThat(firstLine.last()).isEqualTo(END_TABLE_END_CHAR)
        assertThat(firstLine.contains(TABLE_HORIZONTAL_LINE)).isTrue()
        assertThat(firstLine.filterNot { acceptedCharacters.contains(it) }).isEmpty()
    }

    @Test
    fun `table column should have correct sizes`() {
        // given
        val expectedColumnSizes = sampleColumns.map { it.columnSize }

        // when
        val table = buildTable(*sampleColumns)

        // then
        val tableLines = table.split(System.lineSeparator())
        val headerLine = tableLines[1] // 0->table start, 1->header
        val dataLine = tableLines[3] // 0->table start, 1->header, 2 -> separator, 3-> data
        dataLine
            .split(TABLE_VERTICAL_LINE)
            .filter { it.isNotEmpty() }
            .forEachIndexed { index, columnContent ->
                assertThat(columnContent.length).isEqualTo(expectedColumnSizes[index])
            }
        headerLine
            .split(TABLE_VERTICAL_LINE)
            .filter { it.isNotEmpty() }
            .forEachIndexed { index, columnContent ->
                assertThat(columnContent.length).isEqualTo(expectedColumnSizes[index])
            }
    }
}
