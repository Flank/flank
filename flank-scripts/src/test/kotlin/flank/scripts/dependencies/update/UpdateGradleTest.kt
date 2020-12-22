package flank.scripts.dependencies.update

import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class UpdateGradleTest(private val settings: List<TestChannelSettings>) {
    @get:Rule
    val tempFolder = TemporaryFolder()

    private val testReport = File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/report.json")
    private val testGradleVersionFile =
        File("src/test/kotlin/flank/scripts/dependencies/update/testfiles/test_gradle-wrapper.properties.test")

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun params() = listOf(
            asList(
                ReleaseCandidate(version = "6.7-rc-1", isExpected = true),
                Running(version = "6.5.2"),
                Current(version = "6.6")
            ),
            asList(
                ReleaseCandidate(version = "6.6-rc-1"),
                Running(version = "6.5.3"),
                Current(version = "6.8", isExpected = true)
            ),
            asList(
                ReleaseCandidate(version = "6.7-rc-2", isExpected = true),
                Running(version = "6.7-rc-1"),
                Current(version = "6.6")
            ),
            asList(
                ReleaseCandidate(version = "6.2-rc-2"),
                Running(version = "6.5", isExpected = true),
                Current(version = "6.4")
            )
        )
    }

    @Test
    fun `Should update gradle`() {
        // given
        val expectedVersions = testGradleVersionFile.replaceGradleVersion(settings.first { it.isExpected }.version)
        val copyOfTestVersions = tempFolder.newFile("gradle-wrapper.properties").apply {
            writeText(testGradleVersionFile.replaceGradleVersion(settings.first { it.name == "running" }.version))
        }
        val preparedReport = tempFolder.newFile("report.json").apply {
            writeText(testReport.replaceChannelTemplate(settings))
        }

        // when
        preparedReport.updateGradle(tempFolder.root.absolutePath)

        // then
        assertEquals(expectedVersions, copyOfTestVersions.readText())
    }
}

private fun File.replaceChannelTemplate(settings: List<TestChannelSettings>): String {
    var result = readText().replace("\r\n", "\n")
    for (setting in settings) {
        result = result.replace(
            "\"${setting.name}\": \\{([^{}]+)}".toRegex(),
            """
    |        "${setting.name}": {
    |            "version": "${setting.version}",
    |            "reason": "",
    |            "isUpdateAvailable": false,
    |            "isFailure": false
    |        }
        """.trimMargin()
        )
    }
    return result
}

private fun asList(vararg settings: TestChannelSettings) = arrayOf(settings.toList())

private fun File.replaceGradleVersion(version: String) = readText()
    .replace("\r\n", "\n")
    .replace("6.5.1", version)

sealed class TestChannelSettings(
    val name: String,
    val version: String,
    val isExpected: Boolean
)

private class ReleaseCandidate(
    version: String,
    isExpected: Boolean = false
) : TestChannelSettings("releaseCandidate", version, isExpected)

private class Running(
    version: String,
    isExpected: Boolean = false
) : TestChannelSettings("running", version, isExpected)

private class Current(
    version: String,
    isExpected: Boolean = false
) : TestChannelSettings("current", version, isExpected)
