package ftl.environment.ios

import ftl.api.DeviceModel
import ftl.presentation.cli.firebase.test.ios.models.describe.prepareDescription
import org.junit.Assert.assertEquals
import org.junit.Test

class IosModelDescriptionTest {
    @Test
    fun `should return model with tag if any tag exists`() {
        val models = listOf(
            DeviceModel.Ios(
                deviceCapabilities = listOf("accelerometer", "arm64"),
                formFactor = "PHONE",
                id = "iphone6s",
                name = "iPhone 6s",
                screenDensity = 326,
                screenX = 750,
                screenY = 1334,
                supportedVersionIds = listOf("10.3", "11.2"),
                tags = listOf("deprecated=10.3", "deprecated=11.2")
            )
        )

        val modelDescription = models.find { it.id == "iphone6s" }?.prepareDescription()
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
        assertEquals(expected, modelDescription)
    }

    @Test
    fun `should return model without tag if no tags`() {
        val models = listOf(
            DeviceModel.Ios(
                deviceCapabilities = listOf("accelerometer", "arm64"),
                formFactor = "PHONE",
                id = "iphone6s",
                name = "iPhone 6s",
                screenDensity = 326,
                screenX = 750,
                screenY = 1334,
                supportedVersionIds = listOf("10.3", "11.2"),
                tags = emptyList()
            )
        )

        val modelDescription = models.find { it.id == "iphone6s" }?.prepareDescription()
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
        assertEquals(expected, modelDescription)
    }
}
