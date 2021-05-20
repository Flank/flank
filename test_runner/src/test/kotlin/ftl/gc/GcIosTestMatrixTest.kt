package ftl.gc

import flank.common.isWindows
import ftl.api.TestMatrixIos
import ftl.args.IosArgs
import ftl.client.google.run.ios.executeIosTests
import ftl.ios.xctest.FIXTURES_PATH
import ftl.run.platform.ios.createIosTestConfig
import ftl.test.util.FlankTestRunner
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeFalse
import org.junit.Test
import org.junit.runner.RunWith
import java.io.StringReader

@RunWith(FlankTestRunner::class)
class GcIosTestMatrixTest {

    @After
    fun tearDown() = unmockkAll()

    @Test(expected = IllegalArgumentException::class)
    fun `build negativeShardErrors`() {
        runBlocking {
            val iosArgs = mockk<IosArgs>(relaxed = true) {
                every { otherFiles } returns emptyMap()
                every { devices } returns emptyList()
                every { resultsHistoryName } returns ""
            }

            val type = TestMatrixIos.Type.XcTest(
                xcTestGcsPath = "",
                xcodeVersion = "",
                testSpecialEntitlements = false,
                matrixGcsPath = "",
                xcTestRunFileGcsPath = ""
            )
            val config = createIosTestConfig(iosArgs)
            executeIosTests(config, listOf(type))
        }
    }

    @Test
    fun `build validArgs`() {
        runBlocking {
            assumeFalse(isWindows) // TODO enable it on #1180

            val iosArgs = mockk<IosArgs>(relaxed = true)
            every { iosArgs.testTimeout } returns "3m"
            every { iosArgs.resultsBucket } returns "/hi"
            every { iosArgs.project } returns "123"
            every { iosArgs.xctestrunFile } returns "$FIXTURES_PATH/ios/EarlGreyExample/EarlGreyExampleSwiftTests.xctestrun"

            val type = TestMatrixIos.Type.XcTest(
                xcTestGcsPath = "",
                xcodeVersion = "",
                testSpecialEntitlements = false,
                matrixGcsPath = "",
                xcTestRunFileGcsPath = ""
            )
            val config = createIosTestConfig(iosArgs)
            executeIosTests(config, listOf(type))
        }
    }

    @Test
    fun `should fill otherFiles`() {
        val iosArgs = IosArgs.load(
            StringReader(
                """
            gcloud:
              test: ./test_runner/src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/EarlGreyExample.zip
              xctestrun-file: ./test_runner/src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/EarlGreyExampleSwiftTests.xctestrun
              results-dir: test_dir
              other-files:
                com.my.app:/Documents/file.txt: local/file.txt
                /private/var/mobile/Media/file.jpg: gs://bucket/file.jpg
                """.trimIndent()
            )
        )

        val expected = mapOf(
            "com.my.app:/Documents/file.txt" to "local/file.txt",
            "/private/var/mobile/Media/file.jpg" to "gs://bucket/file.jpg"
        )
        assertEquals(expected, iosArgs.otherFiles)
    }

    @Test
    fun `should not fill otherFiles`() {
        val iosArgs = IosArgs.load(
            StringReader(
                """
            gcloud:
              test: ./test_runner/src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/EarlGreyExample.zip
              xctestrun-file: ./test_runner/src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/EarlGreyExampleSwiftTests.xctestrun
              results-dir: test_dir
                """.trimIndent()
            )
        )

        val expected = emptyMap<String, String>()
        assertEquals(expected, iosArgs.otherFiles)
    }

    @Test
    fun `should fill additional ipas`() {
        val iosArgs = IosArgs.load(
            StringReader(
                """
            gcloud:
              test: ./test_runner/src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/EarlGreyExample.zip
              xctestrun-file: ./test_runner/src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/EarlGreyExampleSwiftTests.xctestrun
              results-dir: test_dir
              additional-ipas:
                - path/to/local/file.ipa
                - gs://bucket/file.ipa
                """.trimIndent()
            )
        )

        val expected = listOf("path/to/local/file.ipa", "gs://bucket/file.ipa")
        assertEquals(expected, iosArgs.additionalIpas)
    }

    @Test
    fun `should not fill additional ipas`() {
        val iosArgs = IosArgs.load(
            StringReader(
                """
            gcloud:
              test: ./test_runner/src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/EarlGreyExample.zip
              xctestrun-file: ./test_runner/src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/EarlGreyExampleSwiftTests.xctestrun
              results-dir: test_dir
                """.trimIndent()
            )
        )

        val expected = emptyList<String>()
        assertEquals(expected, iosArgs.additionalIpas)
    }

    @Test
    fun `should fill directoriesToPull`() {
        val iosArgs = IosArgs.load(
            StringReader(
                """
            gcloud:
              test: ./test_runner/src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/EarlGreyExample.zip
              xctestrun-file: ./test_runner/src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/EarlGreyExampleSwiftTests.xctestrun
              results-dir: test_dir
              directories-to-pull:
                - test/test/test
                """.trimIndent()
            )
        )

        val expected = listOf("test/test/test")
        assertEquals(expected, iosArgs.directoriesToPull)
    }

    @Test
    fun `should not fill directoriesToPull`() {
        val iosArgs = IosArgs.load(
            StringReader(
                """
            gcloud:
              test: ./test_runner/src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/EarlGreyExample.zip
              xctestrun-file: ./test_runner/src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/EarlGreyExampleSwiftTests.xctestrun
              results-dir: test_dir
                """.trimIndent()
            )
        )

        val expected = emptyList<String>()
        assertEquals(expected, iosArgs.directoriesToPull)
    }
}
