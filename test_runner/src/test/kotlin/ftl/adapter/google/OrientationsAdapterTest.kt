package ftl.adapter.google

import com.google.common.truth.Truth.assertThat
import com.google.testing.model.Date
import com.google.testing.model.DeviceIpBlock
import ftl.api.IpBlockList
import org.junit.Test

class OrientationsAdapterTest {

    @Test
    fun `Should properly map ip blocks`() {
        // given
        val testItem = DeviceIpBlock()
            .setBlock("block")
            .setForm("form")
            .setAddedDate(Date().setDay(1).setMonth(1).setYear(2000))
        val expected = IpBlockList.IpBlock(
            "block",
            "form",
            "2000-01-01"
        )

        // when
        val actual = listOf(testItem).toApiModel()

        // then
        assertThat(actual.blocks).isEqualTo(listOf(expected))
    }

    @Test
    fun `Should properly map ip blocks with missing fields`() {
        // given
        val testItem = DeviceIpBlock()
        val expected = IpBlockList.IpBlock(
            "[Unable to fetch]",
            "[Unable to fetch]",
            "[Unable to fetch]"
        )

        // when
        val actual = listOf(testItem).toApiModel()

        // then
        assertThat(actual.blocks).isEqualTo(listOf(expected))
    }
}
