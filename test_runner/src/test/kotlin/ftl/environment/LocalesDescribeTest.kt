package ftl.environment

import com.google.api.services.testing.model.Locale
import org.junit.Assert
import org.junit.Test

class LocalesDescribeTest {

    @Test
    fun `should return locales with tag if any tag exists`() {
        val locales = listOf(
            Locale().apply {
                id = "test"
                name = "name_test"
                tags = listOf("one", "second")
            }
        )

        val localesDescription = locales.getLocaleDescription("test").trim()
        val expected = """
                id: test
                name: name_test
                tags:
                - one
                - second
        """.trimIndent()
        Assert.assertEquals(expected, localesDescription)
    }

    @Test
    fun `should return locales without tag header if no tags`() {
        val locales = listOf(
            Locale().apply {
                id = "test"
                name = "name_test"
            }
        )

        val localesDescription = locales.getLocaleDescription("test").trim()
        val expected = """
                id: test
                name: name_test
        """.trimIndent()
        Assert.assertEquals(expected, localesDescription)
    }
}
