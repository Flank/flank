package ftl.environment.ios

import com.google.api.services.testing.model.IosVersion
import ftl.test.util.TestHelper.getThrowable
import org.junit.Assert
import org.junit.Test

class IosSoftwareVersionDescriptionTest {
    @Test
    fun `should return software version with tag if any tag exists`() {
        val versionId = "11.2"
        val versions = listOf(
            IosVersion().apply {
                id = versionId
                majorVersion = 10
                minorVersion = 3
                tags = listOf("default")
                supportedXcodeVersionIds = listOf("10.2.1", "10.3", "11.0", "11.1", "11.2.1", "11.3.1")
            }
        )

        val localesDescription = versions.getDescription(versionId)
        val expected = """
            id: '$versionId'
            majorVersion: 10
            minorVersion: 3
            supportedXcodeVersionIds:
            - 10.2.1
            - 10.3
            - 11.0
            - 11.1
            - 11.2.1
            - 11.3.1
            tags:
            - default
        """.trimIndent()
        Assert.assertEquals(expected, localesDescription)
    }

    @Test
    fun `should return software version without tag not exist`() {
        val versionId = "11.2"
        val versions = listOf(
            IosVersion().apply {
                id = versionId
                majorVersion = 10
                minorVersion = 3
                tags = null
                supportedXcodeVersionIds = listOf("10.2.1", "10.3", "11.0", "11.1", "11.2.1", "11.3.1")
            }
        )

        val localesDescription = versions.getDescription(versionId)
        val expected = """
            id: '$versionId'
            majorVersion: 10
            minorVersion: 3
            supportedXcodeVersionIds:
            - 10.2.1
            - 10.3
            - 11.0
            - 11.1
            - 11.2.1
            - 11.3.1
        """.trimIndent()
        Assert.assertEquals(expected, localesDescription)
    }

    @Test
    fun `should return error message if version not found`() {
        val versions = listOf<IosVersion>()
        val versionName = "test"
        val localesDescription = getThrowable { versions.getDescription(versionName) }
        val expected = "ERROR: '$versionName' is not a valid OS version"
        Assert.assertEquals(expected, localesDescription.message)
    }
}
