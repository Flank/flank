package ftl.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

internal class TableColumnTest {

    @Test
    fun `Should calculate properly column width`() {
        // given
        val expectedColumnSize = 8

        // when
        val tableColumn = TableColumn("55555", listOf("1", "22", "333", "4444", "55555", "666666"), alignment = Alignment.LEFT)

        // then
        assertThat(tableColumn.columnSize).isEqualTo(expectedColumnSize)
    }

    @Test
    fun `Should calculate properly column width when header value is the longest`() {
        // given
        val expectedColumnSize = 7

        // when
        val tableColumn = TableColumn("55555", listOf("1", "22", "333", "4444"), alignment = Alignment.LEFT)

        // then
        assertThat(tableColumn.columnSize).isEqualTo(expectedColumnSize)
    }
}
