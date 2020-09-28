package ftl.environment.ios

import com.google.api.services.testing.model.IosModel
import ftl.test.util.TestHelper.getThrowable
import ftl.run.exception.FlankGeneralError
import org.junit.Assert
import org.junit.Test

class IosModelDescriptionTest {
    @Test
    fun `should return model with tag if any tag exists`() {
        val models = listOf(IosModel().apply {
            deviceCapabilities = listOf("accelerometer", "arm64")
            formFactor = "PHONE"
            id = "iphone6s"
            name = "iPhone 6s"
            screenDensity = 326
            screenX = 750
            screenY = 1334
            supportedVersionIds = listOf("10.3", "11.2")
            tags = listOf("deprecated=10.3", "deprecated=11.2")
        })

        val modelDescription = models.getDescription("iphone6s")
        val expected = """
        deviceCapabilities:
        - accelerometer
        - arm64
        formFactor: PHONE
        id: iphone6s
        name: iPhone 6s
        screenDensity: 326
        screenX: 750
        screenY: 1334
        supportedVersionIds:
        - 10.3
        - 11.2
        tags:
        - deprecated=10.3
        - deprecated=11.2
""".trimIndent()
        Assert.assertEquals(expected, modelDescription)
    }

    @Test
    fun `should return model without tag if no tags`() {
        val models = listOf(IosModel().apply {
            deviceCapabilities = listOf("accelerometer", "arm64")
            formFactor = "PHONE"
            id = "iphone6s"
            name = "iPhone 6s"
            screenDensity = 326
            screenX = 750
            screenY = 1334
            supportedVersionIds = listOf("10.3", "11.2")
        })

        val modelDescription = models.getDescription("iphone6s")
        val expected = """
        deviceCapabilities:
        - accelerometer
        - arm64
        formFactor: PHONE
        id: iphone6s
        name: iPhone 6s
        screenDensity: 326
        screenX: 750
        screenY: 1334
        supportedVersionIds:
        - 10.3
        - 11.2
""".trimIndent()
        Assert.assertEquals(expected, modelDescription)
    }

    @Test(expected = FlankGeneralError::class)
    fun `should return error message if model not found and throw FlankGeneralError`() {
        val versions = listOf<IosModel>()
        val versionName = "test"
        val localesDescription = getThrowable { versions.getDescription(versionName) }
        val expected = "ERROR: '$versionName' is not a valid model"
        Assert.assertEquals(expected, localesDescription.message)
        throw localesDescription
    }
}
