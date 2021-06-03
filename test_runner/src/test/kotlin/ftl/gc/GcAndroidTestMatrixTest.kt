package ftl.gc

import com.google.testing.model.TestSetup
import ftl.api.TestMatrixAndroid.Type
import ftl.args.AndroidArgs
import ftl.client.google.run.android.executeAndroidTests
import ftl.client.google.run.android.setEnvironmentVariables
import ftl.run.platform.android.createAndroidTestConfig
import ftl.test.util.FlankTestRunner
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.StringReader

@RunWith(FlankTestRunner::class)
class GcAndroidTestMatrixTest {
    private val appApk = "../test_projects/android/apks/app-debug.apk"
    private val testApk = "../test_projects/android/apks/app-debug-androidTest.apk"

    @After
    fun tearDown() = unmockkAll()

    @Test(expected = IllegalArgumentException::class)
    fun `build negativeShardErrors`() {
        runBlocking {
            val androidArgs = mockk<AndroidArgs>(relaxed = true) {
                every { otherFiles } returns emptyMap()
                every { devices } returns emptyList()
                every { resultsHistoryName } returns ""
                every { additionalAppTestApks } returns emptyList()
                every { obbFiles } returns emptyList()
            }
            val type = Type.Instrumentation(
                appApkGcsPath = "",
                testApkGcsPath = "",
                testShards = emptyList(),
                orchestratorOption = null,
                numUniformShards = null,
                disableSharding = false,
                testRunnerClass = "",
                keepTestTargetsEmpty = false,
                testTargetsForShard = emptyList()
            )
            val config = createAndroidTestConfig(androidArgs)

            executeAndroidTests(listOf(config to type))
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `build invalidShardErrors`() {
        runBlocking {
            val androidArgs = mockk<AndroidArgs>(relaxed = true) {
                every { otherFiles } returns emptyMap()
                every { devices } returns emptyList()
                every { resultsHistoryName } returns ""
                every { additionalAppTestApks } returns emptyList()
                every { obbFiles } returns emptyList()
            }

            val type = Type.Instrumentation(
                appApkGcsPath = "",
                testApkGcsPath = "",
                testShards = listOf(listOf("")),
                orchestratorOption = null,
                numUniformShards = null,
                disableSharding = false,
                testRunnerClass = "",
                keepTestTargetsEmpty = false,
                testTargetsForShard = emptyList()
            )

            val config = createAndroidTestConfig(androidArgs)

            executeAndroidTests(listOf(config to type))
        }
    }

    @Test
    fun `build validArgs`() {
        runBlocking {

            val androidArgs = mockk<AndroidArgs>(relaxed = true) {
                every { otherFiles } returns emptyMap()
                every { devices } returns emptyList()
                every { resultsHistoryName } returns ""
                every { additionalAppTestApks } returns emptyList()
                every { obbFiles } returns emptyList()

                every { testTimeout } returns "3m"
                every { resultsBucket } returns "/hi"
                every { project } returns "123"
            }

            val type = Type.Instrumentation(
                appApkGcsPath = "",
                testApkGcsPath = "",
                testShards = emptyList(),
                orchestratorOption = null,
                numUniformShards = null,
                disableSharding = false,
                testRunnerClass = "",
                keepTestTargetsEmpty = false,
                testTargetsForShard = emptyList()
            )

            val config = createAndroidTestConfig(androidArgs)

            executeAndroidTests(listOf(config to type))
        }
    }

    @Test
    fun `should not set env variables on robo test`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          robo-directives:
            "click:button3": ""
            "ignore:button1": ""
            "ignore:button2": ""
          environment-variables:
            coverage: true
            coverageFilePath: /sdcard/
            clearPackageData: true
        """.trimIndent()
        val androidArgs = AndroidArgs.load(StringReader(yaml), null)

        val testSetup = TestSetup().setEnvironmentVariables(
            androidArgs.environmentVariables,
            Type.Robo(
                appApkGcsPath = "",
                flankRoboDirectives = emptyList(),
                roboScriptGcsPath = ""
            )
        )
        assertTrue(testSetup.environmentVariables.isEmpty())
    }

    @Test
    fun `should set env variables on instrumental test`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          robo-directives:
            "click:button3": ""
            "ignore:button1": ""
            "ignore:button2": ""
          environment-variables:
            coverage: true
            coverageFilePath: /sdcard/
            clearPackageData: true
        """.trimIndent()
        val androidArgs = AndroidArgs.load(StringReader(yaml), null)

        val testSetup = TestSetup().setEnvironmentVariables(
            androidArgs.environmentVariables,
            Type.Instrumentation(
                appApkGcsPath = "",
                testApkGcsPath = "",
                testShards = emptyList(),
                orchestratorOption = null,
                numUniformShards = null,
                disableSharding = false,
                testRunnerClass = "",
                keepTestTargetsEmpty = false,
                testTargetsForShard = emptyList()
            )
        )
        assertTrue(testSetup.environmentVariables.isNotEmpty())
    }

    @Test
    fun `should set env variables with values from test config`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          environment-variables:
            coverage: true
            coverageFilePath: /sdcard/
            clearPackageData: true
        """.trimIndent()
        val androidArgs = AndroidArgs.load(StringReader(yaml), null)

        val testSetup = TestSetup().setEnvironmentVariables(
            androidArgs.environmentVariables,
            Type.Instrumentation(
                appApkGcsPath = "",
                testApkGcsPath = "",
                testShards = emptyList(),
                orchestratorOption = null,
                numUniformShards = null,
                disableSharding = false,
                testRunnerClass = "",
                keepTestTargetsEmpty = false,
                environmentVariables = mapOf(Pair("coverageFile", "/sdcard/test.ec")),
                testTargetsForShard = emptyList()
            )
        )
        assertTrue(testSetup.environmentVariables.any { it.key == "coverageFile" && it.value == "/sdcard/test.ec" })
    }

    @Test
    fun `should set env variables and override androidArgs env variables by value from TestConfig variables`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          environment-variables:
            coverage: true
            coverageFile: /sdcard/test.ec
            clearPackageData: true
        """.trimIndent()
        val androidArgs = AndroidArgs.load(StringReader(yaml), null)

        val testSetup = TestSetup().setEnvironmentVariables(
            androidArgs.environmentVariables,
            Type.Instrumentation(
                appApkGcsPath = "",
                testApkGcsPath = "",
                testShards = emptyList(),
                orchestratorOption = null,
                numUniformShards = null,
                disableSharding = false,
                testRunnerClass = "",
                keepTestTargetsEmpty = false,
                environmentVariables = mapOf(Pair("coverageFile", "/sdcard/test_module1.ec")),
                testTargetsForShard = emptyList()
            )
        )
        assertTrue(testSetup.environmentVariables.any { it.key == "coverageFile" && it.value == "/sdcard/test_module1.ec" })
    }

    @Test
    fun `should override env variable value instead of add second with same key`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          environment-variables:
            coverage: true
            coverageFile: /sdcard/test.ec
            clearPackageData: true
        """.trimIndent()
        val androidArgs = AndroidArgs.load(StringReader(yaml), null)

        val testSetup = TestSetup().setEnvironmentVariables(
            androidArgs.environmentVariables,
            Type.Instrumentation(
                appApkGcsPath = "",
                testApkGcsPath = "",
                testShards = emptyList(),
                orchestratorOption = null,
                numUniformShards = null,
                disableSharding = false,
                testRunnerClass = "",
                keepTestTargetsEmpty = false,
                environmentVariables = mapOf(Pair("coverageFile", "/sdcard/test_module1.ec")),
                testTargetsForShard = emptyList()
            )
        )
        assertEquals(1, testSetup.environmentVariables.count { it.key == "coverageFile" })
    }
}
