package ftl.reports.api

import org.junit.Assert.assertEquals
import org.junit.Test

class UtilsTest {

    @Test
    fun `should add leading 0 to single digit integer`() {
        // given
        val expectedString = "05"

        // when
        val actual = 5.twoDigitString()

        // then
        assertEquals(expectedString, actual)
    }

    @Test
    fun `should not add leading 0 to two digits integer`() {
        // given
        val expectedString = "15"

        // when
        val actual = 15.twoDigitString()

        // then
        assertEquals(expectedString, actual)
    }
}
