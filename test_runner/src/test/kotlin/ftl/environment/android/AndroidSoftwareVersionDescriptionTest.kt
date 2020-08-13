package ftl.environment.android

import com.google.api.services.testing.model.AndroidVersion
import com.google.api.services.testing.model.Date
import ftl.test.util.TestHelper.getThrowable
import org.junit.Assert
import org.junit.Test

class AndroidSoftwareVersionDescriptionTest {
    @Test
    fun `should return software version with tag if any tag exists`() {
        val versions = listOf(AndroidVersion().apply {
            id = "26"
            apiLevel = 26
            codeName = "Oreo"
            versionString = "8.0.x"
            releaseDate = Date().apply {
                day = 21
                month = 8
                year = 2017
            }
            tags = listOf("default")
        })

        val localesDescription = versions.getDescription("26")
        val expected = """
            apiLevel: 26
            codeName: Oreo
            id: '26'
            releaseDate:
              day: 21
              month: 8
              year: 2017
            tags:
            - default
            versionString: 8.0.x
            """.trimIndent()
        Assert.assertEquals(expected, localesDescription)
    }

    @Test
    fun `should return software version without tag if no tags`() {
        val versions = listOf(AndroidVersion().apply {
            id = "23"
            apiLevel = 23
            codeName = "Marshmallow"
            versionString = "6.0.x"
            releaseDate = Date().apply {
                day = 5
                month = 10
                year = 2015
            }
        })

        val localesDescription = versions.getDescription("23")
        val expected = """
            apiLevel: 23
            codeName: Marshmallow
            id: '23'
            releaseDate:
              day: 5
              month: 10
              year: 2015
            versionString: 6.0.x
            """.trimIndent()
        Assert.assertEquals(expected, localesDescription)
    }

    @Test
    fun `should return error message if version not found`() {
        val versions = listOf<AndroidVersion>()
        val versionName = "test"
        val localesDescription = getThrowable { versions.getDescription(versionName) }
        val expected = "ERROR: '$versionName' is not a valid OS version"
        Assert.assertEquals(expected, localesDescription.message)
    }
}
