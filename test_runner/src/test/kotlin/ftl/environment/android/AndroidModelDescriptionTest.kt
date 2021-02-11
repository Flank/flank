package ftl.environment.android

import com.google.common.truth.Truth.assertThat
import com.google.testing.model.AndroidModel
import flank.common.normalizeLineEnding
import ftl.cli.firebase.test.android.models.AndroidModelsCommand
import ftl.run.exception.FlankGeneralError
import ftl.test.util.TestHelper.getThrowable
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule

class AndroidModelDescriptionTest {

    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun `should return model with tag if any tag exists`() {
        val models = listOf(
            AndroidModel().apply {
                id = "walleye"
                codename = "walleye"
                brand = "Google"
                form = "PHYSICAL"
                formFactor = "PHONE"
                manufacturer = "Google"
                name = "Pixel 2"
                screenDensity = 420
                screenX = 1080
                screenY = 1920
                supportedAbis = listOf("arm64-v8a", "armeabi-v7a", "armeabi")
                supportedVersionIds = listOf("26", "27", "28")
                tags = listOf("default")
                thumbnailUrl =
                    "https://lh3.googleusercontent.com/j4urvb3lXTaFGZI6IzHmAjum2HQVID1OHPhDB7dOzRvXb2WscSX2RFwEEFFSYhajqRO5Yu0e6FYQ"
            }
        )

        val modelDescription = models.getDescription("walleye")
        val expected = """
        brand: Google
        codename: walleye
        form: PHYSICAL
        formFactor: PHONE
        id: walleye
        manufacturer: Google
        name: Pixel 2
        screenDensity: 420
        screenX: 1080
        screenY: 1920
        supportedAbis:
        - arm64-v8a
        - armeabi-v7a
        - armeabi
        supportedVersionIds:
        - 26
        - 27
        - 28
        tags:
        - default
        thumbnailUrl: https://lh3.googleusercontent.com/j4urvb3lXTaFGZI6IzHmAjum2HQVID1OHPhDB7dOzRvXb2WscSX2RFwEEFFSYhajqRO5Yu0e6FYQ
        """.trimIndent()
        Assert.assertEquals(expected, modelDescription)
    }

    @Test
    fun `should return model without tag if no tags`() {
        val models = listOf(
            AndroidModel().apply {
                id = "walleye"
                codename = "walleye"
                brand = "Google"
                form = "PHYSICAL"
                formFactor = "PHONE"
                manufacturer = "Google"
                name = "Pixel 2"
                screenDensity = 420
                screenX = 1080
                screenY = 1920
                supportedAbis = listOf("arm64-v8a", "armeabi-v7a", "armeabi")
                supportedVersionIds = listOf("26", "27", "28")
                thumbnailUrl =
                    "https://lh3.googleusercontent.com/j4urvb3lXTaFGZI6IzHmAjum2HQVID1OHPhDB7dOzRvXb2WscSX2RFwEEFFSYhajqRO5Yu0e6FYQ"
            }
        )

        val modelDescription = models.getDescription("walleye")
        val expected = """
        brand: Google
        codename: walleye
        form: PHYSICAL
        formFactor: PHONE
        id: walleye
        manufacturer: Google
        name: Pixel 2
        screenDensity: 420
        screenX: 1080
        screenY: 1920
        supportedAbis:
        - arm64-v8a
        - armeabi-v7a
        - armeabi
        supportedVersionIds:
        - 26
        - 27
        - 28
        thumbnailUrl: https://lh3.googleusercontent.com/j4urvb3lXTaFGZI6IzHmAjum2HQVID1OHPhDB7dOzRvXb2WscSX2RFwEEFFSYhajqRO5Yu0e6FYQ
        """.trimIndent()
        Assert.assertEquals(expected, modelDescription)
    }

    @Test(expected = FlankGeneralError::class)
    fun `should return error message if model not found and throw FlankGeneralError`() {
        val versions = listOf<AndroidModel>()
        val versionName = "test"
        val localesDescription = getThrowable { versions.getDescription(versionName) }
        val expected = "ERROR: '$versionName' is not a valid model"
        Assert.assertEquals(expected, localesDescription.message)
        throw localesDescription
    }

    @Test
    fun `should not print version information`() {
        AndroidModelsCommand().run()
        val output = systemOutRule.log.normalizeLineEnding()
        assertThat(output).doesNotContainMatch("version: .*")
    }
}
